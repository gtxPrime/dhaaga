package com.dhaaga.app.ui.seller

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.dhaaga.app.data.repository.CatalogResult
import com.dhaaga.app.data.repository.GeminiAIService
import com.dhaaga.app.ui.components.AudioRecorderHelper
import com.dhaaga.app.ui.theme.*
import kotlinx.coroutines.launch

data class LanguageOption(val name: String, val code: String)

val SUPPORTED_LANGUAGES = listOf(
    LanguageOption("हिंदी (Hindi)", "hi-IN"),
    LanguageOption("English", "en-IN"),
    LanguageOption("বাংলা (Bengali)", "bn-IN"),
    LanguageOption("தமிழ் (Tamil)", "ta-IN"),
    LanguageOption("मराठी (Marathi)", "mr-IN"),
    LanguageOption("ગુજરાતી (Gujarati)", "gu-IN"),
    LanguageOption("తెలుగు (Telugu)", "te-IN")
)

@Composable
fun VoiceCatalogerDialog(
    productBitmap: Bitmap?,
    onDismiss: () -> Unit,
    onCatalogGenerated: (CatalogResult) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedLanguage by remember { mutableStateOf(SUPPORTED_LANGUAGES[0]) }
    var transcriptText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isAnalyzingAI by remember { mutableStateOf(false) }
    var recordingRms by remember { mutableStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val recorderHelper = remember {
        AudioRecorderHelper(
            context = context,
            onPartialResult = { partial -> transcriptText = partial },
            onFinalResult = { finalResult ->
                transcriptText = finalResult
                isRecording = false
            },
            onError = { err ->
                errorMessage = err
                isRecording = false
            },
            onRmsUpdate = { rms -> recordingRms = rms }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            recorderHelper.stopListening()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isRecording = true
            errorMessage = null
            recorderHelper.startListening(selectedLanguage.code)
        } else {
            errorMessage = "Microphone permission is required to record voice notes."
        }
    }

    // Pulse animation for recording
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(
        onDismissRequest = {
            recorderHelper.stopListening()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = DhaagaSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(DhaagaPrimary, DhaagaPrimaryLight))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Multilingual Auto-Cataloger", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            Text("AI Multilingual Voice Intelligence", fontSize = 12.sp, color = DhaagaPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DhaagaTextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Language Selection Chips
                Text(
                    "Speak in your preferred language:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DhaagaTextDark,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SUPPORTED_LANGUAGES.forEach { lang ->
                        val isSelected = selectedLanguage.code == lang.code
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedLanguage = lang
                                if (isRecording) {
                                    recorderHelper.stopListening()
                                    recorderHelper.startListening(lang.code)
                                }
                            },
                            label = { Text(lang.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DhaagaPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Pulsing Mic Record Button
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) Brush.radialGradient(listOf(Color(0xFFE53935), Color(0xFFC62828)))
                            else Brush.radialGradient(listOf(DhaagaPrimaryLight, DhaagaPrimary))
                        )
                        .clickable {
                            if (isRecording) {
                                recorderHelper.stopListening()
                                isRecording = false
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    errorMessage = null
                                    isRecording = true
                                    recorderHelper.startListening(selectedLanguage.code)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Record",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    if (isRecording) "Listening... Speak now about your craft" else "Tap to Speak (Voice Note)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isRecording) Color(0xFFE53935) else DhaagaPrimary
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        errorMessage ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Transcript / Input Box
                OutlinedTextField(
                    value = transcriptText,
                    onValueChange = { transcriptText = it },
                    label = { Text("Spoken Voice Transcript / Artisan Note") },
                    placeholder = { Text("e.g. मैंने हाथ से यह बागरू ब्लॉक प्रिंट दुपट्टा बनाया है प्राकृतिक रंगों से 2.5 मीटर लंबा है", fontSize = 12.sp, color = DhaagaTextLight) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp, max = 130.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DhaagaPrimary,
                        focusedLabelColor = DhaagaPrimary,
                        cursorColor = DhaagaPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick sample voice note template button for convenience
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            transcriptText = "मैंने शुद्ध खादी कॉटन पर हाथ से बागरू डाबू ब्लॉक प्रिंट किया है प्राकृतिक नील और कत्था रंग से। 2.5 मीटर लंबाई है। 3 दिन में तैयार किया।"
                        }
                    ) {
                        Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(16.dp), tint = DhaagaPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use Sample Artisan Note", fontSize = 12.sp, color = DhaagaPrimary, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action: Generate with Gemini
                Button(
                    onClick = {
                        if (transcriptText.isNotBlank() && !isAnalyzingAI) {
                            coroutineScope.launch {
                                isAnalyzingAI = true
                                val result = GeminiAIService.autoCatalogProduct(
                                    context = context,
                                    inputSpeechOrText = transcriptText,
                                    productImageBitmap = productBitmap
                                )
                                isAnalyzingAI = false
                                result.onSuccess { catalog ->
                                    onCatalogGenerated(catalog)
                                    onDismiss()
                                }
                            }
                        }
                    },
                    enabled = transcriptText.isNotBlank() && !isAnalyzingAI,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                ) {
                    if (isAnalyzingAI) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("AI Translating & Cataloging...", color = Color.White, fontSize = 14.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Bilingual Catalog (EN + HI)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
