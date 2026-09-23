package com.dhaaga.app.ui.seller

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

@Composable
fun AISettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = remember { AppLanguageManager.getCurrentLanguage(context) }

    fun tr(key: String, fallback: String): String =
        AppLanguageManager.translate(key, currentLang, fallback)

    // Magic Hour State
    var selectedGenMode by remember { mutableStateOf(MagicHourService.getGenerationMode(context)) }
    var selectedModel by remember { mutableStateOf(MagicHourService.getSelectedModel(context)) }
    var magicHourKeysText by remember { mutableStateOf(MagicHourService.getKeys(context).joinToString("\n")) }
    var activeKeyIndex by remember { mutableStateOf(MagicHourService.getActiveKeyIndex(context)) }

    // Gemini State
    val activeGeminiKey = GeminiAIService.getApiKey(context)
    var geminiKeyText by remember { mutableStateOf(if (activeGeminiKey.isNotBlank()) activeGeminiKey else GeminiAIService.DEFAULT_API_KEY) }

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(tr("settings_ai_title", "AI Studio & Model Settings"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            Text(tr("settings_ai_sub", "Image generation, API rotation & NLP config"), fontSize = 11.sp, color = DhaagaTextMedium)
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
                    // SECTION 1: IMAGE GENERATION ENGINE (API vs ON-DEVICE)
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
                                Text(tr("settings_engine_title", "Image Generation Engine"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            }

                            // Mode Selector Pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Cloud API Mode
                                val isApi = selectedGenMode == ImageGenerationMode.API
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isApi) DhaagaPrimary else Color.White)
                                        .border(
                                            width = if (isApi) 1.5.dp else 1.dp,
                                            color = if (isApi) DhaagaPrimary else DhaagaDivider,
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
                                            tr("mode_cloud_api", "Cloud API Model"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isApi) Color.White else DhaagaTextDark
                                        )
                                        Text(
                                            tr("mode_cloud_api_sub", "Magic Hour (Default)"),
                                            fontSize = 10.sp,
                                            color = if (isApi) Color.White.copy(alpha = 0.85f) else DhaagaTextMedium
                                        )
                                    }
                                }

                                // On-Device Mode
                                val isOnDevice = selectedGenMode == ImageGenerationMode.ON_DEVICE
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isOnDevice) DhaagaPrimary else Color.White)
                                        .border(
                                            width = if (isOnDevice) 1.5.dp else 1.dp,
                                            color = if (isOnDevice) DhaagaPrimary else DhaagaDivider,
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
                                            tr("mode_on_device", "On-Device"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOnDevice) Color.White else DhaagaTextDark
                                        )
                                        Text(
                                            tr("mode_on_device_sub", "ML Kit Engine"),
                                            fontSize = 10.sp,
                                            color = if (isOnDevice) Color.White.copy(alpha = 0.85f) else DhaagaTextMedium
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = if (selectedGenMode == ImageGenerationMode.API)
                                        com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.WandMagicSparkles
                                    else
                                        com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Bolt,
                                    contentDescription = null,
                                    tint = DhaagaPrimary,
                                    modifier = Modifier.size(13.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedGenMode == ImageGenerationMode.API)
                                        tr("mode_cloud_note", "High-fidelity commercial product editing rendered in the cloud with auto-rotating API keys.")
                                    else
                                        tr("mode_ondevice_note", "Fast local subject isolation without network dependency. Creates pure white cyclorama studio shots."),
                                    fontSize = 11.sp,
                                    color = DhaagaTextMedium
                                )
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // SECTION 2: MAGIC HOUR MODEL SELECTION (When in API mode)
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
                                        Icon(Icons.Default.ModelTraining, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tr("select_ai_model", "Select AI Model"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                    }
                                    Text(tr("free_tier_badge", "Free Tier Compatible"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DhaagaSuccess)
                                }

                                MagicHourService.AVAILABLE_MODELS.forEach { modelInfo ->
                                    val isCurrent = selectedModel == modelInfo.id
                                    val localizedModelName = when (modelInfo.id) {
                                        "qwen-edit" -> tr("model_qwen_name", modelInfo.name)
                                        "flux-2-klein" -> tr("model_flux_name", modelInfo.name)
                                        "krea-2" -> tr("model_krea_name", modelInfo.name)
                                        else -> modelInfo.name
                                    }
                                    val localizedCostTag = when (modelInfo.id) {
                                        "qwen-edit" -> tr("model_qwen_cost", modelInfo.costTag)
                                        "flux-2-klein" -> tr("model_flux_cost", modelInfo.costTag)
                                        "krea-2" -> tr("model_krea_cost", modelInfo.costTag)
                                        else -> modelInfo.costTag
                                    }
                                    val localizedDescription = when (modelInfo.id) {
                                        "qwen-edit" -> tr("model_qwen_desc", modelInfo.description)
                                        "flux-2-klein" -> tr("model_flux_desc", modelInfo.description)
                                        "krea-2" -> tr("model_krea_desc", modelInfo.description)
                                        else -> modelInfo.description
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedModel = modelInfo.id
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
                                                    Text(localizedModelName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFFE8F5E9))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(localizedCostTag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DhaagaSuccess)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(localizedDescription, fontSize = 10.sp, color = DhaagaTextMedium)
                                            }

                                            RadioButton(
                                                selected = isCurrent,
                                                onClick = {
                                                    selectedModel = modelInfo.id
                                                    saveSuccess = false
                                                },
                                                colors = RadioButtonDefaults.colors(selectedColor = DhaagaPrimary)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ----------------------------------------------------
                        // SECTION 3: MULTI-KEY POOL & AUTO-ROTATION
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
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tr("settings_keys_title", "Magic Hour API Keys (Auto-Rotating)"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
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
                                        Text("${tr("reset_btn", "Reset")} (${MagicHourService.BUNDLED_KEYS.size} ${tr("keys_count_label", "Keys")})", fontSize = 11.sp, color = DhaagaPrimary)
                                    }
                                }

                                val parsedKeys = magicHourKeysText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }

                                // Key Status Pill & Active Key Indicator
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${tr("configured_label", "Configured")}: ${parsedKeys.size} ${tr("keys_count_label", "key(s)")} | ${tr("active_slot_label", "Active Slot")}: #${activeKeyIndex + 1}",
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
                                        Text(tr("rotate_key_btn", "Rotate Key"), fontSize = 10.sp, color = DhaagaTextDark)
                                    }
                                }

                                Text(
                                    tr("keys_help_text", "Enter keys separated by newline. If a key runs out of credits (HTTP 402), Dhaaga automatically switches to the next slot seamlessly:"),
                                    fontSize = 10.5.sp,
                                    color = DhaagaTextMedium
                                )

                                OutlinedTextField(
                                    value = magicHourKeysText,
                                    onValueChange = {
                                        magicHourKeysText = it
                                        saveSuccess = false
                                    },
                                    placeholder = { Text("mhk_live_...\nmhk_live_...", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DhaagaPrimary,
                                        cursorColor = DhaagaPrimary
                                    )
                                )
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // SECTION 4: GOOGLE GEMINI NLP & CATALOGING KEY
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
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(tr("gemini_nlp_title", "Google Gemini (Catalog & Voice)"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
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
                                        Text(tr("use_bundled_btn", "Use Bundled"), fontSize = 11.sp, color = DhaagaPrimary)
                                    }
                                }
                            }

                            Text(
                                tr("gemini_nlp_sub", "Powers Multilingual Auto-Cataloger (EN+HI) and Dynamic Pricing Assistant."),
                                fontSize = 10.5.sp,
                                color = DhaagaTextMedium
                            )

                            OutlinedTextField(
                                value = geminiKeyText,
                                onValueChange = {
                                    geminiKeyText = it
                                    saveSuccess = false
                                },
                                placeholder = { Text(tr("paste_gemini_key", "Paste Gemini API Key"), fontSize = 11.sp) },
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
                                        Icon(Icons.Default.RestartAlt, contentDescription = tr("reset_btn", "Reset"), tint = DhaagaTextMedium)
                                    }
                                }
                            )
                        }
                    }

                    if (saveSuccess) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.CircleCheck,
                                contentDescription = null,
                                tint = DhaagaSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                tr("settings_saved_msg", "AI Studio & Model configurations saved successfully!"),
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
                            // Save Magic Hour Preferences
                            MagicHourService.setGenerationMode(context, selectedGenMode)
                            MagicHourService.setSelectedModel(context, selectedModel)
                            val keyList = magicHourKeysText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }
                            MagicHourService.saveKeys(context, keyList)

                            // Save Gemini Preference
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
