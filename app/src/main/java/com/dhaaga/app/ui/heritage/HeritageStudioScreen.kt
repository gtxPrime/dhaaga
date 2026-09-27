package com.dhaaga.app.ui.heritage

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.mock.GITagRegistry
import com.dhaaga.app.data.model.*
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager
import com.dhaaga.app.utils.AppTtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HeritageStudioScreen(
    viewModel: AppViewModel,
    onBack: (() -> Unit)? = null,
    onPublished: (String) -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Studio Workflow State: 0 = Input, 1 = AI Structuring, 2 = Artisan Review & Validate
    var currentStep by remember { mutableStateOf(0) }

    // Artisan inputs
    var craftTitleInput by remember { mutableStateOf("") }
    var artisanVoiceNoteText by remember { mutableStateOf("") }
    var selectedPhotoUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1582560475093-ba66accbc424?w=800&auto=format&fit=crop&q=80") }
    var selectedState by remember { mutableStateOf(currentUser?.state.takeIf { !it.isNullOrBlank() } ?: "Maharashtra") }
    var communityName by remember { mutableStateOf("Warli Tribal Collective") }

    // Generated draft record
    var draftCraftNameEn by remember { mutableStateOf("Warli Ritual Wall Painting") }
    var draftCraftNameHi by remember { mutableStateOf("वारली पारंपरिक भित्ति चित्रकला") }
    var draftSummaryEn by remember { mutableStateOf("Ancient sacred geometric folk art made using fermented rice flour on Geru earth wash, celebrating Mother Earth and fertility deity Palaghat.") }
    var draftSummaryHi by remember { mutableStateOf("गेरू की मिट्टी पर चावल के लेप से बनाई जाने वाली पवित्र ज्यामितीय लोककला, जो प्रकृति और कुलदेवी पालाघाट को समर्पित है।") }
    var draftKahaaniEn by remember { mutableStateOf("This heritage record captures the oral wisdom passed through matrilineal Suhasini artists during weddings and harvest cycles.") }
    var draftKahaaniHi by remember { mutableStateOf("यह परम्परा पीढ़ियों से माताओं द्वारा बेटियों को सिखाई गई है, जिसमें सामूहिक जीवन और प्रकृति के सामंजस्य का उत्सव मनाया जाता है।") }
    var verifiedGiTag by remember { mutableStateOf("GI Application #183 (Warli Painting)") }

    // Speech recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!spoken.isNullOrBlank()) {
            artisanVoiceNoteText = if (artisanVoiceNoteText.isBlank()) spoken else "$artisanVoiceNoteText $spoken"
            Toast.makeText(context, "Voice note captured!", Toast.LENGTH_SHORT).show()
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
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AppLanguageManager.translate("heritage_studio_title", currentLang, "शिल्पकार प्रलेखन स्टूडियो"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = PaletteForest
                        )
                        Text(
                            text = "AI Heritage Documentation Studio",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PaletteDarkGreen
                        )
                    }

                    Surface(
                        color = PaletteMintCard,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Step ${currentStep + 1} of 3",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteForest,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                0 -> {
                    // ── Step 0: Artisan Capture (Photo + Voice + Notes) ──────────
                    item {
                        Surface(
                            color = PaletteMintCard,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Architecture,
                                    contentDescription = null,
                                    tint = PaletteForest,
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        text = "Preserve Your Living Knowledge",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteDarkGreen
                                    )
                                    Text(
                                        text = "Capture craft photos and speak in your own language. Gemini AI will structure the cultural taxonomy for your review.",
                                        fontSize = 11.sp,
                                        color = DhaagaTextMedium
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Photo Preview & Select
                        Text(
                            text = "Craft / Technique Photograph",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, DhaagaBorder, RoundedCornerShape(14.dp))
                        ) {
                            AsyncImage(
                                model = selectedPhotoUrl,
                                contentDescription = "Craft Photo",
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Tap to Change Photo",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    item {
                        // Voice Note Recorder (Oral Lore input)
                        Text(
                            text = "Record Oral History / Technique Voice Note",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (artisanVoiceNoteText.isNotBlank()) "Voice Captured" else "Tap Mic to Speak in Hindi / Regional Tongue",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (artisanVoiceNoteText.isNotBlank()) PaletteForest else DhaagaTextMedium
                                    )
                                    IconButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (currentLang == "hi") "hi-IN" else "en-IN")
                                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your craft story, materials, and techniques...")
                                                }
                                                speechLauncher.launch(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Voice input: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(PaletteForest)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Speak",
                                            tint = Color.White
                                        )
                                    }
                                }

                                if (artisanVoiceNoteText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "\"$artisanVoiceNoteText\"",
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = PaletteDarkGreen,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Quick text fields
                        OutlinedTextField(
                            value = craftTitleInput,
                            onValueChange = { craftTitleInput = it },
                            label = { Text("Tradition / Craft Working Title") },
                            placeholder = { Text("e.g. Warli Tarpa Dance Painting") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = communityName,
                            onValueChange = { communityName = it },
                            label = { Text("Community / Clan Lineage") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                currentStep = 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Structure with Gemini AI Engine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                1 -> {
                    // ── Step 1: AI Processing & Multimodal Analysis ──────────────
                    item {
                        LaunchedEffect(Unit) {
                            delay(2200) // Simulated Gemini multimodal reasoning
                            currentStep = 2
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = PaletteForest, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Gemini Multimodal Heritage Pipeline",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Analyzing visual motifs • Transcribing dialect • Matching GI clusters...",
                                fontSize = 12.sp,
                                color = DhaagaTextMedium
                            )
                        }
                    }
                }

                2 -> {
                    // ── Step 2: Artisan Sovereignty & Validation Gate ────────────
                    item {
                        Surface(
                            color = Color(0xFFFFF9E6),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE6C84B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = PaletteDarkGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = "Artisan Validation & Sovereignty Gate",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteDarkGreen
                                    )
                                    Text(
                                        text = "AI is only an assistant. You are the master knowledge holder. Please review and edit the draft below before publishing.",
                                        fontSize = 11.sp,
                                        color = DhaagaTextMedium
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Draft Heritage Information (Editable)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draftCraftNameEn,
                            onValueChange = { draftCraftNameEn = it },
                            label = { Text("Craft Name (English)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draftCraftNameHi,
                            onValueChange = { draftCraftNameHi = it },
                            label = { Text("Craft Name (हिंदी)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draftSummaryEn,
                            onValueChange = { draftSummaryEn = it },
                            label = { Text("Cultural Summary (English)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = draftKahaaniHi,
                            onValueChange = { draftKahaaniHi = it },
                            label = { Text("The Kahaani Lore (हिंदी)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        Surface(
                            color = PaletteMintCard,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = PaletteForest,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Matched GI Registry: $verifiedGiTag",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { currentStep = 0 },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Edit Inputs")
                            }

                            Button(
                                onClick = {
                                    val newCraftId = "heritage_user_${System.currentTimeMillis()}"
                                    val newRecord = CraftHeritageModel(
                                        craftId = newCraftId,
                                        craftNameEn = draftCraftNameEn,
                                        craftNameHi = draftCraftNameHi,
                                        category = "Folk Painting",
                                        state = selectedState,
                                        district = currentUser?.district.takeIf { !it.isNullOrBlank() } ?: "Palghar",
                                        community = communityName,
                                        traditionAgeYears = 2500,
                                        summaryEn = draftSummaryEn,
                                        summaryHi = draftSummaryHi,
                                        storyKahaaniEn = draftKahaaniEn,
                                        storyKahaaniHi = draftKahaaniHi,
                                        culturalSignificance = "Sacred ritual folk art celebrating nature, harvest, and community harmony.",
                                        bannerImageUrl = selectedPhotoUrl,
                                        galleryImageUrls = listOf(selectedPhotoUrl),
                                        giTagNumber = verifiedGiTag,
                                        primaryMasterArtisanId = currentUser?.uid ?: "seller001",
                                        masterArtisanName = currentUser?.name.takeIf { !it.isNullOrBlank() } ?: "Savita Dhodi",
                                        masterArtisanVillage = "${currentUser?.village ?: "Mokhada"}, ${selectedState}",
                                        verifiedByArtisan = true
                                    )
                                    viewModel.publishHeritageRecord(newRecord)
                                    Toast.makeText(context, "Published to Bharat Heritage Archives!", Toast.LENGTH_LONG).show()
                                    onPublished(newCraftId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(2f).height(48.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Publish, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Confirm & Publish Record",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
