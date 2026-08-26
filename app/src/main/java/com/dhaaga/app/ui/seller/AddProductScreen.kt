package com.dhaaga.app.ui.seller

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.ui.onboarding.DhaagaTextField
import com.dhaaga.app.ui.onboarding.toTitleCase
import com.dhaaga.app.ui.theme.*

@Composable
fun AddProductScreen(onBack: () -> Unit, onPublish: () -> Unit) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var titleHi by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var craftType by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var isProcessingAI by remember { mutableStateOf(false) }
    var aiCompleted by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Scaffold(
        containerColor = DhaagaBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DhaagaPrimary, DhaagaPrimaryLight)))
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column {
                        Text("Add Product", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("List in under 5 minutes", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Step 1: Photo
            StepCard(
                step = "1",
                title = "Product Photo",
                icon = Icons.Default.PhotoCamera,
                subtitle = "Take or upload product photo"
            ) {
                if (selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // AI enhance option
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = {}) {
                                Text("Upload As-Is", color = Color.White, fontSize = 12.sp)
                            }
                            TextButton(
                                onClick = {
                                    isProcessingAI = true
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DhaagaPrimaryLight, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AI Enhance", color = DhaagaPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PhotoOptionButton(
                            icon = Icons.Default.PhotoCamera,
                            label = "Camera",
                            modifier = Modifier.weight(1f),
                            onClick = { /* Camera launch */ }
                        )
                        PhotoOptionButton(
                            icon = Icons.Default.PhotoLibrary,
                            label = "Gallery",
                            modifier = Modifier.weight(1f),
                            onClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }
                }

                // AI Processing indicator
                if (isProcessingAI) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = DhaagaPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "AI enhancing your photo...",
                            fontSize = 13.sp,
                            color = DhaagaPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Step 2: Voice / Text Description
            StepCard(
                step = "2",
                title = "Describe Your Product",
                icon = Icons.Default.Mic,
                subtitle = "Speak in your language or type"
            ) {
                // Voice record button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DhaagaPrimary.copy(alpha = 0.08f))
                        .border(1.dp, DhaagaPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Hold to record in your language",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DhaagaPrimary
                            )
                            Text(
                                "Hindi, Tamil, Bengali, or any of 22 languages",
                                fontSize = 12.sp,
                                color = DhaagaTextMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("— OR type manually —", fontSize = 12.sp, color = DhaagaTextLight, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Step 3: Attributes (editable)
            StepCard(
                step = "3",
                title = "Product Details",
                icon = Icons.Default.Edit,
                subtitle = "AI will auto-fill after voice/photo. Edit as needed."
            ) {
                DhaagaTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Product Title (English)",
                    placeholder = "e.g. Warli Tribal Painting",
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(12.dp))
                DhaagaTextField(
                    value = titleHi,
                    onValueChange = { titleHi = it },
                    label = "Title (Hindi)",
                    placeholder = "e.g. वारली चित्र"
                )
                Spacer(modifier = Modifier.height(12.dp))
                DhaagaTextField(
                    value = craftType,
                    onValueChange = { craftType = it },
                    label = "Craft Type",
                    placeholder = "e.g. Warli Art, Madhubani, Pottery",
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(12.dp))
                DhaagaTextField(
                    value = material,
                    onValueChange = { material = it },
                    label = "Material",
                    placeholder = "e.g. Natural Pigments On Handmade Paper",
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(12.dp))
                DhaagaTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = "Size",
                    placeholder = "e.g. 30x40 cm",
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    placeholder = { Text("Describe your craft, technique, and what makes it special...", color = DhaagaTextLight) },
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DhaagaPrimary,
                        focusedLabelColor = DhaagaPrimary,
                        cursorColor = DhaagaPrimary
                    )
                )
            }

            // Step 4: Pricing
            StepCard(
                step = "4",
                title = "Set Your Price",
                icon = Icons.Default.Payments,
                subtitle = "We'll compare with market prices"
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { c -> c.isDigit() } },
                        label = { Text("Price (₹)") },
                        placeholder = { Text("850", color = DhaagaTextLight) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DhaagaPrimary,
                            focusedLabelColor = DhaagaPrimary,
                            cursorColor = DhaagaPrimary
                        ),
                        prefix = { Text("₹ ") }
                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DhaagaPrimary,
                            focusedLabelColor = DhaagaPrimary,
                            cursorColor = DhaagaPrimary
                        )
                    )
                }

                // Market price comparison
                if (price.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DhaagaAccent.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = DhaagaAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Market Price Intelligence", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaAccent)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Amazon: ₹920 • Flipkart: ₹880 • GeM: ₹950", fontSize = 12.sp, color = DhaagaTextMedium)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DhaagaSuccess, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Your price of ₹$price is competitive!", fontSize = 12.sp, color = DhaagaSuccess, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Publish button
            Button(
                onClick = onPublish,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
            ) {
                Icon(Icons.Default.Publish, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publish to Dhaaga + ONDC", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StepCard(
    step: String,
    title: String,
    icon: ImageVector,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DhaagaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DhaagaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = icon, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                    Text(subtitle, fontSize = 12.sp, color = DhaagaTextMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun PhotoOptionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DhaagaCardBg)
            .border(1.dp, DhaagaDivider.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = DhaagaPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, fontSize = 13.sp, color = DhaagaTextDark, fontWeight = FontWeight.Medium)
        }
    }
}
