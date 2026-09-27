package com.dhaaga.app.ui.product

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.dhaaga.app.ui.components.CardAsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
private fun CircularActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    tint: Color = PaletteDarkGreen,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
        modifier = modifier.size(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    sharedKey: String = "product-image-$productId",
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onAddToCart: (ProductModel) -> Unit,
    onBulkEnquiry: (ProductModel) -> Unit,
    onHeritageClick: (String) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val product = products.find { it.productId == productId }
        ?: MockData.mockProducts.firstOrNull { it.productId == productId }
        ?: return

    val isSeller = user?.isSeller == true
    val isWishlisted = wishlist.contains(productId)
    val isInCart = cart.any { it.productId == productId }
    var showStory by remember { mutableStateOf(true) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var isFullscreenImageOpen by remember { mutableStateOf(false) }
    var showGiDetailsDialog by remember { mutableStateOf(false) }

    val images = remember(product) {
        if (product.imageUrls.isNotEmpty()) product.imageUrls else listOf(product.primaryImageUrl)
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = DhaagaBackground, // Crisp #FCFCFC Canvas
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DhaagaBackground)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularActionButton(
                            onClick = onBack,
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PaletteDarkGreen
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "AUTHENTIC GUILD CRAFT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteForest,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = product.titleEn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularActionButton(
                                onClick = { viewModel.toggleWishlist(productId) },
                                icon = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) Color(0xFFE53935) else PaletteDarkGreen
                            )
                            CircularActionButton(
                                onClick = {
                                    Toast.makeText(context, "Link copied to share!", Toast.LENGTH_SHORT).show()
                                },
                                icon = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = PaletteDarkGreen
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 160.dp)
            ) {
                // 1. Hero Image Card with HorizontalPager Swipe Gesture & Thumbnails
                Spacer(modifier = Modifier.height(8.dp))

                val heroBoxModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = sharedKey),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(
                                        durationMillis = 380,
                                        easing = FastOutSlowInEasing
                                    )
                                },
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(24.dp))
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(340.dp)
                            .shadow(6.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(PaletteMintCard)
                    }
                } else {
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(340.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(PaletteMintCard)
                }

                Box(modifier = heroBoxModifier) {
                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 2,
                        modifier = Modifier.fillMaxSize()
                    ) { pageIndex ->
                        CardAsyncImage(
                            model = images[pageIndex],
                            contentDescription = "${product.titleEn} - Image ${pageIndex + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { isFullscreenImageOpen = true },
                            indicatorSize = 28.dp
                        )
                    }

                    // Top Scrim Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )

                    // GI Certified Badge (Top Left - Clickable)
                    if (product.hasGITag || product.giVerified || !product.giTag.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaletteForest)
                                .clickable { showGiDetailsDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "GI Certified — ${product.giTag ?: "Heritage"}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Top Right: Photo Counter & Zoom Icon
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (images.size > 1) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${pagerState.currentPage + 1}/${images.size}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                                .clickable { isFullscreenImageOpen = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Bottom Floating Image Thumbnails Strip (Synced with pagerState)
                    if (images.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                images.forEachIndexed { idx, url ->
                                    val isSelected = idx == pagerState.currentPage
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(idx)
                                                }
                                            }
                                    ) {
                                        CardAsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize(),
                                            indicatorSize = 16.dp,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Heritage Tradition Banner (SIH 26197)
                if (product.craftHeritageId.isNotBlank()) {
                    Surface(
                        onClick = { onHeritageClick(product.craftHeritageId) },
                        color = PaletteMintCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PaletteForest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Living Tradition Behind This Creation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Text(
                                    text = "Explore Sacred Heritage Lore & Listen to Oral History →",
                                    fontSize = 11.sp,
                                    color = PaletteForest,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = PaletteForest,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 2. Product Title & Price Row
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val displayTitle = if (currentLang != "en" && product.titleHi.isNotBlank()) product.titleHi else product.titleEn
                            val subTitle = if (currentLang != "en" && product.titleHi.isNotBlank()) product.titleEn else product.titleHi
                            Text(
                                text = displayTitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen,
                                lineHeight = 26.sp
                            )
                            if (subTitle.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = subTitle,
                                    fontSize = 13.sp,
                                    color = DhaagaTextMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            if (product.hasSalePrice) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = product.priceDisplay,
                                        fontSize = 13.sp,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                        color = DhaagaTextLight
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFD32F2F)
                                    ) {
                                        Text(
                                            text = "-${product.discountPercentage}%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = product.effectivePriceDisplay,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaletteForest
                                )
                            } else {
                                Text(
                                    text = product.priceDisplay,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteForest
                                )
                            }
                            Text(
                                text = viewModel.tr("per_piece", "per piece"),
                                fontSize = 10.sp,
                                color = DhaagaTextLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rating & Authenticity Badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PaletteForest.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "%.1f".format(product.avgRating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Text(
                                    text = " (${product.reviewCount} reviews)",
                                    fontSize = 11.sp,
                                    color = DhaagaTextMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PaletteSage.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PaletteForest,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Handmade ${product.authenticityScore}%",
                                    fontSize = 11.sp,
                                    color = PaletteDarkGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // GI Certified Heritage Banner
                    if (product.hasGITag || product.giVerified || !product.giTag.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaletteTerracotta.copy(alpha = 0.08f))
                                .border(1.dp, PaletteTerracotta.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .clickable { showGiDetailsDialog = true }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = PaletteTerracotta,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "GI CERTIFIED HERITAGE" + if (!product.giTag.isNullOrBlank()) " • ${product.giTag}" else "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaletteTerracotta,
                                    letterSpacing = 0.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (product.giCraftName.isNotBlank()) product.giCraftName else "${product.craftType} (${product.region})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Details",
                                tint = PaletteTerracotta,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Available Discount Coupon Card with Copy/Apply
                    if (product.isCouponValid) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PaletteGreenTint.copy(alpha = 0.55f)),
                            border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(PaletteForest),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = product.couponCode ?: "",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = PaletteDarkGreen
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = PaletteForest
                                            ) {
                                                Text(
                                                    text = if (product.discountType == "percentage") "${product.discountValue}% OFF" else "₹${product.discountValue} FLAT OFF",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        val remainingMins = product.couponExpiryTimestamp?.let { ((it - System.currentTimeMillis()) / (60 * 1000L)).coerceAtLeast(0) }
                                        val expiryText = if (remainingMins != null) {
                                            if (remainingMins < 60) "Expires in ${remainingMins}m" else if (remainingMins < 1440) "Expires in ${remainingMins / 60}h" else "Expires in ${remainingMins / 1440}d"
                                        } else "Limited time coupon"
                                        Text(expiryText, fontSize = 10.5.sp, color = PaletteSage)
                                    }
                                }

                                Surface(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Coupon Code", product.couponCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied \"${product.couponCode}\"! Apply in shopping bag.", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy Code", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. Artisan Profile Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.dp, PaletteForest.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(PaletteForest.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.sellerName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteDarkGreen
                                    )
                                    Text(
                                        text = "${product.sellerVillage} • ${product.craftType}",
                                        fontSize = 12.sp,
                                        color = DhaagaTextMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${product.authenticityScore}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteForest
                                    )
                                    Text(
                                        text = "SHILPI SCORE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DhaagaTextLight
                                    )
                                }
                            }

                            if (product.supportBeneficiary.isNotBlank() || product.provenanceNote.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = PaletteForest.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(10.dp))

                                if (product.supportBeneficiary.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VolunteerActivism,
                                            contentDescription = null,
                                            tint = PaletteForest,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = product.supportBeneficiary,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PaletteDarkGreen
                                        )
                                    }
                                }

                                if (product.provenanceNote.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = PaletteForest,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = product.provenanceNote,
                                            fontSize = 11.sp,
                                            color = DhaagaTextMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. The Story (Kahaani) Expandable Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStory = !showStory },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = PaletteTerracotta,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = viewModel.tr("the_story", "THE STORY (KAHAANI)"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (showStory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = PaletteForest
                                )
                            }

                            AnimatedVisibility(visible = showStory) {
                                Column {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val localizedStory = if (currentLang != "en" && product.descriptionHi.isNotBlank()) {
                                        product.descriptionHi
                                    } else {
                                        product.storyEn.ifBlank { product.descriptionEn.ifBlank { "Warli painting is a 2,500-year-old ritual tribal art originating from Maharashtra. Depicting harmony between human life and nature." } }
                                    }
                                    Text(
                                        text = localizedStory,
                                        fontSize = 13.sp,
                                        color = PaletteDarkGreen,
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Artisan Oral History Audio Player Pill
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White)
                                            .clickable {
                                                isAudioPlaying = !isAudioPlaying
                                                Toast.makeText(
                                                    context,
                                                    if (isAudioPlaying) "Playing Artisan Oral History Audio..." else "Paused Audio",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(PaletteForest),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Play",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Artisan Oral History",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DhaagaTextDark,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Marathi & Hindi • 1m 15s",
                                                    fontSize = 11.sp,
                                                    color = DhaagaTextLight,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = PaletteTerracotta.copy(alpha = 0.12f),
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text(
                                                    text = if (isAudioPlaying) "Playing..." else "Listen",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PaletteTerracotta,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. Product Details Table Grid
                    Text(
                        text = "PRODUCT DETAILS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf(
                                "Craft Type" to product.craftType,
                                "Material" to product.material,
                                "Technique" to product.technique,
                                "Size" to product.sizeCm,
                                "Region" to product.region,
                                "Colors" to product.color.joinToString(", ")
                            ).forEachIndexed { index, (key, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 12.sp,
                                        color = DhaagaTextMedium,
                                        modifier = Modifier.weight(0.9f)
                                    )
                                    Text(
                                        text = value,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DhaagaTextDark,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1.1f)
                                    )
                                }
                                if (index < 5) {
                                    HorizontalDivider(color = DhaagaBackground)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6. About This Product Card
                    Text(
                        text = "ABOUT THIS PRODUCT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = product.descriptionEn,
                                fontSize = 13.sp,
                                color = PaletteDarkGreen,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Fair Trade Guarantee Strip
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PaletteTerracotta.copy(alpha = 0.08f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = PaletteTerracotta,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "₹${(product.priceListed * 0.88 / 100).toInt()} directly reaches ${product.sellerName.split(" ").firstOrNull() ?: "Artisan"}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "FAIR TRADE",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaletteTerracotta,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(84.dp))
            }
        }

        // 7. Docked Bottom Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Price Column
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "TOTAL PRICE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextLight,
                        letterSpacing = 0.8.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = product.priceDisplay,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            text = "₹${(product.priceListed * 1.4 / 100).toInt()}",
                            fontSize = 11.sp,
                            color = DhaagaTextLight,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                // Right Actions: Add to Bag + Buy Craft
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add to Bag Button
                    OutlinedButton(
                        onClick = {
                            if (isInCart) {
                                Toast.makeText(context, "Item is already in your Bag", Toast.LENGTH_SHORT).show()
                            } else {
                                onAddToCart(product)
                                Toast.makeText(context, "Added to bag", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isInCart) PaletteMintCard else Color.White,
                            contentColor = if (isInCart) PaletteTerracotta else PaletteDarkGreen
                        ),
                        border = BorderStroke(1.dp, if (isInCart) PaletteTerracotta else Color(0xFFCBD5E1)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (isInCart) Icons.Default.Check else Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = if (isInCart) PaletteTerracotta else PaletteDarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isInCart) viewModel.tr("in_stock_label", "In Bag") else viewModel.tr("add_to_bag", "Add to Bag"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInCart) PaletteTerracotta else PaletteDarkGreen
                        )
                    }

                    // Buy Craft Button (Terracotta CTA)
                    Button(
                        onClick = {
                            if (!isInCart) {
                                onAddToCart(product)
                            }
                            Toast.makeText(context, "Proceeding to checkout...", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteTerracotta),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = viewModel.tr("buy_craft", "Buy Now"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // Fullscreen Image Preview Dialog
        if (isFullscreenImageOpen) {
            Dialog(
                onDismissRequest = { isFullscreenImageOpen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                ) {
                    val fullscreenPagerState = rememberPagerState(
                        initialPage = pagerState.currentPage,
                        pageCount = { images.size }
                    )

                    HorizontalPager(
                        state = fullscreenPagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CardAsyncImage(
                                model = images[pageIndex],
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.85f),
                                indicatorSize = 32.dp
                            )
                        }
                    }

                    // Top Bar with Close button & Counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isFullscreenImageOpen = false },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "${fullscreenPagerState.currentPage + 1} / ${images.size}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showGiDetailsDialog) {
        Dialog(
            onDismissRequest = { showGiDetailsDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PaletteForest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("GI Heritage Verified", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                                Text("Govt. of India Statutory Protection", fontSize = 11.sp, color = PaletteSage)
                            }
                        }
                        IconButton(onClick = { showGiDetailsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = PaletteForest)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PaletteForest)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = product.giTag ?: "GI Verified",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "${product.authenticityScore}% Shilpi Score",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteForest
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (product.giCraftName.isNotBlank()) product.giCraftName else product.craftType,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen
                            )
                            Text(
                                text = "Cluster: ${product.region} • ${product.sellerVillage}",
                                fontSize = 12.sp,
                                color = PaletteForest,
                                fontWeight = FontWeight.Medium
                            )
                            if (product.giVerificationReason.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = product.giVerificationReason,
                                    fontSize = 12.sp,
                                    color = DhaagaTextMedium,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PaletteForest.copy(alpha = 0.06f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Geographical Indications (GI) protect traditional community handlooms and crafts under the Indian GI Act of 1999. Artisans in this verified cluster preserve generational weaving, carving, and natural dye techniques with zero middleman dilution.",
                            fontSize = 11.sp,
                            color = PaletteDarkGreen,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showGiDetailsDialog = false },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
                    ) {
                        Text("Understood", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
