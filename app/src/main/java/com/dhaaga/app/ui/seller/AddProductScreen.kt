package com.dhaaga.app.ui.seller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.ui.components.CardAsyncImage
import com.dhaaga.app.ui.components.FontAwesomeIcons
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.repository.GeminiAIService
import com.dhaaga.app.data.repository.ImageUploadRepository
import com.dhaaga.app.data.repository.PricingAnalysisResult
import com.dhaaga.app.ui.onboarding.DhaagaTextField
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddProductScreen(
    viewModel: AppViewModel? = null,
    onBack: () -> Unit,
    onPublish: () -> Unit,
    showTopBar: Boolean = true,
    bottomPadding: androidx.compose.ui.unit.Dp = 24.dp
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentLang = remember { AppLanguageManager.getCurrentLanguage(context) }

    fun tr(key: String, fallback: String): String =
        viewModel?.tr(key, fallback)?.ifBlank { AppLanguageManager.translate(key, currentLang, fallback) }
            ?: AppLanguageManager.translate(key, currentLang, fallback)

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isStudioEnhanced by remember { mutableStateOf(false) }

    // Form fields
    var title by remember { mutableStateOf("") }
    var titleHi by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var descriptionHi by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var craftType by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var technique by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var enableSalePrice by remember { mutableStateOf(false) }
    var salePrice by remember { mutableStateOf("") }
    var enableCoupon by remember { mutableStateOf(false) }
    var discountType by remember { mutableStateOf("percentage") }
    var discountValue by remember { mutableStateOf("15") }
    var couponCode by remember { mutableStateOf("") }
    var couponDurationMinutes by remember { mutableStateOf(10080L) } // 7 days default
    var couponUsageLimit by remember { mutableStateOf(0) }
    var hasGITag by remember { mutableStateOf(false) } // GI Geographical Indication Tag

    // AI & Dialog States
    var showAIStudioDialog by remember { mutableStateOf(false) }
    var showVoiceCatalogerDialog by remember { mutableStateOf(false) }
    var showAISettingsDialog by remember { mutableStateOf(false) }

    var pricingResult by remember { mutableStateOf<PricingAnalysisResult?>(null) }
    var isPricingLoading by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }

    // Camera Preview Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            // Save to temp cache to get Uri for Coil AsyncImage
            try {
                val tempFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                FileOutputStream(tempFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                }
                selectedImageUri = Uri.fromFile(tempFile)
                isStudioEnhanced = false
            } catch (e: Exception) {
                // Ignore fallback
            }
        }
    }

    // Camera Permission Launcher (Prevents SecurityException crashes)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Cannot launch camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, tr("camera_permission_required", "Camera permission is required to capture product photos."), Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            isStudioEnhanced = false
            coroutineScope.launch {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        selectedBitmap = BitmapFactory.decodeStream(stream)
                    }
                } catch (e: Exception) {
                    selectedBitmap = null
                }
            }
        }
    }

    fun rotateSelectedImage(degrees: Float) {
        val current = selectedBitmap ?: return
        val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(current, 0, 0, current.width, current.height, matrix, true)
        selectedBitmap = rotated
        try {
            val tempFile = File(context.cacheDir, "rotated_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { fos ->
                rotated.compress(Bitmap.CompressFormat.JPEG, 92, fos)
            }
            selectedImageUri = Uri.fromFile(tempFile)
        } catch (e: Exception) {
            // Fallback
        }
    }

    // Dialogs
    if (showAIStudioDialog && selectedImageUri != null) {
        AIStudioDialog(
            originalUri = selectedImageUri!!,
            originalBitmap = selectedBitmap,
            onDismiss = { showAIStudioDialog = false },
            onEnhancedImageReady = { enhancedUri, enhancedBitmap ->
                selectedImageUri = enhancedUri
                selectedBitmap = enhancedBitmap
                isStudioEnhanced = true
                showAIStudioDialog = false
                Toast.makeText(context, tr("studio_photo_applied_toast", "Studio Enhanced Photo Applied!"), Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showVoiceCatalogerDialog) {
        VoiceCatalogerDialog(
            productBitmap = selectedBitmap,
            onDismiss = { showVoiceCatalogerDialog = false },
            onCatalogGenerated = { catalog ->
                title = catalog.titleEn
                titleHi = catalog.titleHi
                description = catalog.descriptionEn
                descriptionHi = catalog.descriptionHi
                craftType = catalog.craftType
                material = catalog.material
                size = catalog.size
                technique = catalog.technique
                region = catalog.region
                if (catalog.suggestedPrice > 0) {
                    price = catalog.suggestedPrice.toString()
                }
                showVoiceCatalogerDialog = false
                Toast.makeText(context, tr("bilingual_details_applied_toast", "Bilingual Product Details Auto-Filled!"), Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAISettingsDialog) {
        AISettingsDialog(
            onDismiss = { showAISettingsDialog = false }
        )
    }

    val formContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // =========================================================================
            // 1. CRAFT PHOTOS & AI STUDIO (Minimalist Hero Dropzone & Controls)
            // =========================================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tr("craft_photos_title", "Craft Photography"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen
                            )
                            Text(
                                text = tr("craft_photos_sub", "High-clarity photos increase buyer interest"),
                                fontSize = 11.5.sp,
                                color = PaletteSage
                            )
                        }
                        if (isStudioEnhanced) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PaletteForest.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("STUDIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF4F1EA))
                        ) {
                            CardAsyncImage(
                                model = selectedImageUri,
                                contentDescription = tr("original_photo_cd", "Selected Product"),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                                indicatorSize = 28.dp,
                                shape = RoundedCornerShape(16.dp)
                            )

                            // Clean Bottom Action Bar
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        selectedImageUri = null
                                        selectedBitmap = null
                                        isStudioEnhanced = false
                                    }
                                ) {
                                    Text(tr("retake_btn", "Retake"), color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { rotateSelectedImage(-90f) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.RotateLeft, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { rotateSelectedImage(90f) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Button(
                                    onClick = { showAIStudioDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        if (isStudioEnhanced) tr("ai_studio_applied", "Studio Ready") else tr("ai_studio_enhance", "AI Studio"),
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // Clean, Aesthetic Empty Dropzone with Dual Pills
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF7FAF4))
                                .border(1.dp, PaletteSage.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(PaletteForest.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = tr("add_photo_prompt", "Add Craft Photo"),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Text(
                                    text = tr("add_photo_hint", "Use camera or select from your gallery"),
                                    fontSize = 11.5.sp,
                                    color = PaletteSage,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = {
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                                try {
                                                    cameraLauncher.launch(null)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Cannot open camera: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                        shape = RoundedCornerShape(24.dp),
                                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 9.dp)
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tr("camera_btn", "Camera"), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                imagePickerLauncher.launch("image/*")
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Cannot open gallery: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(24.dp),
                                        border = BorderStroke(1.2.dp, PaletteForest),
                                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 9.dp)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tr("gallery_btn", "Gallery"), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = PaletteForest)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. SMART VOICE AUTO-CATALOGER (Modern 1-Tap Assistant Banner)
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                PaletteForest.copy(alpha = 0.08f),
                                PaletteGreenTint.copy(alpha = 0.75f)
                            )
                        )
                    )
                    .border(1.dp, PaletteForest.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .clickable { showVoiceCatalogerDialog = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PaletteForest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tr("voice_quick_title", "Auto-Fill with Voice Note"),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(PaletteForest)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("AI", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                        Text(
                            text = tr("voice_quick_sub", "Speak in any Indian language. AI formats titles, specs & story."),
                            fontSize = 11.sp,
                            color = PaletteSage,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(PaletteForest.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(tr("speak_btn", "Speak"), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                    }
                }
            }

            // =========================================================================
            // 3. CRAFT DETAILS (Unified, Clean Form Card)
            // =========================================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = tr("step_3_title", "Craft Details"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Text(
                        text = tr("step_3_subtitle", "Product details auto-filled or edited by you"),
                        fontSize = 11.5.sp,
                        color = PaletteSage
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DhaagaTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = tr("title_en_label", "Product Title (English - SEO Optimized)"),
                        placeholder = "e.g. Handcrafted Bagru Dabu Indigo Cotton Dupatta",
                        capitalization = KeyboardCapitalization.Words
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DhaagaTextField(
                        value = titleHi,
                        onValueChange = { titleHi = it },
                        label = tr("title_hi_label", "Product Title (Hindi - हिंदी में शीर्षक)"),
                        placeholder = "e.g. हस्तनिर्मित बागरू डाबू प्राकृतिक नील कॉटन दुपट्टा"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DhaagaTextField(
                                value = craftType,
                                onValueChange = { craftType = it },
                                label = tr("craft_type_label", "Craft Type"),
                                placeholder = "e.g. Bagru Print",
                                capitalization = KeyboardCapitalization.Words
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DhaagaTextField(
                                value = material,
                                onValueChange = { material = it },
                                label = tr("material_label", "Material"),
                                placeholder = "e.g. 100% Khadi Cotton",
                                capitalization = KeyboardCapitalization.Words
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DhaagaTextField(
                                value = size,
                                onValueChange = { size = it },
                                label = tr("size_dimensions_label", "Size / Dimensions"),
                                placeholder = "e.g. 2.5m x 1m",
                                capitalization = KeyboardCapitalization.Words
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DhaagaTextField(
                                value = region,
                                onValueChange = { region = it },
                                label = tr("artisan_region_label", "Artisan Region"),
                                placeholder = "e.g. Jaipur, Rajasthan",
                                capitalization = KeyboardCapitalization.Words
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(tr("description_en_label", "SEO Product Story (English)")) },
                        placeholder = { Text("AI-generated product story...", color = DhaagaTextLight) },
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PaletteForest,
                            focusedLabelColor = PaletteForest,
                            cursorColor = PaletteForest
                        )
                    )

                    if (descriptionHi.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = descriptionHi,
                            onValueChange = { descriptionHi = it },
                            label = { Text(tr("description_hi_label", "Product Story (Hindi - विवरण)")) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaletteForest,
                                focusedLabelColor = PaletteForest,
                                cursorColor = PaletteForest
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Minimalist GI Tag Row (No heavy card nesting!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (hasGITag) PaletteForest.copy(alpha = 0.08f) else Color(0xFFF7FAF4))
                            .border(1.dp, if (hasGITag) PaletteForest.copy(alpha = 0.35f) else Color(0xFFE2EAD9), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (hasGITag) PaletteForest else PaletteSage,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    tr("gi_tag_title", "Geographical Indication (GI) Tag"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (hasGITag) PaletteForest else PaletteDarkGreen
                                )
                                if (hasGITag) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PaletteForest)
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("GI", fontSize = 8.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                tr("gi_tag_sub", "Certified authentic regional handicraft certification"),
                                fontSize = 11.sp,
                                color = PaletteSage
                            )
                        }
                        Switch(
                            checked = hasGITag,
                            onCheckedChange = { hasGITag = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PaletteForest,
                                checkedTrackColor = PaletteForest.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // =========================================================================
            // 4. PRICING & INVENTORY (Clean Card with Expandable Tools)
            // =========================================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = tr("price_inventory_title", "Price & Inventory"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Text(
                        text = tr("price_inventory_sub", "Set your price and stock availability"),
                        fontSize = 11.5.sp,
                        color = PaletteSage
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it.filter { c -> c.isDigit() } },
                            label = { Text(tr("price_label", "Price (₹)")) },
                            placeholder = { Text("850", color = DhaagaTextLight) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaletteForest,
                                focusedLabelColor = PaletteForest,
                                cursorColor = PaletteForest
                            ),
                            prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = PaletteForest) }
                        )
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                            label = { Text(tr("stock_quantity_label", "Stock Quantity")) },
                            placeholder = { Text("1", color = DhaagaTextLight) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaletteForest,
                                focusedLabelColor = PaletteForest,
                                cursorColor = PaletteForest
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Minimalist AI Dynamic Pricing Strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF7FAF4))
                            .border(1.dp, PaletteSage.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(PaletteForest.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            tint = PaletteForest,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tr("dynamic_pricing_title", "AI Price Assistant"),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = tr("dynamic_pricing_sub", "Market intelligence & cost benchmarking"),
                                            fontSize = 10.5.sp,
                                            color = PaletteSage,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                if (pricingResult == null) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isPricingLoading = true
                                                val currentPriceLong = price.toLongOrNull() ?: 0L
                                                val result = GeminiAIService.analyzeDynamicPricing(
                                                    context = context,
                                                    productImageBitmap = selectedBitmap,
                                                    title = title.ifBlank { "Handcrafted Product" },
                                                    craftType = craftType.ifBlank { "Traditional Craft" },
                                                    material = material.ifBlank { "Handmade Material" },
                                                    size = size.ifBlank { "Standard" },
                                                    enteredPrice = currentPriceLong
                                                )
                                                isPricingLoading = false
                                                result.onSuccess { res -> pricingResult = res }
                                            }
                                        },
                                        enabled = !isPricingLoading,
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(34.dp)
                                            .defaultMinSize(minWidth = 84.dp)
                                    ) {
                                        if (isPricingLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 1.8.dp)
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = tr("suggest_btn", "Suggest"),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                isPricingLoading = true
                                                val currentPriceLong = price.toLongOrNull() ?: 0L
                                                val result = GeminiAIService.analyzeDynamicPricing(
                                                    context = context,
                                                    productImageBitmap = selectedBitmap,
                                                    title = title.ifBlank { "Handcrafted Product" },
                                                    craftType = craftType.ifBlank { "Traditional Craft" },
                                                    material = material.ifBlank { "Handmade Material" },
                                                    size = size.ifBlank { "Standard" },
                                                    enteredPrice = currentPriceLong
                                                )
                                                isPricingLoading = false
                                                result.onSuccess { res -> pricingResult = res }
                                            }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PaletteForest, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            if (pricingResult != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PaletteForest.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(tr("suggested_price_tag", "AI Recommendation"), fontSize = 10.5.sp, color = PaletteSage)
                                        Text("₹${pricingResult!!.recommendedPrice}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                                    }
                                    Button(
                                        onClick = {
                                            price = pricingResult!!.recommendedPrice.toString()
                                            Toast.makeText(context, "Applied ₹${pricingResult!!.recommendedPrice}", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(tr("apply_btn", "Apply"), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Minimalist Collapsible Accordion: Discounts & Special Offers (ZERO Nested Cards)
                    var showDiscountsAccordion by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF7FAF4))
                            .border(1.dp, Color(0xFFE2EAD9), RoundedCornerShape(12.dp))
                            .clickable { showDiscountsAccordion = !showDiscountsAccordion }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Tag,
                                contentDescription = null,
                                tint = PaletteForest,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tr("discounts_coupons_title", "Discounts & Special Offers"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PaletteDarkGreen
                            )
                            if (enableSalePrice || enableCoupon) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PaletteForest)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ACTIVE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Icon(
                            imageVector = if (showDiscountsAccordion) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = PaletteSage
                        )
                    }

                    AnimatedVisibility(visible = showDiscountsAccordion) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            com.dhaaga.app.ui.components.ProductDiscountCouponCard(
                                basePriceRupees = price.toLongOrNull() ?: 0L,
                                enableSalePrice = enableSalePrice,
                                onEnableSalePriceChange = { enableSalePrice = it },
                                salePriceRupees = salePrice,
                                onSalePriceChange = { salePrice = it },
                                enableCoupon = enableCoupon,
                                onEnableCouponChange = { enableCoupon = it },
                                discountType = discountType,
                                onDiscountTypeChange = { discountType = it },
                                discountValue = discountValue,
                                onDiscountValueChange = { discountValue = it },
                                couponCode = couponCode,
                                onCouponCodeChange = { couponCode = it },
                                durationMinutes = couponDurationMinutes,
                                onDurationMinutesChange = { couponDurationMinutes = it },
                                usageLimit = couponUsageLimit,
                                onUsageLimitChange = { couponUsageLimit = it },
                                wrapInCard = false,
                                showTitle = false
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 5. PUBLISH BUTTON (Full-Width Forest Sage Button)
            // =========================================================================
            Button(
                onClick = {
                    val currentUser = viewModel?.currentUser?.value
                    val sellerId = currentUser?.uid?.ifEmpty { "seller_default" } ?: "seller_default"
                    val sellerName = currentUser?.name?.ifEmpty { "Artisan" } ?: "Artisan"
                    val sellerVillage = currentUser?.village?.ifEmpty { currentUser?.state ?: "India" } ?: "India"
                    val pricePaise = (price.toLongOrNull() ?: 0L) * 100L

                    coroutineScope.launch {
                        isPublishing = true
                        var uploadedImageUrl = ""

                        if (selectedImageUri != null) {
                            val uploadResult = ImageUploadRepository.uploadImage(context, selectedImageUri!!)
                            uploadResult.onSuccess { url ->
                                uploadedImageUrl = url
                            }
                        }

                        val productId = "prod_${System.currentTimeMillis()}"
                        val finalImageUrl = if (uploadedImageUrl.isNotEmpty()) {
                            uploadedImageUrl
                        } else {
                            selectedImageUri?.toString() ?: ""
                        }

                        val newProduct = ProductModel(
                            productId = productId,
                            sellerId = sellerId,
                            sellerName = sellerName,
                            sellerVillage = sellerVillage,
                            titleEn = title,
                            titleHi = titleHi,
                            descriptionEn = description,
                            descriptionHi = descriptionHi,
                            craftType = craftType.ifBlank { "Traditional Handcraft" },
                            material = material,
                            sizeCm = size,
                            technique = technique,
                            region = region.ifBlank { currentUser?.state ?: "India" },
                            priceListed = pricePaise,
                            stockQuantity = quantity.toIntOrNull() ?: 1,
                            imageUrls = if (finalImageUrl.isNotEmpty()) listOf(finalImageUrl) else emptyList(),
                            status = "active",
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis(),
                            salePricePaise = if (enableSalePrice && (salePrice.toLongOrNull() ?: 0L) > 0 && (salePrice.toLongOrNull() ?: 0L) * 100L < pricePaise) (salePrice.toLongOrNull()!! * 100L) else null,
                            hasDiscount = enableSalePrice || enableCoupon,
                            discountType = discountType,
                            discountValue = discountValue.toLongOrNull() ?: 0L,
                            couponCode = if (enableCoupon && couponCode.isNotBlank()) couponCode else null,
                            couponExpiryTimestamp = if (enableCoupon && couponCode.isNotBlank()) System.currentTimeMillis() + (couponDurationMinutes * 60 * 1000L) else null,
                            couponUsageLimit = if (enableCoupon) couponUsageLimit else 0,
                            couponUsageCount = 0,
                            giTag = if (hasGITag) craftType.ifBlank { "Traditional Craft" } else null,
                            giVerified = hasGITag
                        )

                        // Save directly to Firestore and local state
                        viewModel?.addUploadedProduct(newProduct)

                        isPublishing = false
                        Toast.makeText(context, tr("craft_published_toast", "Craft Published to Dhaaga!"), Toast.LENGTH_LONG).show()
                        onPublish()
                    }
                },
                enabled = !isPublishing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(tr("step_5_publishing", "Publishing Craft to Dhaaga..."), fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tr("step_5_publish", "Publish to Dhaaga + ONDC"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showTopBar) {
        Scaffold(
            containerColor = DhaagaBackground,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("back", "Back"), tint = PaletteForest)
                            }
                            Column {
                                Text(tr("list_new_craft", "List New Craft"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                                Text(tr("ai_studio_smart_cataloging", "AI studio & smart cataloging"), fontSize = 11.5.sp, color = PaletteSage)
                            }
                        }

                        IconButton(
                            onClick = { showAISettingsDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PaletteForest.copy(alpha = 0.08f))
                        ) {
                            Icon(Icons.Default.SettingsSuggest, contentDescription = tr("settings_ai_title", "AI Settings"), tint = PaletteForest, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                formContent()
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().background(DhaagaBackground)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(tr("list_new_craft", "List New Craft"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                        Text(tr("ai_studio_smart_cataloging", "AI studio & smart cataloging"), fontSize = 12.sp, color = PaletteSage)
                    }
                    IconButton(
                        onClick = { showAISettingsDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PaletteForest.copy(alpha = 0.08f))
                    ) {
                        Icon(Icons.Default.SettingsSuggest, contentDescription = tr("settings_ai_title", "AI Settings"), tint = PaletteForest, modifier = Modifier.size(20.dp))
                    }
                }
            }
            formContent()
        }
    }
}
