package com.dhaaga.app.ui.heritage

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager
import com.dhaaga.app.utils.AppTtsManager

@Composable
fun LearnPracticeScreen(
    initialCraftId: String = "",
    viewModel: AppViewModel,
    onBack: (() -> Unit)? = null,
    onViewHeritageRecord: (String) -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val traditions by viewModel.heritageTraditions.collectAsState()

    var selectedCraftId by remember {
        mutableStateOf(
            if (initialCraftId.isNotBlank()) initialCraftId
            else traditions.firstOrNull()?.craftId ?: "heritage_warli_01"
        )
    }

    val selectedCraft = remember(selectedCraftId, traditions) {
        traditions.find { it.craftId == selectedCraftId }
            ?: HeritageRegistry.getCraftById(selectedCraftId)
            ?: traditions.first()
    }

    var selectedSection by remember { mutableStateOf(0) } // 0: Guided Practice, 1: Motif Decoder, 2: Natural Materials
    val completedSteps = remember { mutableStateListOf<Int>() }
    var practiceCompletedCelebrated by remember { mutableStateOf(false) }

    val isSpeaking by AppTtsManager.isSpeaking.collectAsState()

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
                                    imageVector = Icons.Default.School,
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
                                text = AppLanguageManager.translate("learn_practice_title", currentLang, "ज्ञान व अभ्यास • LEARN & PRACTICE"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = PaletteForest
                            )
                            Text(
                                text = if (currentLang == "hi") selectedCraft.craftNameHi else selectedCraft.craftNameEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PaletteDarkGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    AppTtsManager.stop()
                                } else {
                                    val text = if (currentLang == "hi") {
                                        "अभ्यास सत्र: ${selectedCraft.craftNameHi}। आइए सीखें इसकी तकनीक और प्रतीक।"
                                    } else {
                                        "Practice session: ${selectedCraft.craftNameEn}. Let us learn its technique and ancient symbols."
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
                                contentDescription = "Voice Instructions",
                                tint = if (isSpeaking) Color.White else PaletteDarkGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal craft selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(traditions) { c ->
                            val isSelected = c.craftId == selectedCraftId
                            Surface(
                                onClick = {
                                    selectedCraftId = c.craftId
                                    completedSteps.clear()
                                    practiceCompletedCelebrated = false
                                },
                                color = if (isSelected) PaletteForest else PaletteMintCard,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, if (isSelected) PaletteForest else PaletteSage.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = if (currentLang == "hi") c.craftNameHi.take(12) else c.craftNameEn.take(16),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else PaletteDarkGreen,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary sub-tab row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val subTabs = listOf(
                            "Steps" to 0,
                            "Motifs" to 1,
                            "Materials" to 2
                        )
                        subTabs.forEach { (title, idx) ->
                            val isSelected = selectedSection == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PaletteTerracotta else Color.Transparent)
                                    .clickable { selectedSection = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else PaletteDarkGreen
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
            contentPadding = PaddingValues(bottom = 120.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header summary card
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tradition: ${selectedCraft.traditionAgeYears}+ Years",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteTerracotta,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            TextButton(
                                onClick = { onViewHeritageRecord(selectedCraft.craftId) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Heritage Record →",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteTerracotta
                                )
                            }
                        }
                        Text(
                            text = if (currentLang == "hi") selectedCraft.summaryHi else selectedCraft.summaryEn,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = PaletteDarkGreen
                        )
                    }
                }
            }

            when (selectedSection) {
                0 -> {
                    // ── Sub-tab 0: Guided Step-by-Step Practice ──────────────────
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Interactive Guided Steps",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen
                            )
                            Text(
                                text = "${completedSteps.size} of ${selectedCraft.processSteps.size} Completed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteForest
                            )
                        }
                    }

                    items(selectedCraft.processSteps) { step ->
                        val isDone = completedSteps.contains(step.stepNumber)
                        Surface(
                            onClick = {
                                if (isDone) completedSteps.remove(step.stepNumber)
                                else completedSteps.add(step.stepNumber)
                            },
                            color = if (isDone) PaletteMintCard else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isDone) PaletteForest else DhaagaBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = isDone,
                                    onCheckedChange = { checked ->
                                        if (checked) completedSteps.add(step.stepNumber)
                                        else completedSteps.remove(step.stepNumber)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PaletteForest,
                                        checkmarkColor = Color.White
                                    )
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Step ${step.stepNumber}: ${if (currentLang == "hi") step.titleHi else step.titleEn}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (currentLang == "hi") step.descriptionHi else step.descriptionEn,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = DhaagaTextMedium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Estimated Time: ~${step.durationMin} mins",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PaletteForest
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                practiceCompletedCelebrated = true
                                viewModel.incrementHeritageLearner(selectedCraft.craftId)
                                Toast.makeText(
                                    context,
                                    "Congratulations! You practiced ${selectedCraft.craftNameEn}. Cultural learner score updated!",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Complete & Record My Practice",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        if (practiceCompletedCelebrated) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = PaletteMintCard,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PaletteForest),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Living Tradition Sustained!",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen
                                        )
                                        Text(
                                            text = "You are now one of ${selectedCraft.learnerCount + 1} active practitioners keeping this sacred Indian craft alive.",
                                            fontSize = 11.sp,
                                            color = DhaagaTextMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // ── Sub-tab 1: Motif & Symbolism Decoder ─────────────────────
                    item {
                        Text(
                            text = "Cultural Iconography & Sacred Symbols",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            text = "Tap any symbol to decode its ancient cosmic, agricultural or spiritual wisdom.",
                            fontSize = 12.sp,
                            color = DhaagaTextLight
                        )
                    }

                    items(selectedCraft.motifsAndSymbols) { sym ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f)),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(PaletteMintCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = PaletteForest,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = sym.symbolNameEn,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen
                                        )
                                        Text(
                                            text = sym.symbolNameHi,
                                            fontSize = 12.sp,
                                            color = PaletteForest
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (currentLang == "hi") sym.meaningHi else sym.meaningEn,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PaletteDarkGreen
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // ── Sub-tab 2: Natural Materials & Indigenous Alchemy ────────
                    item {
                        Text(
                            text = "Indigenous Materials & Natural Chemistry",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            text = "How nature's plants, minerals, and earth are transformed into living craft.",
                            fontSize = 12.sp,
                            color = DhaagaTextLight
                        )
                    }

                    items(selectedCraft.rawMaterials) { mat ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f)),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
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
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${mat.nameEn} • ${mat.nameHi}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen
                                        )
                                        Surface(
                                            color = PaletteMintCard,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (mat.isEcoFriendly) "100% Eco-Friendly" else "Traditional Compound",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PaletteForest,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Source: ${mat.source}",
                                    fontSize = 12.sp,
                                    color = DhaagaTextMedium
                                )
                                if (mat.preparationNote.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Preparation: ${mat.preparationNote}",
                                        fontSize = 11.sp,
                                        color = PaletteForest
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
