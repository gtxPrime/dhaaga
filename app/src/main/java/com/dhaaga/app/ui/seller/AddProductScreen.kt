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
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.repository.GeminiAIService
import com.dhaaga.app.data.repository.ImageUploadRepository
import com.dhaaga.app.data.repository.PricingAnalysisResult
import com.dhaaga.app.ui.onboarding.DhaagaTextField
import com.dhaaga.app.ui.theme.*
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
            Toast.makeText(context, "Camera permission is required to capture product photos.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "✨ Studio Enhanced Photo Applied!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "✅ Bilingual Product Details Auto-Filled!", Toast.LENGTH_SHORT).show()
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

            // ==========================================
            // Step 1: AI Image Enhancer & Studio
            // ==========================================
            StepCard(
                step = "1",
                title = "AI Image Enhancer & Studio",
                icon = Icons.Default.AutoAwesome,
                subtitle = "Camera capture & Nano Banana studio formatting"
            ) {
                if (selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF4F1EA))
                            .border(1.dp, DhaagaDivider.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Product",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top Studio Badge
                        if (isStudioEnhanced) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DhaagaPrimary.copy(alpha = 0.92f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("STUDIO ENHANCED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        // Bottom Action Controls
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    // Change Photo option
                                    selectedImageUri = null
                                    selectedBitmap = null
                                    isStudioEnhanced = false
                                }
                            ) {
                                Text("Retake", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showAIStudioDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isStudioEnhanced) "AI White Studio (Applied)" else "AI Studio Enhance",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { rotateSelectedImage(-90f) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.RotateLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rotate Left", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                        }

                        OutlinedButton(
                            onClick = { rotateSelectedImage(90f) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rotate Right", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PhotoOptionButton(
                            icon = Icons.Default.PhotoCamera,
                            label = "Built-in Camera",
                            sublabel = "Product framing guide",
                            modifier = Modifier.weight(1f),
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
                            }
                        )
                        PhotoOptionButton(
                            icon = Icons.Default.PhotoLibrary,
                            label = "Gallery",
                            sublabel = "Pick from device",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                try {
                                    imagePickerLauncher.launch("image/*")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open gallery: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }

            // ==========================================
            // Step 2: Multilingual Auto-Cataloger (Voice Note)
            // ==========================================
            StepCard(
                step = "2",
                title = "Multilingual Auto-Cataloger",
                icon = Icons.Default.Mic,
                subtitle = "Artisan voice note in regional language"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DhaagaPrimary.copy(alpha = 0.08f), DhaagaPrimaryLight.copy(alpha = 0.12f))
                            )
                        )
                        .border(1.dp, DhaagaPrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable { showVoiceCatalogerDialog = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(DhaagaPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Record Voice Note / Auto-Catalog",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DhaagaPrimary
                            )
                            Text(
                                "Speak in Hindi, Tamil, Bengali, etc. AI auto-translates & formats into SEO catalog.",
                                fontSize = 12.sp,
                                color = DhaagaTextMedium,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DhaagaPrimary)
                    }
                }
            }

            // ==========================================
            // Step 3: Product Details (Bilingual & Editable)
            // ==========================================
            StepCard(
                step = "3",
                title = "Product Details",
                icon = Icons.Default.EditNote,
                subtitle = "Auto-filled by AI. Edit as needed."
            ) {
                DhaagaTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Product Title (English - SEO Optimized)",
                    placeholder = "e.g. Handcrafted Bagru Dabu Indigo Cotton Dupatta",
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(12.dp))
                DhaagaTextField(
                    value = titleHi,
                    onValueChange = { titleHi = it },
                    label = "Product Title (Hindi - हिंदी में शीर्षक)",
                    placeholder = "e.g. हस्तनिर्मित बागरू डाबू प्राकृतिक नील कॉटन दुपट्टा"
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DhaagaTextField(
                            value = craftType,
                            onValueChange = { craftType = it },
                            label = "Craft Type",
                            placeholder = "e.g. Bagru Print, Dhokra",
                            capitalization = KeyboardCapitalization.Words
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DhaagaTextField(
                            value = material,
                            onValueChange = { material = it },
                            label = "Material",
                            placeholder = "e.g. 100% Khadi Cotton",
                            capitalization = KeyboardCapitalization.Words
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DhaagaTextField(
                            value = size,
                            onValueChange = { size = it },
                            label = "Size / Dimensions",
                            placeholder = "e.g. 2.5m x 1m",
                            capitalization = KeyboardCapitalization.Words
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DhaagaTextField(
                            value = region,
                            onValueChange = { region = it },
                            label = "Artisan Region",
                            placeholder = "e.g. Jaipur, Rajasthan",
                            capitalization = KeyboardCapitalization.Words
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("SEO Product Story (English)") },
                    placeholder = { Text("AI generated e-commerce description...", color = DhaagaTextLight) },
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

                if (descriptionHi.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = descriptionHi,
                        onValueChange = { descriptionHi = it },
                        label = { Text("Product Story (Hindi - विवरण)") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DhaagaPrimary,
                            focusedLabelColor = DhaagaPrimary,
                            cursorColor = DhaagaPrimary
                        )
                    )
                }
            }

            // ==========================================
            // Step 4: Dynamic Pricing Assistant
            // ==========================================
            StepCard(
                step = "4",
                title = "Set Your Price & Market Comparison",
                icon = Icons.Default.Payments,
                subtitle = "AI cost breakdown & competitor intelligence"
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
                        label = { Text("Stock Quantity") },
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

                Spacer(modifier = Modifier.height(14.dp))

                // Integrated Dynamic Pricing Card
                DynamicPricingCard(
                    pricingResult = pricingResult,
                    isLoading = isPricingLoading,
                    onCalculateRequested = {
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
                            result.onSuccess { res ->
                                pricingResult = res
                            }
                        }
                    },
                    onApplyPrice = { suggestedPrice ->
                        price = suggestedPrice.toString()
                        Toast.makeText(context, "Optimal Price ₹$suggestedPrice applied!", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Discounts, Coupons & Sale Pricing Component
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
                    onUsageLimitChange = { couponUsageLimit = it }
                )
            }

            // ==========================================
            // Step 5: Publish Button with Image Upload
            // ==========================================
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
                            couponUsageCount = 0
                        )

                        // Save directly to Firestore and local state
                        viewModel?.addUploadedProduct(newProduct)

                        isPublishing = false
                        Toast.makeText(context, "Craft Published to Dhaaga!", Toast.LENGTH_LONG).show()
                        onPublish()
                    }
                },
                enabled = !isPublishing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Publishing with Studio Media...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(viewModel?.tr("publish_craft", "Publish to Dhaaga + ONDC") ?: "Publish to Dhaaga + ONDC", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
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
                        .background(Brush.horizontalGradient(listOf(DhaagaPrimary, DhaagaPrimaryLight)))
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
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Column {
                                Text(viewModel?.tr("list_new_craft", "Add Product") ?: "Add Product", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("AI Studio & Smart Cataloging", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }

                        IconButton(onClick = { showAISettingsDialog = true }) {
                            Icon(Icons.Default.SettingsSuggest, contentDescription = "AI Settings", tint = Color.White)
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(viewModel?.tr("list_new_craft", "List New Craft") ?: "List New Craft", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                        Text("AI Studio & Smart Cataloging", fontSize = 12.sp, color = DhaagaTextMedium)
                    }
                    IconButton(onClick = { showAISettingsDialog = true }) {
                        Icon(Icons.Default.SettingsSuggest, contentDescription = "AI Settings", tint = DhaagaPrimary)
                    }
                }
            }
            formContent()
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
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DhaagaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = icon, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                    Text(subtitle, fontSize = 11.5.sp, color = DhaagaTextMedium, lineHeight = 15.sp)
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
    sublabel: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 108.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.35f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PaletteForest.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = PaletteForest,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.5.sp,
                color = PaletteDarkGreen,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
            if (sublabel.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sublabel,
                    fontSize = 10.sp,
                    color = PaletteSage,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
