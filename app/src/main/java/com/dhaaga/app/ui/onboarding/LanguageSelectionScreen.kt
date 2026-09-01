package com.dhaaga.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.ui.theme.*

@Composable
fun LanguageSelectionScreen(
    viewModel: com.dhaaga.app.AppViewModel? = null,
    onLanguageSelected: (String) -> Unit
) {
    var selectedLang by remember { mutableStateOf(viewModel?.selectedLanguage?.value ?: "en") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DhaagaBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header gradient extending to top edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DhaagaPrimary, DhaagaPrimaryLight)
                        )
                    )
                    .statusBarsPadding()
                    .padding(top = 24.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.dhaaga.app.R.drawable.dhaaga_logo),
                        contentDescription = "Dhaaga",
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Choose your language",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "अपनी भाषा चुनें",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audio Onboarding Guide
            com.dhaaga.app.ui.components.AudioGuideCard(
                englishText = "Welcome to Dhaaga. Please choose your preferred language to continue.",
                hindiText = "धागा में आपका स्वागत है। आगे बढ़ने के लिए कृपया अपनी पसंदीदा भाषा चुनें।",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Language grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(MockData.languages) { (langName, code) ->
                    val isSelected = selectedLang == code
                    val borderColor by animateColorAsState(
                        if (isSelected) DhaagaPrimary else DhaagaDivider.copy(alpha = 0.4f),
                        label = "border"
                    )
                    val bgColor by animateColorAsState(
                        if (isSelected) DhaagaPrimary.copy(alpha = 0.1f) else DhaagaSurface,
                        label = "bg"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedLang = code
                                viewModel?.setLanguage(code)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = langName,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DhaagaPrimary else DhaagaTextDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Continue button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = {
                        viewModel?.setLanguage(selectedLang)
                        onLanguageSelected(selectedLang)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DhaagaPrimary
                    )
                ) {
                    val continueText = com.dhaaga.app.utils.AppLanguageManager.translate("continue_btn", selectedLang, "Continue")
                    Text(
                        text = "$continueText • ${com.dhaaga.app.utils.AppLanguageManager.getLanguageName(selectedLang).substringBefore(" ")}",
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
            }
        }
    }
}
