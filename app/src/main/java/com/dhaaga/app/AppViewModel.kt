package com.dhaaga.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.data.model.CartItemModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        private const val TAG = "DhaagaApp"
        private const val PREFS_NAME = "dhaaga_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_VILLAGE = "user_village"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_STATE = "user_state"
        private const val KEY_USER_UID = "user_uid"
    }

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

    private val _products = MutableStateFlow(MockData.mockProducts)
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _cart = MutableStateFlow(MockData.mockCart.toMutableList())
    val cart: StateFlow<List<CartItemModel>> = _cart.asStateFlow()

    // Real-time Cart Animation Event Trigger (Timestamp) & Last Added Product
    private val _cartAnimationEvent = MutableStateFlow<Long>(0L)
    val cartAnimationEvent: StateFlow<Long> = _cartAnimationEvent.asStateFlow()

    private val _lastAddedProduct = MutableStateFlow<ProductModel?>(null)
    val lastAddedProduct: StateFlow<ProductModel?> = _lastAddedProduct.asStateFlow()

    val cartTotal: Long get() = _cart.value.sumOf { it.totalPrice }
    val cartCount: Int get() = _cart.value.size

    init {
        Log.i(TAG, "Dhaaga ViewModel initialized. Initial Session logged in = ${_currentUser.value != null}")
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
        val effectiveUid = if (firebaseUid.isNotEmpty()) firebaseUid else prefs.getString(KEY_USER_UID, "user_${System.currentTimeMillis()}") ?: ""

        if (name.isEmpty() && phone.isEmpty() && effectiveUid.isEmpty()) return null

        return if (role == "seller") {
            MockData.mockSeller.copy(
                uid = effectiveUid,
                name = name.ifEmpty { "Artisan Seller" },
                village = village.ifEmpty { "India" },
                state = state.ifEmpty { "India" },
                phoneNumber = phone
            )
        } else {
            MockData.mockBuyer.copy(
                uid = effectiveUid,
                name = name.ifEmpty { "Craft Buyer" },
                phoneNumber = phone
            )
        }
    }

    fun loginAs(user: UserModel) {
        val firebaseUid = firebaseAuth?.currentUser?.uid
        val effectiveUser = if (!firebaseUid.isNullOrEmpty() && (user.uid.isEmpty() || user.uid.startsWith("mock_") || user.uid.startsWith("user_") || user.uid.startsWith("seller") || user.uid.startsWith("buyer"))) {
            user.copy(uid = firebaseUid)
        } else {
            user
        }

        Log.i(TAG, "🔑 User logged in & saved to session: ${effectiveUser.name} (${effectiveUser.role}, UID=${effectiveUser.uid}, Phone=${effectiveUser.phoneNumber})")
        _currentUser.value = effectiveUser
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_UID, effectiveUser.uid)
            .putString(KEY_USER_NAME, effectiveUser.name)
            .putString(KEY_USER_ROLE, effectiveUser.role)
            .putString(KEY_USER_VILLAGE, effectiveUser.village)
            .putString(KEY_USER_STATE, effectiveUser.state)
            .putString(KEY_USER_PHONE, effectiveUser.phoneNumber)
            .apply()

        // Sync user profile to Firestore in background
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
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase sign out warning: ${e.message}")
        }
        _currentUser.value = null
        prefs.edit().clear().apply()
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
        Log.d(TAG, "Current Cart Total: ₹${cartTotal / 100} across ${_cart.value.size} items")
    }

    fun removeFromCart(productId: String) {
        Log.i(TAG, "🛒 Removed product $productId from cart")
        _cart.value = _cart.value.filter { it.productId != productId }.toMutableList()
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
    }

    fun clearCart() {
        _cart.value = mutableListOf()
    }
}
