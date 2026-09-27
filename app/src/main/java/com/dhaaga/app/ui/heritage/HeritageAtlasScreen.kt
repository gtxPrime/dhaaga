package com.dhaaga.app.ui.heritage

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.mock.HeritageRegistry
import com.dhaaga.app.data.model.CraftHeritageModel
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager

@Composable
fun HeritageAtlasScreen(
    viewModel: AppViewModel,
    onBack: (() -> Unit)? = null,
    onCraftClick: (String) -> Unit
) {
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val traditions by viewModel.heritageTraditions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }
    var giOnlyFilter by remember { mutableStateOf(false) }

    val zones = listOf("All", "West", "North", "East", "Central", "South")
    val categories = listOf("All", "Folk Painting", "Handloom Textile", "Terracotta & Ceramic", "Metalcraft")

    val filteredTraditions = remember(traditions, searchQuery, selectedZone, selectedCategory, giOnlyFilter) {
        traditions.filter { craft ->
            val matchesQuery = searchQuery.isBlank() ||
                    craft.craftNameEn.contains(searchQuery, ignoreCase = true) ||
                    craft.craftNameHi.contains(searchQuery, ignoreCase = true) ||
                    craft.state.contains(searchQuery, ignoreCase = true) ||
                    craft.district.contains(searchQuery, ignoreCase = true)

            val matchesZone = when (selectedZone) {
                "West" -> craft.state in listOf("Maharashtra", "Gujarat", "Goa", "Rajasthan")
                "North" -> craft.state in listOf("Jammu and Kashmir", "Himachal Pradesh", "Punjab", "Uttarakhand", "Delhi", "Uttar Pradesh")
                "East" -> craft.state in listOf("Bihar", "West Bengal", "Odisha", "Jharkhand")
                "Central" -> craft.state in listOf("Madhya Pradesh", "Chhattisgarh")
                "South" -> craft.state in listOf("Karnataka", "Tamil Nadu", "Kerala", "Telangana", "Andhra Pradesh")
                else -> true
            }

            val matchesCategory = selectedCategory == "All" || craft.category.equals(selectedCategory, ignoreCase = true)
            val matchesGi = !giOnlyFilter || craft.hasGITag

            matchesQuery && matchesZone && matchesCategory && matchesGi
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (onBack != null) {
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
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PaletteForest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = AppLanguageManager.translate("heritage_atlas_title", currentLang, "भारत शिल्प एटलस • HERITAGE ATLAS"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = PaletteForest
                            )
                            Text(
                                text = "Explore Living Clusters & Traditions",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PaletteDarkGreen
                            )
                        }

                        // GI filter quick toggle
                        FilterChip(
                            selected = giOnlyFilter,
                            onClick = { giOnlyFilter = !giOnlyFilter },
                            label = { Text("GI Tags", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PaletteForest,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = PaletteMintCard,
                                labelColor = PaletteDarkGreen
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = PaletteForest,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = AppLanguageManager.translate("search_atlas", currentLang, "Search states, craft names, clusters..."),
                                        fontSize = 13.sp,
                                        color = DhaagaTextLight
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = DhaagaTextMedium,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Zone filters
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(zones) { zone ->
                            val isSelected = selectedZone == zone
                            Surface(
                                onClick = { selectedZone = zone },
                                color = if (isSelected) PaletteForest else PaletteMintCard,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, if (isSelected) PaletteForest else PaletteSage.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = if (zone == "All") "All India" else "$zone Zone",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else PaletteDarkGreen,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredTraditions.size} Living Traditions Found",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextMedium
                    )
                }
            }

            items(filteredTraditions, key = { it.craftId }) { craft ->
                AtlasCraftCard(
                    craft = craft,
                    currentLang = currentLang,
                    onClick = { onCraftClick(craft.craftId) }
                )
            }

            if (filteredTraditions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = PaletteSage,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No heritage traditions found matching your filters.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DhaagaTextMedium
                        )
                        TextButton(onClick = {
                            searchQuery = ""
                            selectedZone = "All"
                            selectedCategory = "All"
                            giOnlyFilter = false
                        }) {
                            Text("Reset Filters", color = PaletteForest, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AtlasCraftCard(
    craft: CraftHeritageModel,
    currentLang: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                com.dhaaga.app.ui.components.CardAsyncImage(
                    model = craft.bannerImageUrl,
                    contentDescription = "${craft.craftNameEn} ${craft.craftId}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Living Tradition Age Pill
                Surface(
                    color = PaletteForest,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${craft.traditionAgeYears}+ Years",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // GI Tag Badge
                if (craft.hasGITag) {
                    Surface(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = PaletteForest,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GI Verified",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteForest
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLang == "hi") craft.craftNameHi else craft.craftNameEn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        color = PaletteMintCard,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = craft.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteForest,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = PaletteForest,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${craft.district}, ${craft.state} • ${craft.community}",
                        fontSize = 12.sp,
                        color = DhaagaTextMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (currentLang == "hi") craft.summaryHi else craft.summaryEn,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = PaletteDarkGreen,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = DhaagaBorder.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (craft.hasOralHistory) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = PaletteForest,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Oral Lore",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PaletteForest
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = DhaagaTextMedium,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${craft.learnerCount} Learners",
                                fontSize = 11.sp,
                                color = DhaagaTextMedium
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Explore",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteForest
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = PaletteForest,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
