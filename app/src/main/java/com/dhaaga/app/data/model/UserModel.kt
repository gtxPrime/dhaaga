package com.dhaaga.app.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserModel(
    val uid: String = "",
    val phoneNumber: String = "",
    val name: String = "",
    val role: String = "buyer",          // "seller" | "buyer"
    val languagePref: String = "en",
    val village: String = "",
    val district: String = "",
    val state: String = "",
    val craftTypes: List<String> = emptyList(),
    val craftInterests: List<String> = emptyList(),
    val profilePhotoUrl: String = "",
    val shilpiScore: Int = 0,
    val walletBalance: Long = 0L,
    val totalEarnings: Long = 0L,
    val bio: String = "",
    val yearsExperience: Int = 0,
    val storefrontSlug: String = "",
    val fcmToken: String = "",
    val isAadhaarVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    @get:Exclude
    val isSeller: Boolean get() = role == "seller"
    @get:Exclude
    val isBuyer: Boolean get() = role == "buyer"
}
