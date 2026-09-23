package com.dhaaga.app.ui.seller

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dhaaga.app.data.repository.ImageGenerationMode
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager
import kotlinx.coroutines.delay

/**
 * Animated, language-dynamic loading dialog shown while AI studio photography is generating.
 * Cycles dynamically through staging phases (fixing lights, removing glare, staging backdrop, etc.)
 * in the artisan's preferred language.
 */
@Composable
fun StudioLoadingDialog(
    isGenerating: Boolean,
    generationMode: ImageGenerationMode,
    activeModel: String,
    activeKeySlot: Int,
    backendStatusMessage: String,
    onDismissRequest: () -> Unit = {}
) {
    if (!isGenerating) return

    val context = LocalContext.current
    val currentLang = remember { AppLanguageManager.getCurrentLanguage(context) }
    val localizedSteps = remember(currentLang) { AppLanguageManager.getStudioLoadingSteps(currentLang) }
    val titleText = remember(currentLang) {
        AppLanguageManager.translate("studio_loading_title", currentLang, "AI Studio Photography")
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }

    // Step cycle timer
    LaunchedEffect(isGenerating) {
        currentStepIndex = 0
        while (isGenerating) {
            delay(2400L)
            currentStepIndex = (currentStepIndex + 1) % localizedSteps.size
        }
    }

    // Breathing pulse animation for studio ring
    val infiniteTransition = rememberInfiniteTransition(label = "studio_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    val stepIcons: List<ImageVector> = listOf(
        Icons.Default.LightMode,       // Fixing lights
        Icons.Default.FlashOff,        // Eliminating glare
        Icons.Default.FilterFrames,    // Staging backdrop & table
        Icons.Default.BlurOn,          // Soft drop shadows
        Icons.Default.AutoFixHigh,     // Polishing textures
        Icons.Default.PhotoCamera,     // Rendering hero photo
        Icons.Default.AutoAwesome      // Final touches
    )

    val currentIcon = stepIcons.getOrElse(currentStepIndex) { Icons.Default.AutoAwesome }
    val currentStepText = localizedSteps.getOrElse(currentStepIndex) { localizedSteps.first() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = DhaagaSurface,
            tonalElevation = 10.dp,
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(DhaagaPrimaryLight.copy(alpha = 0.7f), DhaagaPrimary.copy(alpha = 0.4f))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animated Pulsing Studio Aperture
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(92.dp)
                ) {
                    // Outer Glowing Aura
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(DhaagaPrimary.copy(alpha = auraAlpha * 0.35f))
                    )

                    // Secondary Inner Ring
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        DhaagaPrimary.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(2.dp, DhaagaPrimary.copy(alpha = 0.6f), CircleShape)
                    )

                    // Core Glowing Circle with Icon
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(DhaagaPrimary, DhaagaPrimaryLight)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentIcon,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            label = "step_icon"
                        ) { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Title & Subtitle Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = titleText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark,
                        textAlign = TextAlign.Center
                    )

                    // Engine Badge Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DhaagaCardBg)
                            .border(1.dp, DhaagaDivider.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(DhaagaSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (generationMode == ImageGenerationMode.API)
                                    "Magic Hour API • $activeModel (${AppLanguageManager.translate("slot_label", currentLang, "Slot")} #$activeKeySlot)"
                                else
                                    AppLanguageManager.translate("mode_on_device_sub", currentLang, "On-Device Studio • ML Kit Engine"),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DhaagaPrimary
                            )
                        }
                    }
                }

                // Smooth Linear Step Progress Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = (currentStepIndex + 1f) / localizedSteps.size.toFloat(),
                        animationSpec = tween(500, easing = LinearOutSlowInEasing),
                        label = "studio_progress"
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = DhaagaPrimary,
                        trackColor = DhaagaDivider.copy(alpha = 0.35f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${AppLanguageManager.translate("stage_label", currentLang, "Stage")} ${currentStepIndex + 1} ${AppLanguageManager.translate("of_label", currentLang, "of")} ${localizedSteps.size}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = DhaagaTextMedium
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DhaagaPrimary
                        )
                    }
                }

                // Dynamic Localized Status Text (Fixing lights, removing glare, etc.)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentStepText,
                        transitionSpec = {
                            (slideInVertically { height -> height / 2 } + fadeIn(tween(400)))
                                .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(tween(400)))
                        },
                        label = "stage_text"
                    ) { text ->
                        Text(
                            text = text,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DhaagaTextDark,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Real-time backend status message (e.g. uploading bytes, rotating keys)
                if (backendStatusMessage.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF7F6F2))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = DhaagaTextMedium,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = backendStatusMessage,
                                fontSize = 10.5.sp,
                                color = DhaagaTextMedium,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
