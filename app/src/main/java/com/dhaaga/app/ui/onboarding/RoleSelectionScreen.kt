package com.dhaaga.app.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit,
    viewModel: com.dhaaga.app.AppViewModel? = null
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DhaagaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App icon mark
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DhaagaPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = viewModel?.tr("who_are_you", "Who are you?") ?: "Who are you?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DhaagaTextDark
            )
            Text(
                text = "आप कौन हैं?",
                fontSize = 16.sp,
                color = DhaagaTextMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Guide with Hindi / English toggle and Audio ON/OFF switch
            com.dhaaga.app.ui.components.AudioGuideCard(
                englishText = "Welcome to Dhaaga! Please choose your role to continue. If you make and sell handmade crafts, select 'I am an Artisan'. If you want to explore and purchase authentic crafts, select 'I am a Buyer'. Then tap Continue.",
                hindiText = "नमस्ते! धागा में आपका स्वागत है। कृपया अपनी भूमिका चुनें। यदि आप हस्तशिल्प बनाते और बेचते हैं, तो 'कारीगर' चुनें। यदि आप प्रामाणिक कला खरीदना चाहते हैं, तो 'खरीदार' चुनें। इसके बाद नीचे 'आगे बढ़ें' पर टैप करें।",
                initialLanguage = viewModel?.selectedLanguage?.value ?: "hi"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Uniform Role Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleCard(
                    icon = Icons.Default.Storefront,
                    title = "I'm an Artisan",
                    subtitle = "I make & sell crafts",
                    hindiText = "मैं कारीगर हूँ",
                    role = "seller",
                    isSelected = selectedRole == "seller",
                    modifier = Modifier.weight(1f),
                    onSelect = { selectedRole = "seller" }
                )
                RoleCard(
                    icon = Icons.Default.ShoppingBag,
                    title = "I'm a Buyer",
                    subtitle = "I buy handmade crafts",
                    hindiText = "मैं खरीदार हूँ",
                    role = "buyer",
                    isSelected = selectedRole == "buyer",
                    modifier = Modifier.weight(1f),
                    onSelect = { selectedRole = "buyer" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Features callout with vector icons
            if (selectedRole == "seller") {
                InfoCard(
                    icon = Icons.Default.AutoAwesome,
                    text = "AI-powered product listing, GI Tag detection, voice cataloger, and seller dashboard"
                )
            } else if (selectedRole == "buyer") {
                InfoCard(
                    icon = Icons.Default.Search,
                    text = "Browse authentic GI-certified crafts, voice search, bulk enquiry, and order tracking"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { selectedRole?.let { onRoleSelected(it) } },
                enabled = selectedRole != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DhaagaPrimary,
                    disabledContainerColor = DhaagaDivider.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = viewModel?.tr("continue_btn", "Continue") ?: "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    hindiText: String,
    role: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val bgBrush = if (isSelected) {
        Brush.verticalGradient(listOf(DhaagaPrimary.copy(alpha = 0.08f), DhaagaPrimaryLight.copy(alpha = 0.08f)))
    } else {
        Brush.verticalGradient(listOf(DhaagaSurface, DhaagaSurface))
    }
    val borderColor by animateColorAsState(
        if (isSelected) DhaagaPrimary else DhaagaDivider.copy(alpha = 0.3f),
        label = "border"
    )

    Box(
        modifier = modifier
            .height(190.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(bgBrush)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onSelect() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) DhaagaPrimary.copy(alpha = 0.15f) else DhaagaBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) DhaagaPrimary else DhaagaTextMedium,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) DhaagaPrimary else DhaagaTextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = DhaagaTextMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hindiText,
                fontSize = 12.sp,
                color = DhaagaTextLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DhaagaAccent.copy(alpha = 0.08f))
            .border(1.dp, DhaagaAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DhaagaAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                color = DhaagaTextMedium,
                lineHeight = 20.sp
            )
        }
    }
}
