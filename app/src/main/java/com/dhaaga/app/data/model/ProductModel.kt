package com.dhaaga.app.data.model

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
    val updatedAt: Long = System.currentTimeMillis()
) {
    val priceRupees: Float get() = priceListed / 100f
    val priceDisplay: String get() = "₹${priceListed / 100}"
    val hasGITag: Boolean get() = giTag != null && giVerified
    val primaryImageUrl: String get() = imageUrls.firstOrNull() ?: ""
}
