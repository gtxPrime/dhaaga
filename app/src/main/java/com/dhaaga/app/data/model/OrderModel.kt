package com.dhaaga.app.data.model

data class OrderModel(
    val orderId: String = "",
    val productId: String = "",
    val productTitle: String = "",
    val productImageUrl: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val quantity: Int = 1,
    val unitPrice: Long = 0L,            // in paise
    val totalAmount: Long = 0L,
    val platformFee: Long = 0L,
    val sellerPayout: Long = 0L,
    val paymentMethod: String = "mock_upi",
    val paymentStatus: String = "paid",
    val isMockPayment: Boolean = true,
    val deliveryAddress: AddressModel = AddressModel(),
    val shippingCarrier: String = "Delhivery (Demo)",
    val trackingId: String = "",
    val status: String = "confirmed",    // pending|confirmed|packed|shipped|delivered|cancelled
    val isB2b: Boolean = false,
    val estimatedDelivery: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalDisplay: String get() = "₹${totalAmount / 100}"
    val statusDisplay: String get() = when(status) {
        "pending"   -> "Order Placed"
        "confirmed" -> "Confirmed"
        "packed"    -> "Being Prepared"
        "shipped"   -> "Shipped"
        "delivered" -> "Delivered"
        "cancelled" -> "Cancelled"
        else -> status.replaceFirstChar { it.uppercase() }
    }
}

data class AddressModel(
    val name: String = "",
    val phone: String = "",
    val line1: String = "",
    val line2: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = ""
)

data class CartItemModel(
    val productId: String = "",
    val productTitle: String = "",
    val productImageUrl: String = "",
    val sellerName: String = "",
    val unitPrice: Long = 0L,
    val quantity: Int = 1
) {
    val totalPrice: Long get() = unitPrice * quantity
    val totalDisplay: String get() = "₹${totalPrice / 100}"
    val unitDisplay: String get() = "₹${unitPrice / 100}"
}
