package com.dhaaga.app.ui.seller

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dhaaga.app.data.repository.GeminiAIService
import com.dhaaga.app.data.repository.ImageGenerationMode
import com.dhaaga.app.data.repository.MagicHourService
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AISettingsDialog(
    initialShowDeveloperKeys: Boolean = false,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = remember { AppLanguageManager.getCurrentLanguage(context) }

    fun tr(key: String, fallback: String): String =
        AppLanguageManager.translate(key, currentLang, fallback)

    // Generation Mode & Quality State
    var selectedGenMode by remember { mutableStateOf(MagicHourService.getGenerationMode(context)) }
    var selectedModel by remember { mutableStateOf(MagicHourService.getSelectedModel(context)) }
    var magicHourKeysText by remember { mutableStateOf(MagicHourService.getKeys(context).joinToString("\n")) }
    var activeKeyIndex by remember { mutableStateOf(MagicHourService.getActiveKeyIndex(context)) }

    // Fast Gen Catalog State
    val activeGeminiKey = GeminiAIService.getApiKey(context)
    var geminiKeyText by remember { mutableStateOf(if (activeGeminiKey.isNotBlank()) activeGeminiKey else GeminiAIService.DEFAULT_API_KEY) }

    // Developer Mode: Revealed when triggered (e.g. on avatar pfp long press)
    var showDeveloperKeys by remember { mutableStateOf(initialShowDeveloperKeys) }
    var saveSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DhaagaSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header (Long press to toggle developer mode)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = {
                                showDeveloperKeys = !showDeveloperKeys
                                Toast.makeText(
                                    context,
                                    if (showDeveloperKeys) "Developer Mode: API Keys Unlocked" else "Developer Mode: API Keys Secured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onClick = {}
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DhaagaPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (showDeveloperKeys) Icons.Default.LockOpen else Icons.Default.Tune,
                                contentDescription = null,
                                tint = DhaagaPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                tr("settings_ai_title", "Studio Quality & Speed"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DhaagaTextDark
                            )
                            Text(
                                tr("settings_ai_sub", "Fast Gen & Better Quality settings"),
                                fontSize = 11.sp,
                                color = DhaagaTextMedium
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = tr("close_btn", "Close"), tint = DhaagaTextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ----------------------------------------------------
                    // SECTION 1: STUDIO GENERATION SPEED & QUALITY
                    // ----------------------------------------------------
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                        border = BorderStroke(1.dp, DhaagaDivider.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Studio Generation Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            }

                            // Mode Selector Pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Better Quality Mode
                                val isBetterQuality = selectedGenMode == ImageGenerationMode.API
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isBetterQuality) DhaagaPrimary else Color.White)
                                        .border(
                                            width = if (isBetterQuality) 1.5.dp else 1.dp,
                                            color = if (isBetterQuality) DhaagaPrimary else DhaagaDivider,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedGenMode = ImageGenerationMode.API
                                            saveSuccess = false
                                        }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Better Quality",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBetterQuality) Color.White else DhaagaTextDark
                                        )
                                        Text(
                                            "Studio Lighting (Default)",
                                            fontSize = 10.sp,
                                            color = if (isBetterQuality) Color.White.copy(alpha = 0.85f) else DhaagaTextMedium
                                        )
                                    }
                                }

                                // Fast Gen Mode
                                val isFastGen = selectedGenMode == ImageGenerationMode.ON_DEVICE
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isFastGen) DhaagaPrimary else Color.White)
                                        .border(
                                            width = if (isFastGen) 1.5.dp else 1.dp,
                                            color = if (isFastGen) DhaagaPrimary else DhaagaDivider,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedGenMode = ImageGenerationMode.ON_DEVICE
                                            saveSuccess = false
                                        }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Fast Gen",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFastGen) Color.White else DhaagaTextDark
                                        )
                                        Text(
                                            "Instant Local Processing",
                                            fontSize = 10.sp,
                                            color = if (isFastGen) Color.White.copy(alpha = 0.85f) else DhaagaTextMedium
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = if (selectedGenMode == ImageGenerationMode.API)
                                        Icons.Default.WorkspacePremium
                                    else
                                        Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = DhaagaPrimary,
                                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedGenMode == ImageGenerationMode.API)
                                        "High-fidelity commercial product editing with studio lighting and natural textures."
                                    else
                                        "Fast local subject isolation with instant pure white cyclorama studio finish.",
                                    fontSize = 11.sp,
                                    color = DhaagaTextMedium
                                )
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // SECTION 2: QUALITY LEVEL SELECTION (When in Better Quality mode)
                    // ----------------------------------------------------
                    if (selectedGenMode == ImageGenerationMode.API) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                            border = BorderStroke(1.dp, DhaagaDivider.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Layers, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Detail & Quality Preset", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                    }
                                    Text("Free Tier Included", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DhaagaSuccess)
                                }

                                val presets = listOf(
                                    Triple("qwen-edit", "Balanced Studio (Better Quality)", "High-precision e-commerce product staging & authentic shadows"),
                                    Triple("flux-2-klein", "Ultra-Fast Mode (Fast Gen)", "Lightweight and lightning-fast studio rendering"),
                                    Triple("krea-2", "High Detail Mode (Studio Quality)", "Vibrant textures and artistic commercial enhancements")
                                )

                                presets.forEach { (modelId, presetName, presetDesc) ->
                                    val isCurrent = selectedModel == modelId
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedModel = modelId
                                                saveSuccess = false
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isCurrent) DhaagaPrimary.copy(alpha = 0.1f) else Color.White,
                                        border = BorderStroke(
                                            width = if (isCurrent) 1.5.dp else 1.dp,
                                            color = if (isCurrent) DhaagaPrimary else DhaagaDivider.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(presetName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                                    if (modelId == "qwen-edit") {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(Color(0xFFE8F5E9))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("Recommended", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DhaagaSuccess)
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(presetDesc, fontSize = 10.sp, color = DhaagaTextMedium)
                                            }

                                            RadioButton(
                                                selected = isCurrent,
                                                onClick = {
                                                    selectedModel = modelId
                                                    saveSuccess = false
                                                },
                                                colors = RadioButtonDefaults.colors(selectedColor = DhaagaPrimary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // SECTION 3: SYSTEM ENGINE STATUS
                    // ----------------------------------------------------
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                        border = BorderStroke(1.dp, DhaagaDivider.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(DhaagaSuccess)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Fast Gen Engine Status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DhaagaSuccess.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("Operational", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DhaagaSuccess)
                                }
                            }

                            Text(
                                "Intelligent Multilingual Auto-Cataloger and Dynamic Pricing engines are connected and optimized for high-speed catalog generation.",
                                fontSize = 11.sp,
                                color = DhaagaTextMedium
                            )
                        }
                    }

                    // Developer Credentials (only visible when unlocked via Avatar PFP long-press)
                    if (showDeveloperKeys) {
                        // Cloud Keys
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                            border = BorderStroke(1.dp, DhaagaDivider.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Studio Cloud Rotation Keys", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                    }
                                    TextButton(
                                        onClick = {
                                            MagicHourService.resetToDefaultKeys(context)
                                            magicHourKeysText = MagicHourService.BUNDLED_KEYS.joinToString("\n")
                                            activeKeyIndex = 0
                                            selectedModel = MagicHourService.DEFAULT_MODEL
                                            saveSuccess = true
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Reset (${MagicHourService.BUNDLED_KEYS.size} Keys)", fontSize = 11.sp, color = DhaagaPrimary)
                                    }
                                }

                                val parsedKeys = magicHourKeysText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Configured: ${parsedKeys.size} key(s) | Active Slot: #${activeKeyIndex + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DhaagaPrimary
                                    )
                                    OutlinedButton(
                                        onClick = {
                                            activeKeyIndex = (activeKeyIndex + 1) % maxOf(1, parsedKeys.size)
                                            val prefs = context.getSharedPreferences("dhaaga_ai_prefs", android.content.Context.MODE_PRIVATE)
                                            prefs.edit().putInt("magichour_active_key_index", activeKeyIndex).apply()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rotate Key", fontSize = 10.sp, color = DhaagaTextDark)
                                    }
                                }

                                OutlinedTextField(
                                    value = magicHourKeysText,
                                    onValueChange = {
                                        magicHourKeysText = it
                                        saveSuccess = false
                                    },
                                    placeholder = { Text("Enter cloud service keys (one per line)...", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DhaagaPrimary,
                                        cursorColor = DhaagaPrimary
                                    )
                                )
                            }
                        }

                        // Catalog Service Key
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                            border = BorderStroke(1.dp, DhaagaDivider.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Fast Gen Catalog Access Key", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                    }
                                    if (GeminiAIService.DEFAULT_API_KEY.isNotBlank() && geminiKeyText != GeminiAIService.DEFAULT_API_KEY) {
                                        TextButton(
                                            onClick = {
                                                geminiKeyText = GeminiAIService.DEFAULT_API_KEY
                                                GeminiAIService.setApiKey(context, geminiKeyText)
                                                saveSuccess = true
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Use Bundled", fontSize = 11.sp, color = DhaagaPrimary)
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = geminiKeyText,
                                    onValueChange = {
                                        geminiKeyText = it
                                        saveSuccess = false
                                    },
                                    placeholder = { Text("Paste catalog service access key", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DhaagaPrimary,
                                        cursorColor = DhaagaPrimary
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            GeminiAIService.resetApiKey(context)
                                            geminiKeyText = GeminiAIService.DEFAULT_API_KEY
                                            saveSuccess = true
                                        }) {
                                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = DhaagaTextMedium)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if (saveSuccess) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DhaagaSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Studio Quality & Speed preferences saved successfully!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DhaagaSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(tr("close_btn", "Close"), color = DhaagaTextDark)
                    }

                    Button(
                        onClick = {
                            MagicHourService.setGenerationMode(context, selectedGenMode)
                            MagicHourService.setSelectedModel(context, selectedModel)
                            val keyList = magicHourKeysText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }
                            MagicHourService.saveKeys(context, keyList)
                            GeminiAIService.setApiKey(context, geminiKeyText)
                            saveSuccess = true
                        },
                        modifier = Modifier.weight(1.3f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(tr("save_settings_btn", "Save Settings"), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
