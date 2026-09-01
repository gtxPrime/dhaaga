package com.dhaaga.app.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.ui.theme.*
import java.util.Locale

@Composable
fun AudioGuideCard(
    englishText: String,
    hindiText: String,
    modifier: Modifier = Modifier,
    initialLanguage: String = "hi",
    autoPlay: Boolean = true
) {
    val context = LocalContext.current
    var audioLang by remember { mutableStateOf(if (initialLanguage == "en") "en" else "hi") }
    var isMuted by remember { mutableStateOf(false) }

    val isSpeaking by com.dhaaga.app.utils.AppTtsManager.isSpeaking.collectAsState()
    val isTtsReady by com.dhaaga.app.utils.AppTtsManager.isReady.collectAsState()

    // Ensure AppTtsManager is initialized
    LaunchedEffect(Unit) {
        com.dhaaga.app.utils.AppTtsManager.init(context)
    }

    // Function to speak instantly without cold start
    val speakCurrent = {
        if (!isMuted) {
            val textToSpeak = if (audioLang == "hi") hindiText else englishText
            com.dhaaga.app.utils.AppTtsManager.speak(textToSpeak, audioLang)
        }
    }

    // Immediate playback on mount or language switch
    LaunchedEffect(audioLang, isMuted, isTtsReady) {
        if (isMuted) {
            com.dhaaga.app.utils.AppTtsManager.stop()
        } else if (autoPlay) {
            speakCurrent()
        }
    }

    // Stop speaking when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            com.dhaaga.app.utils.AppTtsManager.stop()
        }
    }

    // Pulse animation when speaking
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking && !isMuted) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker icon & Status indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(if (isMuted) Color(0xFFEEEEEE) else PaletteForest.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.RecordVoiceOver,
                            contentDescription = if (isMuted) "Audio Off" else "Voice Guide",
                            tint = if (isMuted) Color.Gray else PaletteForest,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (audioLang == "hi") "आवाज सहायता (Voice Guide)" else "Audio Guide (Voice Help)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            text = if (isMuted) "ऑडियो बंद है (Muted)" else if (isSpeaking) "सुनिए • Speaking..." else "टैप करें सुनने के लिए",
                            fontSize = 11.sp,
                            color = if (isMuted) Color.Gray else PaletteForest,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Audio ON / OFF Toggle Button
                Surface(
                    onClick = {
                        isMuted = !isMuted
                        if (isMuted) {
                            com.dhaaga.app.utils.AppTtsManager.stop()
                        } else {
                            speakCurrent()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isMuted) Color(0xFFFFEBEE) else PaletteGreenTint,
                    border = BorderStroke(1.dp, if (isMuted) Color(0xFFEF9A9A) else PaletteForest.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = if (isMuted) Color(0xFFD32F2F) else PaletteForest,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMuted) "Audio OFF" else "Audio ON",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMuted) Color(0xFFD32F2F) else PaletteDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Language Switcher Chips: English vs Hindi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "भाषा चुनें / Language:",
                    fontSize = 11.5.sp,
                    color = PaletteSage,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageChip(
                        label = "हिंदी (Hindi)",
                        isSelected = audioLang == "hi",
                        onClick = {
                            audioLang = "hi"
                            if (isMuted) isMuted = false
                        }
                    )
                    LanguageChip(
                        label = "English",
                        isSelected = audioLang == "en",
                        onClick = {
                            audioLang = "en"
                            if (isMuted) isMuted = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) PaletteForest else Color.White,
        border = BorderStroke(1.dp, if (isSelected) PaletteForest else PaletteSage.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else PaletteDarkGreen,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
