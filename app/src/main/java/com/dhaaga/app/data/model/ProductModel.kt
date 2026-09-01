package com.dhaaga.app.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ProductModel(
    val productId: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerVillage: String = "",
    val titleEn: String = "",
    val titleHi: String = "",
    val descriptionEn: String = "",
    val descriptionHi: String = "",
    val craftType: String = "",
    val material: String = "",
    val color: List<String> = emptyList(),
    val sizeCm: String = "",
    val technique: String = "",
    val region: String = "",
    val giTag: String? = null,
    val giVerified: Boolean = false,
    val authenticityScore: Int = 0,
    val priceListed: Long = 0L,         // in paise
    val priceMin: Long = 0L,
    val moq: Int = 1,
    val imageUrls: List<String> = emptyList(),
    val storyEn: String = "",
    val storyHi: String = "",
    val stockQuantity: Int = 0,
    val status: String = "active",       // draft|active|paused|sold|gem_submitted
    val viewCount: Int = 0,
    val wishlistCount: Int = 0,
    val avgRating: Float = 0f,
    val reviewCount: Int = 0,
    val isFeatured: Boolean = false,
    val festiveTag: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val salePricePaise: Long? = null,              // Optional Direct Sale Price (in paise)
    val hasDiscount: Boolean = false,              // Whether a direct sale or coupon discount is active
    val discountType: String = "percentage",       // "percentage" (e.g. 15%) or "fixed" (e.g. ₹200 flat)
    val discountValue: Long = 0L,                  // numeric value (15 or 200)
    val couponCode: String? = null,                // e.g. "DHAAGA20", "WARLI15"
    val couponExpiryTimestamp: Long? = null,       // Expiry epoch timestamp
    val couponUsageLimit: Int = 0,                 // 0 = unlimited, or 10, 25, 50, etc.
    val couponUsageCount: Int = 0                  // Times redeemed
) {
    @get:Exclude
    val priceRupees: Float get() = priceListed / 100f
    @get:Exclude
    val priceDisplay: String get() = "₹${priceListed / 100}"
    @get:Exclude
    val effectivePricePaise: Long get() {
        if (salePricePaise != null && salePricePaise > 0L && salePricePaise < priceListed) return salePricePaise
        return priceListed
    }
    @get:Exclude
    val effectivePriceRupees: Float get() = effectivePricePaise / 100f
    @get:Exclude
    val effectivePriceDisplay: String get() = "₹${effectivePricePaise / 100}"
    @get:Exclude
    val hasSalePrice: Boolean get() = salePricePaise != null && salePricePaise > 0L && salePricePaise < priceListed
    @get:Exclude
    val discountPercentage: Int get() {
        if (hasSalePrice && priceListed > 0) {
            val diff = priceListed - (salePricePaise ?: priceListed)
            return ((diff.toDouble() / priceListed) * 100).toInt()
        }
        if (discountType == "percentage" && discountValue > 0) return discountValue.toInt()
        if (discountType == "fixed" && discountValue > 0 && priceListed > 0) {
            return (((discountValue * 100).toDouble() / priceListed) * 100).toInt().coerceAtMost(90)
        }
        return 0
    }
    @get:Exclude
    val isCouponValid: Boolean get() {
        if (couponCode.isNullOrBlank()) return false
        val now = System.currentTimeMillis()
        if (couponExpiryTimestamp != null && now > couponExpiryTimestamp) return false
        if (couponUsageLimit > 0 && couponUsageCount >= couponUsageLimit) return false
        return true
    }
    @get:Exclude
    val hasGITag: Boolean get() = giTag != null && giVerified
    @get:Exclude
    val primaryImageUrl: String get() = imageUrls.firstOrNull() ?: ""
}
