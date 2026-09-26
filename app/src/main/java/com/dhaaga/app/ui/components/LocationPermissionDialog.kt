package com.dhaaga.app.ui.components

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.LocationHelper

@Composable
fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (granted) {
            onPermissionGranted()
            onDismiss()
        } else {
            val activity = context as? Activity
            val shouldShowRationale = activity != null && (
                ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            )
            if (!shouldShowRationale) {
                isPermanentlyDenied = true
            } else {
                Toast.makeText(context, "Location permission helps verify authentic craft clusters", Toast.LENGTH_SHORT).show()
                onDismiss()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PaletteForest.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PaletteForest,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Enable Location on Dhaaga",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Verify authentic Indian artisan clusters, discover regional crafts, and auto-detect your workshop territory.",
                    fontSize = 12.5.sp,
                    color = DhaagaTextMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Feature Highlights
                LocationFeatureRow(
                    icon = Icons.Default.Verified,
                    title = "GI Craft Cluster Verification",
                    description = "Verifies your handloom/craft is produced in its recognized statutory cluster."
                )

                Spacer(modifier = Modifier.height(10.dp))

                LocationFeatureRow(
                    icon = Icons.Default.Storefront,
                    title = "Regional Marketplace Discovery",
                    description = "Highlights your crafts to buyers looking for genuine regional handlooms."
                )

                Spacer(modifier = Modifier.height(10.dp))

                LocationFeatureRow(
                    icon = Icons.Default.EditLocation,
                    title = "Zero-Effort Address Auto-fill",
                    description = "Instantly fills state, district, and village details with one tap."
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isPermanentlyDenied) {
                    Text(
                        text = "Location permission is disabled in system settings. Please grant permission in App Settings.",
                        fontSize = 11.5.sp,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(
                        onClick = { LocationHelper.openAppSettings(context) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open App Settings", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            permissionLauncher.launch(LocationHelper.REQUIRED_PERMISSIONS)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Allow Location", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("Skip for Now", fontSize = 13.sp, color = DhaagaTextMedium)
                }
            }
        }
    }
}

@Composable
private fun LocationFeatureRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAF4))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PaletteForest.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
            Text(description, fontSize = 10.5.sp, color = DhaagaTextMedium, lineHeight = 14.sp)
        }
    }
}
