package com.dhaaga.app.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class CraftHeritageModel(
    val craftId: String = "",
    val craftNameEn: String = "",
    val craftNameHi: String = "",
    val category: String = "",              // Folk Painting, Handloom Textile, Terracotta & Clay, Metalcraft, Woodcraft
    val state: String = "",
    val district: String = "",
    val region: String = "",                // e.g. "Western Ghats", "Mithila Region", "Changthang Plateau"
    val community: String = "",             // e.g. "Warli Tribe", "Mithila Community", "Changpa Nomads"
    val traditionAgeYears: Int = 0,         // e.g. 2500
    val summaryEn: String = "",
    val summaryHi: String = "",
    val storyKahaaniEn: String = "",
    val storyKahaaniHi: String = "",
    val culturalSignificance: String = "",
    val rawMaterials: List<CraftMaterial> = emptyList(),
    val toolsUsed: List<String> = emptyList(),
    val processSteps: List<CraftStep> = emptyList(),
    val motifsAndSymbols: List<CraftSymbol> = emptyList(),
    val oralHistoryAudioUrl: String? = null,
    val oralHistoryTranscriptHi: String = "",
    val oralHistoryTranscriptEn: String = "",
    val oralHistoryDurationSec: Int = 180,
    val oralNarratorName: String = "",
    val processVideoUrl: String? = null,
    val bannerImageUrl: String = "",
    val galleryImageUrls: List<String> = emptyList(),
    val giTagNumber: String? = null,
    val giRegisteredYear: String? = null,
    val primaryMasterArtisanId: String = "",
    val masterArtisanName: String = "",
    val masterArtisanVillage: String = "",
    val relatedProductIds: List<String> = emptyList(),
    val verifiedByArtisan: Boolean = true,
    val isFeatured: Boolean = false,
    val viewCount: Int = 0,
    val learnerCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    @get:Exclude
    val hasOralHistory: Boolean get() = !oralHistoryAudioUrl.isNullOrBlank() || oralHistoryTranscriptHi.isNotBlank()

    @get:Exclude
    val hasVideo: Boolean get() = !processVideoUrl.isNullOrBlank()

    @get:Exclude
    val hasGITag: Boolean get() = !giTagNumber.isNullOrBlank()
}

@IgnoreExtraProperties
data class CraftMaterial(
    val nameEn: String = "",
    val nameHi: String = "",
    val source: String = "",                // e.g. "Natural Rice Paste", "Riverbank Clay", "Indigo Plant"
    val isEcoFriendly: Boolean = true,
    val preparationNote: String = ""
)

@IgnoreExtraProperties
data class CraftStep(
    val stepNumber: Int = 1,
    val titleEn: String = "",
    val titleHi: String = "",
    val descriptionEn: String = "",
    val descriptionHi: String = "",
    val durationMin: Int = 15,
    val mediaUrl: String? = null
)

@IgnoreExtraProperties
data class CraftSymbol(
    val symbolNameEn: String = "",
    val symbolNameHi: String = "",
    val meaningEn: String = "",
    val meaningHi: String = "",
    val spiritualSignificance: String = "",
    val iconUrl: String? = null
)
