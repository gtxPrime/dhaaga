package com.dhaaga.app.navigation

/** All navigation route constants */
object Routes {
    // Onboarding
    const val SPLASH = "splash"
    const val LANGUAGE_SELECT = "language_select"
    const val ROLE_SELECT = "role_select"
    const val PHONE_OTP = "phone_otp"
    const val PROFILE_SETUP = "profile_setup"

    // Main (role-aware)
    const val HOME = "home?tab={tab}"
    fun home(tab: Int = 0) = "home?tab=$tab"
    const val PRODUCT_DETAIL = "product_detail/{productId}?sharedKey={sharedKey}"
    const val PROFILE = "profile"

    // Seller
    const val MY_LISTINGS = "my_listings"
    const val ADD_PRODUCT = "add_product"
    const val SELLER_DASHBOARD = "seller_dashboard"

    // Buyer
    const val WISHLIST = "wishlist"
    const val CART = "cart"
    const val MY_ORDERS = "my_orders"
    const val ORDER_TRACKING = "order_tracking/{orderId}"
    const val PAYMENT = "payment/{orderId}"
    const val ORDER_SUCCESS = "order_success/{orderId}"

    // Chat
    const val CHAT_LIST = "chat_list"
    const val CHAT = "chat/{chatId}"

    fun productDetail(productId: String, sharedKey: String = "product-image-$productId") =
        "product_detail/$productId?sharedKey=$sharedKey"
    fun orderTracking(orderId: String) = "order_tracking/$orderId"
    fun payment(orderId: String) = "payment/$orderId"
    fun orderSuccess(orderId: String) = "order_success/$orderId"
    fun chat(chatId: String) = "chat/$chatId"
}
