package com.dhaaga.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.data.model.CartItemModel
import com.dhaaga.app.data.model.OrderModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.utils.AppLanguageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        private const val TAG = "DhaagaApp"
        private const val PREFS_NAME = "dhaaga_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_VILLAGE = "user_village"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_STATE = "user_state"
        private const val KEY_USER_UID = "user_uid"
        private const val KEY_USER_PHOTO = "user_photo_url"
    }

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    // Permanent accounts registry on device (never wiped by logout)
    private val accountsRegistry = application.getSharedPreferences("dhaaga_registered_accounts", Context.MODE_PRIVATE)
    // Permanent crafts registry on device (never wiped by logout)
    private val craftsRegistry = application.getSharedPreferences("dhaaga_permanent_crafts", Context.MODE_PRIVATE)

    // Firebase instances
    private val firebaseAuth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w(TAG, "FirebaseAuth initialization warning: ${e.message}")
        null
    }

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.w(TAG, "FirebaseFirestore initialization warning: ${e.message}")
        null
    }

    // Auth state initialized from SharedPreferences / Firebase session
    private val _currentUser = MutableStateFlow<UserModel?>(loadSavedUser())
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    // Real Artisan Crafts uploaded to Firestore (scoped strictly to the logged-in seller)
    private val _sellerProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val sellerProducts: StateFlow<List<ProductModel>> = _sellerProducts.asStateFlow()
    private var sellerProductsListener: com.google.firebase.firestore.ListenerRegistration? = null

    // Real-time orders for the logged-in artisan (seller side)
    private val _artisanOrders = MutableStateFlow<List<OrderModel>>(loadSavedOrders())
    val artisanOrders: StateFlow<List<OrderModel>> = _artisanOrders.asStateFlow()
    private var sellerOrdersListener: com.google.firebase.firestore.ListenerRegistration? = null

    // Real-time orders placed BY the logged-in buyer
    private val _buyerOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val buyerOrders: StateFlow<List<OrderModel>> = _buyerOrders.asStateFlow()
    private var buyerOrdersListener: com.google.firebase.firestore.ListenerRegistration? = null

    // General marketplace products for buyer tab
    private val _products = MutableStateFlow(MockData.mockProducts)
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(loadSavedWishlist())
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    // Clean cart: restored from local cache so items survive app restarts
    private val _cart = MutableStateFlow<List<CartItemModel>>(loadSavedCart())
    val cart: StateFlow<List<CartItemModel>> = _cart.asStateFlow()

    // Loading state for Firestore initial sync
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Real-time Cart Animation Event Trigger (Timestamp) & Last Added Product
    private val _cartAnimationEvent = MutableStateFlow<Long>(0L)
    val cartAnimationEvent: StateFlow<Long> = _cartAnimationEvent.asStateFlow()

    private val _lastAddedProduct = MutableStateFlow<ProductModel?>(null)
    val lastAddedProduct: StateFlow<ProductModel?> = _lastAddedProduct.asStateFlow()

    val cartTotal: Long get() = _cart.value.sumOf { it.totalPrice }
    val cartCount: Int get() = _cart.value.size

    init {
        Log.i(TAG, "Dhaaga ViewModel initialized. Initial Session logged in = ${_currentUser.value != null}")

        // 0. Ensure Gemini AI API key is initialized in preferences
        com.dhaaga.app.data.repository.GeminiAIService.getApiKey(application)

        // 1. Load locally cached artisan crafts and prepend to marketplace
        val localCrafts = loadLocalArtisanCrafts()
        if (localCrafts.isNotEmpty()) {
            _sellerProducts.value = localCrafts
            val localIds = localCrafts.map { it.productId }.toSet()
            _products.value = localCrafts + MockData.mockProducts.filter { it.productId !in localIds }
            Log.i(TAG, "📦 Loaded ${localCrafts.size} artisan crafts into Home Screen & Listings")
        }

        // 2. Sync any offline or local crafts up to Firestore cloud
        syncLocalCraftsToCloud()

        // 3. Real-time sync for all marketplace products from Firestore
        listenToAllMarketplaceProducts()

        val initialUser = _currentUser.value
        if (initialUser != null) {
            refreshUserFromFirestore(initialUser.uid)
            if (initialUser.isSeller) {
                refreshSellerProducts(initialUser.uid)
                listenToSellerOrders(initialUser.uid)
            } else {
                listenToBuyerOrders(initialUser.uid)
            }
        } else {
            // No user logged in — loading is done immediately
            _isLoading.value = false
        }
    }

    private fun loadSavedUser(): UserModel? {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null

        val firebaseUid = firebaseAuth?.currentUser?.uid ?: prefs.getString(KEY_USER_UID, "") ?: ""
        val role = prefs.getString(KEY_USER_ROLE, "buyer") ?: "buyer"
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val village = prefs.getString(KEY_USER_VILLAGE, "") ?: ""
        val state = prefs.getString(KEY_USER_STATE, "") ?: ""
        val phone = prefs.getString(KEY_USER_PHONE, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val photo = prefs.getString(KEY_USER_PHOTO, "") ?: ""
        val effectiveUid = if (firebaseUid.isNotEmpty()) firebaseUid else prefs.getString(KEY_USER_UID, "user_${System.currentTimeMillis()}") ?: ""

        if (name.isEmpty() && phone.isEmpty() && email.isEmpty() && effectiveUid.isEmpty()) return null

        return UserModel(
            uid = effectiveUid,
            phoneNumber = phone,
            email = email,
            name = name,
            role = role,
            village = village,
            state = state,
            profilePhotoUrl = photo
        )
    }

    val loggedInPhone: String
        get() {
            fun clean(p: String): String = p.trim().removePrefix("+91").removePrefix("+").trim()
            val uPhone = _currentUser.value?.phoneNumber?.trim()?.ifBlank { null }
            if (uPhone != null) return clean(uPhone)
            val prefPhone = prefs.getString(KEY_USER_PHONE, null)?.trim()?.ifBlank { null }
            if (prefPhone != null) return clean(prefPhone)
            val fbPhone = firebaseAuth?.currentUser?.phoneNumber?.trim()?.ifBlank { null }
            if (fbPhone != null) return clean(fbPhone)
            val uid = _currentUser.value?.uid ?: ""
            if (uid.startsWith("artisan_") || uid.startsWith("buyer_")) {
                val extracted = uid.substringAfter("_")
                if (extracted.length >= 10) return clean(extracted)
            }
            return "7668439019"
        }

    fun refreshUserFromFirestore(uid: String) {
        if (uid.isEmpty()) return
        firestore?.collection("users")?.document(uid)?.get()
            ?.addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val firestoreUser = doc.toObject(UserModel::class.java)
                    if (firestoreUser != null) {
                        val finalPhone = firestoreUser.phoneNumber.ifBlank {
                            _currentUser.value?.phoneNumber?.ifBlank { prefs.getString(KEY_USER_PHONE, "") ?: "" } ?: ""
                        }
                        val finalUser = firestoreUser.copy(phoneNumber = finalPhone)
                        _currentUser.value = finalUser
                        Log.i(TAG, "🔄 Refreshed user profile from Firestore: ${finalUser.name} (${finalUser.role}, Phone=$finalPhone)")
                    }
                }
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Failed to refresh user from Firestore: ${e.message}")
            }
    }

    fun checkExistingUserByUid(uid: String, onResult: (UserModel?) -> Unit) {
        if (uid.isBlank()) {
            onResult(null)
            return
        }

        // 1. Check local permanent registry first
        val localRole = accountsRegistry.getString("role_uid_$uid", null)
        val localName = accountsRegistry.getString("name_uid_$uid", null)
        val localPhone = accountsRegistry.getString("phone_uid_$uid", "") ?: ""
        val localEmail = accountsRegistry.getString("email_uid_$uid", "") ?: ""
        val localVillage = accountsRegistry.getString("village_uid_$uid", "") ?: ""
        val localState = accountsRegistry.getString("state_uid_$uid", "") ?: ""
        val localPhoto = accountsRegistry.getString("photo_uid_$uid", "") ?: ""

        if (!localRole.isNullOrBlank() && !localName.isNullOrBlank()) {
            val localUser = UserModel(
                uid = uid,
                phoneNumber = localPhone,
                email = localEmail,
                name = localName,
                role = localRole,
                village = localVillage,
                state = localState,
                profilePhotoUrl = localPhoto
            )
            Log.i(TAG, "⚡ Instant permanent registry found for UID $uid: ${localUser.name} (Role: ${localUser.role})")
            onResult(localUser)
            return
        }

        // 2. Query Firestore users collection by document ID (uid)
        if (firestore == null) {
            onResult(null)
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val existing = doc.toObject(UserModel::class.java)
                    if (existing != null && existing.name.isNotBlank() && existing.role.isNotBlank()) {
                        Log.i(TAG, "🔍 Existing user found in Firestore by UID $uid: ${existing.name}, locked role: ${existing.role}")
                        accountsRegistry.edit()
                            .putString("role_uid_$uid", existing.role)
                            .putString("name_uid_$uid", existing.name)
                            .putString("phone_uid_$uid", existing.phoneNumber)
                            .putString("email_uid_$uid", existing.email)
                            .putString("village_uid_$uid", existing.village)
                            .putString("state_uid_$uid", existing.state)
                            .putString("photo_uid_$uid", existing.profilePhotoUrl)
                            .apply()
                        onResult(existing)
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking user by UID: ${e.message}")
                onResult(null)
            }
    }

    /**
     * Checks permanent accounts registry and Firestore users collection to see if an account already exists.
     * Prevents account type switching if number already exists!
     */
    fun checkExistingUserByPhone(phone: String, onResult: (UserModel?) -> Unit) {
        val sanitized = phone.trim().removePrefix("+91").removePrefix("+").trim()
        val formatted = "+91$sanitized"

        // 1. Check permanent local accounts registry first (instant and immune to network/permission issues)
        val localRole = accountsRegistry.getString("role_$sanitized", null)
        val localName = accountsRegistry.getString("name_$sanitized", null)
        val localUid = accountsRegistry.getString("uid_$sanitized", null)
        val localVillage = accountsRegistry.getString("village_$sanitized", "") ?: ""
        val localState = accountsRegistry.getString("state_$sanitized", "") ?: ""
        val localPhone = accountsRegistry.getString("phone_$sanitized", formatted) ?: formatted

        if (!localRole.isNullOrBlank() && !localUid.isNullOrBlank()) {
            val localUser = UserModel(
                uid = localUid,
                phoneNumber = localPhone,
                name = localName ?: "",
                role = localRole,
                village = localVillage,
                state = localState
            )
            Log.i(TAG, "⚡ Instant permanent registry found for $sanitized: ${localUser.name} (Role: ${localUser.role})")
            onResult(localUser)
            return
        }

        // 2. Query Firestore if not found in local registry
        if (firestore == null) {
            onResult(null)
            return
        }

        firestore.collection("users")
            .whereEqualTo("phoneNumber", formatted)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val userDoc = snapshot.documents.first()
                    val existing = userDoc.toObject(UserModel::class.java)
                    Log.i(TAG, "🔍 Existing user found in Firestore by phone $formatted: ${existing?.name}, locked role: ${existing?.role}")
                    if (existing != null) {
                        accountsRegistry.edit()
                            .putString("role_$sanitized", existing.role)
                            .putString("name_$sanitized", existing.name)
                            .putString("uid_$sanitized", existing.uid)
                            .putString("village_$sanitized", existing.village)
                            .putString("state_$sanitized", existing.state)
                            .putString("phone_$sanitized", existing.phoneNumber)
                            .apply()
                    }
                    onResult(existing)
                } else {
                    // Also check with bare 10-digit phone
                    firestore.collection("users")
                        .whereEqualTo("phoneNumber", sanitized)
                        .get()
                        .addOnSuccessListener { snap2 ->
                            if (snap2 != null && !snap2.isEmpty) {
                                val userDoc = snap2.documents.first()
                                val existing = userDoc.toObject(UserModel::class.java)
                                if (existing != null) {
                                    accountsRegistry.edit()
                                        .putString("role_$sanitized", existing.role)
                                        .putString("name_$sanitized", existing.name)
                                        .putString("uid_$sanitized", existing.uid)
                                        .putString("village_$sanitized", existing.village)
                                        .putString("state_$sanitized", existing.state)
                                        .putString("phone_$sanitized", existing.phoneNumber)
                                        .apply()
                                }
                                onResult(existing)
                            } else {
                                onResult(null)
                            }
                        }
                        .addOnFailureListener { onResult(null) }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking user by phone: ${e.message}")
                onResult(null)
            }
    }

    /**
     * Uploads any crafts in the local permanent cache to Firestore cloud so they are never lost.
     */
    fun syncLocalCraftsToCloud() {
        val local = loadLocalArtisanCrafts()
        if (local.isEmpty() || firestore == null) return
        viewModelScope.launch {
            local.forEach { craft ->
                firestore.collection("products").document(craft.productId).set(craft)
                    .addOnSuccessListener {
                        Log.i(TAG, "☁️ Cloud verified & synced craft: ${craft.productId} (${craft.titleEn})")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Cloud sync notice for ${craft.productId}: ${e.message}")
                    }
            }
        }
    }

    /**
     * Attaches a real-time listener to load crafts uploaded by the logged-in artisan,
     * merging with local permanent cache and syncing bidirectionally with Firestore cloud.
     */
    fun refreshSellerProducts(sellerId: String) {
        sellerProductsListener?.remove()
        // 1. Immediately ensure local permanent crafts are loaded into memory
        val local = loadLocalArtisanCrafts()
        if (local.isNotEmpty()) {
            _sellerProducts.value = local
            val localIds = local.map { it.productId }.toSet()
            _products.value = local + _products.value.filter { it.productId !in localIds }
        }

        // 2. Synchronize any local crafts up to Firestore cloud
        syncLocalCraftsToCloud()

        if (firestore == null) return

        val sanitizedPhone = _currentUser.value?.phoneNumber?.trim()?.removePrefix("+91")?.removePrefix("+")?.trim() ?: ""
        val sellerName = _currentUser.value?.name ?: ""

        Log.i(TAG, "📡 Listening to real Firestore cloud listings for seller: $sellerId (Phone: $sanitizedPhone)")
        sellerProductsListener = firestore.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore seller products listener note: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val allCloud = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ProductModel::class.java)
                        } catch (e: Exception) {
                            Log.w(TAG, "Could not parse cloud product: ${doc.id}")
                            null
                        }
                    }

                    // Identify cloud crafts belonging to this artisan by UID, phone number, name, or default seller
                    val artisanCloudCrafts = allCloud.filter { p ->
                        p.sellerId == sellerId ||
                        (sanitizedPhone.isNotEmpty() && p.sellerId.contains(sanitizedPhone)) ||
                        (sellerName.isNotBlank() && p.sellerName.equals(sellerName, ignoreCase = true)) ||
                        p.sellerId == "seller_default"
                    }

                    val currentLocal = loadLocalArtisanCrafts()
                    // Merge cloud crafts and local crafts (cloud takes precedence for freshest updates)
                    val merged = (artisanCloudCrafts + currentLocal).distinctBy { it.productId }
                    _sellerProducts.value = merged
                    saveLocalArtisanCrafts(merged)

                    val mergedIds = merged.map { it.productId }.toSet()
                    _products.value = merged + _products.value.filter { it.productId !in mergedIds }
                    Log.i(TAG, "☁️ Cloud Synced: ${artisanCloudCrafts.size} seller crafts from cloud, total active: ${merged.size}")
                }
            }
    }

    /**
     * Publishes or updates a craft directly to Firestore, updates Seller Listings ("My Crafts"),
     * and immediately displays it at the top of the Home Screen marketplace feed.
     */
    fun addUploadedProduct(product: ProductModel) {
        // 1. Update Seller Listings ("My Crafts")
        val currentSellerList = _sellerProducts.value.toMutableList()
        val existingIndex = currentSellerList.indexOfFirst { it.productId == product.productId }
        if (existingIndex >= 0) {
            currentSellerList[existingIndex] = product
        } else {
            currentSellerList.add(0, product)
        }
        _sellerProducts.value = currentSellerList

        // 2. Also update General Marketplace on Home/Explore Screen
        val currentMarketplace = _products.value.toMutableList()
        val existingMarketIndex = currentMarketplace.indexOfFirst { it.productId == product.productId }
        if (existingMarketIndex >= 0) {
            currentMarketplace[existingMarketIndex] = product
        } else {
            currentMarketplace.add(0, product)
        }
        _products.value = currentMarketplace

        // 3. Cache locally to permanent storage
        saveLocalArtisanCrafts(currentSellerList)

        // 4. Persist to Firestore Cloud
        firestore?.collection("products")?.document(product.productId)?.set(product)
            ?.addOnSuccessListener {
                Log.i(TAG, "✅ Craft ${product.productId} successfully saved in Cloud Firestore & live on Home Screen")
            }
            ?.addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to save craft to Firestore: ${e.message}")
            }
    }

    fun deleteUploadedProduct(productId: String) {
        _sellerProducts.value = _sellerProducts.value.filter { it.productId != productId }
        _products.value = _products.value.filter { it.productId != productId }
        saveLocalArtisanCrafts(_sellerProducts.value)
        firestore?.collection("products")?.document(productId)?.delete()
            ?.addOnSuccessListener {
                Log.i(TAG, "🗑️ Craft $productId removed from Firestore & Home Screen")
            }
    }

    private fun listenToAllMarketplaceProducts() {
        firestore?.collection("products")
            ?.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore marketplace products listener: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val firestoreProducts = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ProductModel::class.java)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (firestoreProducts.isNotEmpty()) {
                        val local = loadLocalArtisanCrafts()
                        val combinedMarketplace = (firestoreProducts + local).distinctBy { it.productId }
                        val firestoreIds = combinedMarketplace.map { it.productId }.toSet()
                        _products.value = combinedMarketplace + MockData.mockProducts.filter { it.productId !in firestoreIds }

                        // If current user is a seller, also ensure their own crafts stay synced from this cloud stream
                        val u = _currentUser.value
                        if (u != null && u.isSeller) {
                            val sanitizedPhone = u.phoneNumber.trim().removePrefix("+91").removePrefix("+").trim()
                            val sellerCrafts = firestoreProducts.filter { p ->
                                p.sellerId == u.uid ||
                                (sanitizedPhone.isNotEmpty() && p.sellerId.contains(sanitizedPhone)) ||
                                (u.name.isNotBlank() && p.sellerName.equals(u.name, ignoreCase = true)) ||
                                p.sellerId == "seller_default"
                            }
                            if (sellerCrafts.isNotEmpty()) {
                                val mergedSeller = (sellerCrafts + _sellerProducts.value).distinctBy { it.productId }
                                _sellerProducts.value = mergedSeller
                                saveLocalArtisanCrafts(mergedSeller)
                            }
                        }

                        Log.i(TAG, "✨ Home Screen & Marketplace updated with ${firestoreProducts.size} live crafts from Cloud Firestore!")
                    }
                }
            }
    }

    private fun saveLocalArtisanCrafts(list: List<ProductModel>) {
        try {
            val array = JSONArray()
            list.forEach { p ->
                val obj = JSONObject()
                obj.put("productId", p.productId)
                obj.put("sellerId", p.sellerId)
                obj.put("sellerName", p.sellerName)
                obj.put("sellerVillage", p.sellerVillage)
                obj.put("titleEn", p.titleEn)
                obj.put("titleHi", p.titleHi)
                obj.put("descriptionEn", p.descriptionEn)
                obj.put("descriptionHi", p.descriptionHi)
                obj.put("craftType", p.craftType)
                obj.put("material", p.material)
                obj.put("sizeCm", p.sizeCm)
                obj.put("technique", p.technique)
                obj.put("region", p.region)
                obj.put("priceListed", p.priceListed)
                obj.put("stockQuantity", p.stockQuantity)
                obj.put("status", p.status)
                if (p.salePricePaise != null) obj.put("salePricePaise", p.salePricePaise)
                obj.put("hasDiscount", p.hasDiscount)
                obj.put("discountType", p.discountType)
                obj.put("discountValue", p.discountValue)
                if (p.couponCode != null) obj.put("couponCode", p.couponCode)
                if (p.couponExpiryTimestamp != null) obj.put("couponExpiryTimestamp", p.couponExpiryTimestamp)
                obj.put("couponUsageLimit", p.couponUsageLimit)
                obj.put("couponUsageCount", p.couponUsageCount)
                if (p.giTag != null) obj.put("giTag", p.giTag)
                obj.put("giVerified", p.giVerified)
                val imgArray = JSONArray()
                p.imageUrls.forEach { imgArray.put(it) }
                obj.put("imageUrls", imgArray)
                array.put(obj)
            }
            val jsonStr = array.toString()
            // Save to BOTH permanent crafts registry and standard prefs
            craftsRegistry.edit().putString("local_artisan_crafts_cache", jsonStr).apply()
            prefs.edit().putString("local_artisan_crafts_cache", jsonStr).apply()
        } catch (e: Exception) {
            Log.w(TAG, "Failed saving local crafts: ${e.message}")
        }
    }

    private fun loadLocalArtisanCrafts(): List<ProductModel> {
        val json = craftsRegistry.getString("local_artisan_crafts_cache", null)
            ?: prefs.getString("local_artisan_crafts_cache", null)
            ?: return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<ProductModel>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val imgUrls = mutableListOf<String>()
                val imgArray = obj.optJSONArray("imageUrls")
                if (imgArray != null) {
                    for (j in 0 until imgArray.length()) {
                        imgUrls.add(imgArray.getString(j))
                    }
                }
                val salePrice = if (obj.has("salePricePaise")) obj.optLong("salePricePaise") else null
                val couponCode = if (obj.has("couponCode")) obj.optString("couponCode") else null
                val couponExpiry = if (obj.has("couponExpiryTimestamp")) obj.optLong("couponExpiryTimestamp") else null

                list.add(
                    ProductModel(
                        productId = obj.optString("productId"),
                        sellerId = obj.optString("sellerId"),
                        sellerName = obj.optString("sellerName"),
                        sellerVillage = obj.optString("sellerVillage"),
                        titleEn = obj.optString("titleEn"),
                        titleHi = obj.optString("titleHi"),
                        descriptionEn = obj.optString("descriptionEn"),
                        descriptionHi = obj.optString("descriptionHi"),
                        craftType = obj.optString("craftType"),
                        material = obj.optString("material"),
                        sizeCm = obj.optString("sizeCm"),
                        technique = obj.optString("technique"),
                        region = obj.optString("region"),
                        priceListed = obj.optLong("priceListed"),
                        stockQuantity = obj.optInt("stockQuantity", 1),
                        status = obj.optString("status", "active"),
                        imageUrls = imgUrls,
                        salePricePaise = salePrice,
                        hasDiscount = obj.optBoolean("hasDiscount", false),
                        discountType = obj.optString("discountType", "percentage"),
                        discountValue = obj.optLong("discountValue", 0L),
                        couponCode = couponCode,
                        couponExpiryTimestamp = couponExpiry,
                        couponUsageLimit = obj.optInt("couponUsageLimit", 0),
                        couponUsageCount = obj.optInt("couponUsageCount", 0),
                        giTag = if (obj.has("giTag")) obj.optString("giTag") else null,
                        giVerified = obj.optBoolean("giVerified", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loginAs(user: UserModel) {
        val firebaseUid = firebaseAuth?.currentUser?.uid
        val effectiveUser = if (!firebaseUid.isNullOrEmpty() && (user.uid.isEmpty() || user.uid.startsWith("mock_") || user.uid.startsWith("user_") || user.uid.startsWith("seller") || user.uid.startsWith("buyer"))) {
            user.copy(uid = firebaseUid)
        } else {
            user
        }

        Log.i(TAG, "🔑 User logged in & saved to session: ${effectiveUser.name} (${effectiveUser.role}, UID=${effectiveUser.uid}, Phone=${effectiveUser.phoneNumber}, Email=${effectiveUser.email})")
        _currentUser.value = effectiveUser
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_UID, effectiveUser.uid)
            .putString(KEY_USER_NAME, effectiveUser.name)
            .putString(KEY_USER_ROLE, effectiveUser.role)
            .putString(KEY_USER_VILLAGE, effectiveUser.village)
            .putString(KEY_USER_STATE, effectiveUser.state)
            .putString(KEY_USER_PHONE, effectiveUser.phoneNumber)
            .putString(KEY_USER_EMAIL, effectiveUser.email)
            .putString(KEY_USER_PHOTO, effectiveUser.profilePhotoUrl)
            .apply()

        // Persist to permanent accounts registry (never deleted by logout)
        if (effectiveUser.uid.isNotEmpty()) {
            accountsRegistry.edit()
                .putString("role_uid_${effectiveUser.uid}", effectiveUser.role)
                .putString("name_uid_${effectiveUser.uid}", effectiveUser.name)
                .putString("phone_uid_${effectiveUser.uid}", effectiveUser.phoneNumber)
                .putString("email_uid_${effectiveUser.uid}", effectiveUser.email)
                .putString("village_uid_${effectiveUser.uid}", effectiveUser.village)
                .putString("state_uid_${effectiveUser.uid}", effectiveUser.state)
                .putString("photo_uid_${effectiveUser.uid}", effectiveUser.profilePhotoUrl)
                .apply()
        }

        val sanitizedPhone = effectiveUser.phoneNumber.trim().removePrefix("+91").removePrefix("+").trim()
        if (sanitizedPhone.isNotEmpty()) {
            accountsRegistry.edit()
                .putString("role_$sanitizedPhone", effectiveUser.role)
                .putString("name_$sanitizedPhone", effectiveUser.name)
                .putString("uid_$sanitizedPhone", effectiveUser.uid)
                .putString("village_$sanitizedPhone", effectiveUser.village)
                .putString("state_$sanitizedPhone", effectiveUser.state)
                .putString("phone_$sanitizedPhone", effectiveUser.phoneNumber)
                .apply()
            Log.i(TAG, "💾 Saved account in permanent registry for $sanitizedPhone: ${effectiveUser.name} (${effectiveUser.role})")
        }

        if (effectiveUser.isSeller) {
            // Immediately restore crafts into state upon seller login
            val localCrafts = loadLocalArtisanCrafts()
            if (localCrafts.isNotEmpty()) {
                _sellerProducts.value = localCrafts
                val localIds = localCrafts.map { it.productId }.toSet()
                _products.value = localCrafts + _products.value.filter { it.productId !in localIds }
            }
            refreshSellerProducts(effectiveUser.uid)
            listenToSellerOrders(effectiveUser.uid)
        } else {
            // Buyer: attach real-time listener for their placed orders
            listenToBuyerOrders(effectiveUser.uid)
        }

        // Sync user profile to Firestore
        viewModelScope.launch {
            try {
                if (effectiveUser.uid.isNotEmpty()) {
                    firestore?.collection("users")?.document(effectiveUser.uid)?.set(effectiveUser)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Firestore sync note: ${e.message}")
            }
        }
    }

    fun logout() {
        Log.i(TAG, "🚪 User logged out: ${_currentUser.value?.name}")
        sellerProductsListener?.remove()
        sellerProductsListener = null
        sellerOrdersListener?.remove()
        sellerOrdersListener = null
        buyerOrdersListener?.remove()
        buyerOrdersListener = null
        // DO NOT wipe _sellerProducts or local crafts! They belong to this device's artisan.
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase sign out warning: ${e.message}")
        }
        _currentUser.value = null
        _cart.value = emptyList()
        _wishlist.value = emptySet()
        _buyerOrders.value = emptyList()
        // Clear buyer-specific prefs (cart/wishlist belong to the user session)
        prefs.edit()
            .remove(KEY_IS_LOGGED_IN)
            .remove(KEY_USER_UID)
            .remove(KEY_USER_ROLE)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_VILLAGE)
            .remove(KEY_USER_STATE)
            .remove(KEY_USER_PHONE)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_PHOTO)
            .remove("saved_wishlist_json")
            .remove("saved_cart_json")
            .apply()
    }

    fun consumeCartAnimationEvent() {
        _cartAnimationEvent.value = 0L
    }

    fun toggleWishlist(productId: String) {
        val current = _wishlist.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
            Log.d(TAG, "Removed product from wishlist: $productId")
        } else {
            current.add(productId)
            Log.d(TAG, "Added product to wishlist: $productId")
        }
        _wishlist.value = current
        saveWishlist(current)
    }

    fun isWishlisted(productId: String) = _wishlist.value.contains(productId)

    fun addToCart(product: ProductModel, quantity: Int = 1) {
        val existing = _cart.value.indexOfFirst { it.productId == product.productId }
        if (existing >= 0) {
            Log.i(TAG, "🛒 Item already in cart: ${product.titleEn}, not duplicating")
            _lastAddedProduct.value = product
            _cartAnimationEvent.value = System.currentTimeMillis()
            return
        }
        val updated = _cart.value.toMutableList()
        updated.add(CartItemModel(
            productId = product.productId,
            productTitle = product.titleEn,
            productImageUrl = product.primaryImageUrl,
            sellerName = product.sellerName,
            unitPrice = product.priceListed,
            quantity = 1
        ))
        _cart.value = updated
        _lastAddedProduct.value = product
        _cartAnimationEvent.value = System.currentTimeMillis()
        saveCart(updated)
        Log.d(TAG, "Current Cart Total: ₹${cartTotal / 100} across ${_cart.value.size} items")
    }

    fun removeFromCart(productId: String) {
        Log.i(TAG, "🛒 Removed product $productId from cart")
        val updated = _cart.value.filter { it.productId != productId }.toMutableList()
        _cart.value = updated
        saveCart(updated)
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) { removeFromCart(productId); return }
        val updated = _cart.value.toMutableList()
        val idx = updated.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            updated[idx] = updated[idx].copy(quantity = quantity)
            Log.d(TAG, "🛒 Cart item $productId quantity updated to $quantity")
        }
        _cart.value = updated
        saveCart(updated)
    }

    fun clearCart() {
        _cart.value = mutableListOf()
        saveCart(emptyList())
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Multilingual Localization Support across all 22 Indian Languages
    // ─────────────────────────────────────────────────────────────────────────────
    private val _selectedLanguage = MutableStateFlow(prefs.getString("selected_language", "en") ?: "en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun setLanguage(code: String) {
        _selectedLanguage.value = code
        prefs.edit().putString("selected_language", code).apply()
        Log.i(TAG, "🌐 App language updated to: $code (${AppLanguageManager.getLanguageName(code)})")
    }

    fun tr(key: String, fallback: String = ""): String {
        return AppLanguageManager.translate(key, _selectedLanguage.value, fallback)
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Dynamic Artisan Orders & Earnings Dashboard
    // ─────────────────────────────────────────────────────────────────────────────

    val dashboardEarningsPaise: Long get() = _artisanOrders.value.sumOf { it.sellerPayout }
    val dashboardOrdersCount: Int get() = _artisanOrders.value.size
    val dashboardReadyToShipCount: Int get() = _artisanOrders.value.count { it.status == "confirmed" || it.status == "packed" }

    fun adjustProductStock(productId: String, deltaQuantity: Int) {
        // 1. Update _products (Buyer marketplace feed & Product Details)
        val updatedFeed = _products.value.map { prod ->
            if (prod.productId == productId) {
                val newStock = (prod.stockQuantity + deltaQuantity).coerceAtLeast(0)
                val newStatus = if (newStock <= 0) "sold" else if (prod.status == "sold") "active" else prod.status
                prod.copy(stockQuantity = newStock, status = newStatus)
            } else {
                prod
            }
        }
        _products.value = updatedFeed

        // 2. Update _sellerProducts (Artisan "My Crafts" listings)
        val updatedSeller = _sellerProducts.value.map { prod ->
            if (prod.productId == productId) {
                val newStock = (prod.stockQuantity + deltaQuantity).coerceAtLeast(0)
                val newStatus = if (newStock <= 0) "sold" else if (prod.status == "sold") "active" else prod.status
                prod.copy(stockQuantity = newStock, status = newStatus)
            } else {
                prod
            }
        }
        _sellerProducts.value = updatedSeller

        // 3. Cache updated listings locally
        saveLocalArtisanCrafts(updatedSeller)

        // 4. Sync updated product document to Firestore
        val targetProduct = updatedFeed.find { it.productId == productId }
        if (targetProduct != null) {
            firestore?.collection("products")?.document(productId)?.set(targetProduct)
        }
        Log.i(TAG, "📦 Stock auto-updated for product $productId by $deltaQuantity. Remaining stock: ${targetProduct?.stockQuantity}, Status: ${targetProduct?.status}")
    }

    fun cancelOrder(orderId: String, onCancelled: () -> Unit = {}) {
        val orders = _artisanOrders.value.toMutableList()
        val index = orders.indexOfFirst { it.orderId == orderId }
        if (index >= 0) {
            val order = orders[index]
            if (order.status != "cancelled") {
                val updatedOrder = order.copy(status = "cancelled")
                orders[index] = updatedOrder
                _artisanOrders.value = orders
                saveOrders(orders)
                firestore?.collection("orders")?.document(orderId)?.set(updatedOrder)

                // Restore stock on both sides automatically!
                adjustProductStock(order.productId, +order.quantity)
                Log.i(TAG, "❌ Cancelled order $orderId. Restored ${order.quantity} units to stock for product ${order.productId}")
            }
        }
        onCancelled()
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        if (newStatus == "cancelled") {
            cancelOrder(orderId)
            return
        }
        val orders = _artisanOrders.value.toMutableList()
        val index = orders.indexOfFirst { it.orderId == orderId }
        if (index >= 0) {
            val updated = orders[index].copy(status = newStatus)
            orders[index] = updated
            _artisanOrders.value = orders
            saveOrders(orders)
            firestore?.collection("orders")?.document(orderId)?.set(updated)
            Log.i(TAG, "🚚 Order $orderId status updated to $newStatus")
        }
    }

    fun placeDemoOrder(cartItems: List<CartItemModel>, buyer: UserModel?, onPlaced: () -> Unit = {}) {
        if (cartItems.isEmpty()) return
        val currentOrders = _artisanOrders.value.toMutableList()
        val now = System.currentTimeMillis()

        cartItems.forEachIndexed { index, item ->
            // Auto-decrement stock by the purchased quantity on both sides!
            adjustProductStock(item.productId, -item.quantity)

            val order = OrderModel(
                orderId = "DHG-${(1000..9999).random()}-${index + 1}",
                productId = item.productId,
                productTitle = item.productTitle,
                productImageUrl = item.productImageUrl,
                buyerId = buyer?.uid ?: "buyer_demo",
                buyerName = buyer?.name?.takeIf { it.isNotBlank() } ?: "Demo Buyer",
                sellerId = _currentUser.value?.uid ?: "seller_default",
                sellerName = if (item.sellerName.isNotEmpty()) item.sellerName else (_currentUser.value?.name ?: "Master Artisan"),
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                totalAmount = item.totalPrice,
                platformFee = (item.totalPrice * 0.08).toLong(),
                sellerPayout = (item.totalPrice * 0.92).toLong(),
                status = "confirmed",
                createdAt = now
            )
            currentOrders.add(0, order)

            // Persist order to Firestore
            firestore?.collection("orders")?.document(order.orderId)?.set(order)
        }

        _artisanOrders.value = currentOrders
        saveOrders(currentOrders)
        clearCart()
        Log.i(TAG, "🛍️ Placed demo order! ${cartItems.size} items added to Artisan Dashboard. Total orders: ${currentOrders.size}")
        onPlaced()
    }

    /**
     * Attaches a real-time Firestore listener for orders belonging to the logged-in artisan (seller).
     * Merges with locally cached orders and saves the merged list so they survive offline/device-switch.
     */
    fun listenToSellerOrders(sellerId: String) {
        if (sellerId.isEmpty() || firestore == null) return
        sellerOrdersListener?.remove()
        Log.i(TAG, "📡 Listening to Firestore orders for seller: $sellerId")
        sellerOrdersListener = firestore.collection("orders")
            .whereEqualTo("sellerId", sellerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Seller orders listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val cloudOrders = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(OrderModel::class.java) } catch (e: Exception) { null }
                    }
                    // Merge: cloud takes precedence for freshest status; local fills in any offline-created orders
                    val localOrders = loadSavedOrders()
                    val merged = (cloudOrders + localOrders).distinctBy { it.orderId }
                        .sortedByDescending { it.createdAt }
                    _artisanOrders.value = merged
                    saveOrders(merged)
                    Log.i(TAG, "☁️ Seller orders synced: ${cloudOrders.size} from cloud, ${merged.size} total")
                }
            }
    }

    /**
     * Attaches a real-time Firestore listener for orders placed BY the logged-in buyer.
     */
    fun listenToBuyerOrders(buyerId: String) {
        if (buyerId.isEmpty() || firestore == null) return
        buyerOrdersListener?.remove()
        Log.i(TAG, "📡 Listening to Firestore orders for buyer: $buyerId")
        buyerOrdersListener = firestore.collection("orders")
            .whereEqualTo("buyerId", buyerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Buyer orders listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(OrderModel::class.java) } catch (e: Exception) { null }
                    }.sortedByDescending { it.createdAt }
                    _buyerOrders.value = orders
                    Log.i(TAG, "☁️ Buyer orders synced: ${orders.size} orders")
                }
            }
    }

    private fun loadSavedOrders(): List<OrderModel> {
        val json = prefs.getString("saved_orders_json", null)
        if (!json.isNullOrBlank()) {
            try {
                val array = JSONArray(json)
                val list = mutableListOf<OrderModel>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        OrderModel(
                            orderId = obj.optString("orderId"),
                            productId = obj.optString("productId"),
                            productTitle = obj.optString("productTitle"),
                            productImageUrl = obj.optString("productImageUrl"),
                            buyerId = obj.optString("buyerId"),
                            buyerName = obj.optString("buyerName"),
                            sellerId = obj.optString("sellerId"),
                            sellerName = obj.optString("sellerName"),
                            quantity = obj.optInt("quantity", 1),
                            unitPrice = obj.optLong("unitPrice"),
                            totalAmount = obj.optLong("totalAmount"),
                            sellerPayout = obj.optLong("sellerPayout"),
                            status = obj.optString("status", "confirmed"),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                return list
            } catch (e: Exception) {
                Log.w(TAG, "Failed parsing saved orders: ${e.message}")
            }
        }
        // No fallback demo orders — real orders come from Firestore via listenToSellerOrders()
        return emptyList()
    }

    private fun saveOrders(orders: List<OrderModel>) {
        try {
            val array = JSONArray()
            orders.forEach { o ->
                val obj = JSONObject()
                obj.put("orderId", o.orderId)
                obj.put("productId", o.productId)
                obj.put("productTitle", o.productTitle)
                obj.put("productImageUrl", o.productImageUrl)
                obj.put("buyerId", o.buyerId)
                obj.put("buyerName", o.buyerName)
                obj.put("sellerId", o.sellerId)
                obj.put("sellerName", o.sellerName)
                obj.put("quantity", o.quantity)
                obj.put("unitPrice", o.unitPrice)
                obj.put("totalAmount", o.totalAmount)
                obj.put("sellerPayout", o.sellerPayout)
                obj.put("status", o.status)
                obj.put("createdAt", o.createdAt)
                array.put(obj)
            }
            prefs.edit().putString("saved_orders_json", array.toString()).apply()
        } catch (e: Exception) {
            Log.w(TAG, "Failed saving orders: ${e.message}")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Wishlist & Cart Persistence (survives app restarts, cleared on logout)
    // ─────────────────────────────────────────────────────────────────────────────

    private fun saveWishlist(ids: Set<String>) {
        try {
            val array = JSONArray()
            ids.forEach { array.put(it) }
            prefs.edit().putString("saved_wishlist_json", array.toString()).apply()
        } catch (e: Exception) {
            Log.w(TAG, "Failed saving wishlist: ${e.message}")
        }
    }

    private fun loadSavedWishlist(): Set<String> {
        val json = prefs.getString("saved_wishlist_json", null) ?: return emptySet()
        return try {
            val array = JSONArray(json)
            val set = mutableSetOf<String>()
            for (i in 0 until array.length()) set.add(array.getString(i))
            set
        } catch (e: Exception) { emptySet() }
    }

    private fun saveCart(items: List<CartItemModel>) {
        try {
            val array = JSONArray()
            items.forEach { item ->
                val obj = JSONObject()
                obj.put("productId", item.productId)
                obj.put("productTitle", item.productTitle)
                obj.put("productImageUrl", item.productImageUrl)
                obj.put("sellerName", item.sellerName)
                obj.put("unitPrice", item.unitPrice)
                obj.put("quantity", item.quantity)
                array.put(obj)
            }
            prefs.edit().putString("saved_cart_json", array.toString()).apply()
        } catch (e: Exception) {
            Log.w(TAG, "Failed saving cart: ${e.message}")
        }
    }

    private fun loadSavedCart(): List<CartItemModel> {
        val json = prefs.getString("saved_cart_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<CartItemModel>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CartItemModel(
                        productId = obj.optString("productId"),
                        productTitle = obj.optString("productTitle"),
                        productImageUrl = obj.optString("productImageUrl"),
                        sellerName = obj.optString("sellerName"),
                        unitPrice = obj.optLong("unitPrice"),
                        quantity = obj.optInt("quantity", 1)
                    )
                )
            }
            list
        } catch (e: Exception) { emptyList() }
    }
}

