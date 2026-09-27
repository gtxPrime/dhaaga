package com.dhaaga.app.ui.heritage

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.mock.HeritageRegistry
import com.dhaaga.app.data.model.CraftHeritageModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager
import com.dhaaga.app.utils.AppTtsManager
import kotlinx.coroutines.launch

@Composable
fun CraftHeritageDetailScreen(
    craftId: String,
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onLearnPractice: (String) -> Unit = {},
    onSupportCreation: (ProductModel) -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val savedBookmarks by viewModel.savedHeritageBookmarks.collectAsState()
    val allProducts by viewModel.products.collectAsState()

    val craft: CraftHeritageModel = remember(craftId) {
        viewModel.getHeritageCraft(craftId)
            ?: HeritageRegistry.livingTraditions.firstOrNull { it.craftId == craftId }
            ?: HeritageRegistry.livingTraditions.first()
    }

    val isBookmarked = savedBookmarks.contains(craft.craftId)
    val isSpeaking by AppTtsManager.isSpeaking.collectAsState()
    var isOralHistoryPlaying by remember { mutableStateOf(false) }
    var selectedTranscriptLang by remember { mutableStateOf(if (currentLang == "hi") "hi" else "en") }
    var expandedSymbolIndex by remember { mutableStateOf(-1) }

    // Related creation/product to support
    val relatedProduct = remember(craft, allProducts) {
        allProducts.find { it.craftHeritageId == craft.craftId }
            ?: allProducts.firstOrNull { it.productId in craft.relatedProductIds }
    }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val images = remember(craft) {
        listOf<Any>(com.dhaaga.app.ui.components.getCraftFallbackDrawable(craft.craftId))
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })

    // Stop TTS when navigating away
    DisposableEffect(Unit) {
        onDispose {
            AppTtsManager.stop()
        }
    }

    Scaffold(
        containerColor = DhaagaBackground,
        topBar = {
            Surface(
                color = DhaagaBackground.copy(alpha = 0.96f),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, DhaagaBorder.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PaletteDarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AppLanguageManager.translate("heritage_record", currentLang, "LIVING HERITAGE RECORD"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = PaletteForest
                        )
                        Text(
                            text = if (currentLang == "hi") craft.craftNameHi else craft.craftNameEn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PaletteDarkGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Audio Guide Speak Button
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    AppTtsManager.stop()
                                } else {
                                    val text = if (currentLang == "hi") {
                                        "${craft.craftNameHi}। ${craft.summaryHi}। ${craft.storyKahaaniHi}"
                                    } else {
                                        "${craft.craftNameEn}. ${craft.summaryEn}. ${craft.storyKahaaniEn}"
                                    }
                                    AppTtsManager.speak(text, currentLang)
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) PaletteForest else Color.White)
                                .border(1.dp, DhaagaBorder.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Outlined.VolumeUp,
                                contentDescription = "Listen Audio Guide",
                                tint = if (isSpeaking) Color.White else PaletteDarkGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Bookmark Button
                        IconButton(
                            onClick = {
                                viewModel.toggleHeritageBookmark(craft.craftId)
                                val msg = if (!isBookmarked) "Saved to Cultural Passport" else "Removed from Bookmarks"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, DhaagaBorder.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) PaletteForest else PaletteDarkGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Support Artisan & Buy Authentic Creation Footer
            if (relatedProduct != null) {
                Surface(
                    color = Color.White,
                    shadowElevation = 10.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = AppLanguageManager.translate("support_tradition", currentLang, "Direct Support"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DhaagaTextLight,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = relatedProduct.priceDisplay,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = PaletteMintCard,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "100% Direct",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteTerracotta,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.addToCart(relatedProduct)
                                onSupportCreation(relatedProduct)
                                Toast.makeText(context, "Added authentic creation to bag! Supporting ${craft.masterArtisanName}.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PaletteTerracotta),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLanguageManager.translate("add_to_bag", currentLang, "Add to Bag"),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // ── 1. Hero Media Carousel with Video Badge ─────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    com.dhaaga.app.ui.components.CardAsyncImage(
                        model = images[page],
                        contentDescription = "${craft.craftNameEn} ${craft.craftId}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                // Badges Row (Living Tradition + Video Tag)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = PaletteTerracotta,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${craft.traditionAgeYears}+ Yrs Heritage",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }

                    if (craft.hasVideo) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Process Video",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Pager Indicators
                if (images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(images.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            // ── 2. Craft Overview & GI Provenance ───────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = craft.craftNameEn,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            text = craft.craftNameHi,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = PaletteForest
                        )
                    }

                    Surface(
                        color = PaletteMintCard,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = craft.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location & Community Attribution
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = PaletteForest,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${craft.district}, ${craft.state} • ${craft.community}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DhaagaTextMedium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Official GI Tag Provenance Card
                if (craft.hasGITag) {
                    Surface(
                        color = PaletteMintCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PaletteForest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Government Verified Geographical Indication",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Text(
                                    text = "${craft.giTagNumber} • Registered ${craft.giRegisteredYear ?: ""}",
                                    fontSize = 11.sp,
                                    color = DhaagaTextMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── 3. Artisan Oral History Player (Core Living Heritage Feature) ───
                OralHistoryPlayerCard(
                    craft = craft,
                    isPlaying = isOralHistoryPlaying,
                    selectedLang = selectedTranscriptLang,
                    onTogglePlay = {
                        isOralHistoryPlaying = !isOralHistoryPlaying
                        if (isOralHistoryPlaying) {
                            val text = if (selectedTranscriptLang == "hi") craft.oralHistoryTranscriptHi else craft.oralHistoryTranscriptEn
                            AppTtsManager.speak(text, selectedTranscriptLang)
                        } else {
                            AppTtsManager.stop()
                        }
                    },
                    onLangChange = { newLang ->
                        selectedTranscriptLang = newLang
                        if (isOralHistoryPlaying) {
                            val text = if (newLang == "hi") craft.oralHistoryTranscriptHi else craft.oralHistoryTranscriptEn
                            AppTtsManager.speak(text, newLang)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── 4. The Kahaani (Living Tradition Lore) ────────────────────
                SectionHeader(
                    title = AppLanguageManager.translate("craft_story", currentLang, "The Kahaani • Living Tradition Lore"),
                    subtitle = "Passed down through oral memory and community practice"
                )

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = PaletteSage,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (currentLang == "hi") craft.storyKahaaniHi else craft.storyKahaaniEn,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = PaletteDarkGreen,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DhaagaBorder.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cultural Meaning: ${craft.culturalSignificance}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PaletteForest
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── 5. Cultural Motifs & Symbols Decoder ──────────────────────
                SectionHeader(
                    title = "Motifs & Symbolism Decoder",
                    subtitle = "Tap a sacred symbol to decode its ancient meaning"
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    craft.motifsAndSymbols.forEachIndexed { index, symbol ->
                        val isExpanded = expandedSymbolIndex == index
                        Surface(
                            onClick = {
                                expandedSymbolIndex = if (isExpanded) -1 else index
                            },
                            color = if (isExpanded) PaletteMintCard else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isExpanded) PaletteForest else DhaagaBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(PaletteForest.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = PaletteForest,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = symbol.symbolNameEn,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PaletteDarkGreen
                                            )
                                            Text(
                                                text = symbol.symbolNameHi,
                                                fontSize = 12.sp,
                                                color = PaletteForest
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = PaletteDarkGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 10.dp)) {
                                        Text(
                                            text = if (currentLang == "hi") symbol.meaningHi else symbol.meaningEn,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            color = PaletteDarkGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── 6. Materials & Indigenous Technique ───────────────────────
                SectionHeader(
                    title = "Materials & Natural Ingredients",
                    subtitle = "100% Eco-friendly indigenous preparation"
                )

                craft.rawMaterials.forEach { mat ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PaletteMintCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = PaletteForest,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${mat.nameEn} • ${mat.nameHi}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Text(
                                    text = mat.source,
                                    fontSize = 11.sp,
                                    color = DhaagaTextMedium
                                )
                                if (mat.preparationNote.isNotBlank()) {
                                    Text(
                                        text = "Note: ${mat.preparationNote}",
                                        fontSize = 10.sp,
                                        color = PaletteForest
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── 7. Step-by-Step Technique & Learn CTA ─────────────────────
                SectionHeader(
                    title = "Technique Demonstration Steps",
                    subtitle = "How the living tradition is practiced"
                )

                craft.processSteps.forEach { step ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(PaletteForest),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${step.stepNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLang == "hi") step.titleHi else step.titleEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentLang == "hi") step.descriptionHi else step.descriptionEn,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = DhaagaTextMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Practice Button
                OutlinedButton(
                    onClick = { onLearnPractice(craft.craftId) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PaletteForest),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaletteForest),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLanguageManager.translate("start_guided_practice", currentLang, "Enter Guided Practice Hub"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── 8. Meet the Master Artisan ────────────────────────────────
                SectionHeader(
                    title = "Meet the Master Artisan",
                    subtitle = "Direct practitioner preserving this tradition"
                )

                Surface(
                    color = PaletteMintCard,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PaletteForest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = craft.masterArtisanName.take(1),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = craft.masterArtisanName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen
                            )
                            Text(
                                text = craft.masterArtisanVillage,
                                fontSize = 12.sp,
                                color = DhaagaTextMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "3rd Gen Practitioner • State Merit",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteForest,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PaletteDarkGreen
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = DhaagaTextLight
        )
    }
}

@Composable
private fun OralHistoryPlayerCard(
    craft: CraftHeritageModel,
    isPlaying: Boolean,
    selectedLang: String,
    onTogglePlay: () -> Unit,
    onLangChange: (String) -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PaletteTerracotta),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "Oral Archive",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Bilingual tabs
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(2.dp)
                ) {
                    val isHindi = selectedLang == "hi"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isHindi) PaletteTerracotta else Color.Transparent)
                            .clickable { onLangChange("hi") }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "हिंदी",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHindi) Color.White else DhaagaTextDark
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isHindi) PaletteTerracotta else Color.Transparent)
                            .clickable { onLangChange("en") }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "English",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isHindi) Color.White else DhaagaTextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player controls & waveform
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaletteMintCard)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PaletteForest)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = craft.oralNarratorName.ifBlank { craft.masterArtisanName },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Simulated Audio Waveform Bars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heights = listOf(8, 14, 22, 16, 28, 12, 20, 26, 14, 18, 24, 10, 16, 22, 14, 20)
                        heights.forEachIndexed { i, h ->
                            val infiniteTransition = rememberInfiniteTransition(label = "wave_$i")
                            val animatedHeight by infiniteTransition.animateFloat(
                                initialValue = if (isPlaying) (h * 0.6f) else (h * 0.4f),
                                targetValue = if (isPlaying) h.toFloat() else (h * 0.4f),
                                animationSpec = infiniteRepeatable(
                                    animation = tween(300 + (i * 20), easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "wave_h_$i"
                            )
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(animatedHeight.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isPlaying) PaletteForest else PaletteSage)
                            )
                        }
                    }
                }

                Text(
                    text = "${craft.oralHistoryDurationSec / 60}:${String.format("%02d", craft.oralHistoryDurationSec % 60)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = DhaagaTextMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Synchronized Transcript Box
            Text(
                text = "Transcript:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PaletteForest
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (selectedLang == "hi") craft.oralHistoryTranscriptHi else craft.oralHistoryTranscriptEn,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = PaletteDarkGreen,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
