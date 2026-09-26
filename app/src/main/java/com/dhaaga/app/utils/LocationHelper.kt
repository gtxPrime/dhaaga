package com.dhaaga.app.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class UserLocationInfo(
    val city: String,
    val state: String,
    val district: String = "",
    val postalCode: String = "",
    val formattedAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    val displayLocation: String
        get() = when {
            city.isNotBlank() && state.isNotBlank() -> "$city, $state"
            city.isNotBlank() -> city
            state.isNotBlank() -> state
            district.isNotBlank() -> district
            else -> "India"
        }
}

object LocationHelper {
    private const val TAG = "LocationHelper"

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    fun openLocationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open location settings: ${e.message}")
        }
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open app settings: ${e.message}")
        }
    }

    /**
     * Resolves the user's current GPS/Network location and reverse-geocodes it into city, state, and address.
     */
    suspend fun getCurrentLocation(context: Context): Result<UserLocationInfo> = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) {
            return@withContext Result.failure(SecurityException("Location permission not granted"))
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext Result.failure(Exception("Location service unavailable"))

        if (!isLocationEnabled(context)) {
            return@withContext Result.failure(Exception("GPS / Location is disabled on device"))
        }

        try {
            // 1. Try to fetch best recent last-known location
            var bestLocation: Location? = null
            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)

            for (provider in providers) {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = try {
                        locationManager.getLastKnownLocation(provider)
                    } catch (se: SecurityException) {
                        null
                    }
                    if (loc != null) {
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy || (System.currentTimeMillis() - loc.time < 60_000)) {
                            bestLocation = loc
                        }
                    }
                }
            }

            // 2. If no fresh location, request a one-shot live location fix with 8-second timeout
            if (bestLocation == null || (System.currentTimeMillis() - bestLocation.time > 120_000)) {
                val liveLocation = withTimeoutOrNull(8_000L) {
                    requestSingleUpdate(locationManager)
                }
                if (liveLocation != null) {
                    bestLocation = liveLocation
                }
            }

            val finalLocation = bestLocation ?: return@withContext Result.failure(Exception("Unable to acquire GPS fix. Please ensure location is turned on."))

            // 3. Reverse Geocode Coordinates into City, State, District
            val locationInfo = reverseGeocode(context, finalLocation.latitude, finalLocation.longitude)
            Result.success(locationInfo)
        } catch (e: SecurityException) {
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed retrieving location: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun requestSingleUpdate(locationManager: LocationManager): Location? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    try {
                        locationManager.removeUpdates(this)
                    } catch (e: Exception) {
                        // ignore
                    }
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            try {
                val provider = when {
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                    else -> null
                }

                if (provider != null) {
                    locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                } else if (continuation.isActive) {
                    continuation.resume(null)
                }
            } catch (e: SecurityException) {
                if (continuation.isActive) continuation.resume(null)
            }

            continuation.invokeOnCancellation {
                try {
                    locationManager.removeUpdates(listener)
                } catch (e: Exception) {
                    // ignore
                }
            }
        }

    private suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): UserLocationInfo =
        withContext(Dispatchers.IO) {
            var city = ""
            var state = ""
            var district = ""
            var postalCode = ""
            var formattedAddress = ""

            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale("en", "IN"))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val addresses = suspendCancellableCoroutine<List<Address>> { cont ->
                            geocoder.getFromLocation(latitude, longitude, 1) { addrs ->
                                if (cont.isActive) cont.resume(addrs)
                            }
                        }
                        val addr = addresses.firstOrNull()
                        if (addr != null) {
                            city = addr.locality ?: addr.subAdminArea ?: ""
                            state = addr.adminArea ?: ""
                            district = addr.subAdminArea ?: ""
                            postalCode = addr.postalCode ?: ""
                            formattedAddress = addr.getAddressLine(0) ?: ""
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        val addr = addresses?.firstOrNull()
                        if (addr != null) {
                            city = addr.locality ?: addr.subAdminArea ?: ""
                            state = addr.adminArea ?: ""
                            district = addr.subAdminArea ?: ""
                            postalCode = addr.postalCode ?: ""
                            formattedAddress = addr.getAddressLine(0) ?: ""
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Geocoder reverse lookup failed: ${e.message}")
            }

            UserLocationInfo(
                city = city.ifBlank { "Local Cluster" },
                state = state.ifBlank { "India" },
                district = district,
                postalCode = postalCode,
                formattedAddress = formattedAddress,
                latitude = latitude,
                longitude = longitude
            )
        }
}
