package com.dhaaga.app.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dhaaga.app.data.repository.GeminiAIService
import com.dhaaga.app.ui.theme.*

@Composable
fun AISettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var apiKeyText by remember { mutableStateOf(GeminiAIService.getApiKey(context)) }
    var saveSuccess by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = DhaagaSurface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Studio Configuration", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DhaagaTextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Dhaaga Cloud AI Key:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DhaagaTextDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = apiKeyText,
                    onValueChange = {
                        apiKeyText = it
                        saveSuccess = false
                    },
                    placeholder = { Text("Paste your AI API key here", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DhaagaPrimary,
                        cursorColor = DhaagaPrimary
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            GeminiAIService.resetApiKey(context)
                            apiKeyText = GeminiAIService.DEFAULT_API_KEY
                            saveSuccess = true
                        }) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = DhaagaTextMedium)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Models info badges
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DhaagaCardBg, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("• Image Studio: AI Neural Enhancement", fontSize = 11.sp, color = DhaagaTextMedium)
                    Text("• NLP & Pricing: AI Voice & Intelligence", fontSize = 11.sp, color = DhaagaTextMedium)
                }

                if (saveSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("✅ Configuration updated successfully!", fontSize = 12.sp, color = DhaagaSuccess)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", color = DhaagaTextDark)
                    }

                    Button(
                        onClick = {
                            GeminiAIService.setApiKey(context, apiKeyText)
                            saveSuccess = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                    ) {
                        Text("Save Key", color = Color.White)
                    }
                }
            }
        }
    }
}
