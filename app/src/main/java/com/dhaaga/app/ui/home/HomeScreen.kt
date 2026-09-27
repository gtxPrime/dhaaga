package com.dhaaga.app.ui.home

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.ui.components.CardAsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.R
import com.dhaaga.app.data.mock.HeritageRegistry
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.components.NotionAvatar
import com.dhaaga.app.ui.components.LocationPermissionDialog
import com.dhaaga.app.utils.LocationHelper
import com.dhaaga.app.ui.theme.*
import kotlinx.coroutines.delay

data class HomeBannerItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val badge: String,
    val imageUrl: Any,
    val craftHeritageId: String = ""
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    initialTab: Int = 0,
    onProductClick: (String, String) -> Unit,
    onAddProduct: () -> Unit = {},
    onMyListings: () -> Unit = {},
    onDashboard: () -> Unit = {},
    onCart: () -> Unit = {},
    onWishlist: () -> Unit = {},
    onOrders: () -> Unit = {},
    onProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onChatList: () -> Unit = {},
    onCraftClick: (String) -> Unit = {},
    onHeritageAtlas: () -> Unit = {},
    onLearnPractice: (String) -> Unit = {},
    onHeritageStudio: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val context = LocalContext.current
    var showAISettingsDialogWithKeys by remember { mutableStateOf(false) }
    val user by viewModel.currentUser.collectAsState()
    val products by viewModel.products.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartAnimationEvent by viewModel.cartAnimationEvent.collectAsState()
    val lastAddedProduct by viewModel.lastAddedProduct.collectAsState()
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = initialTab.coerceIn(0, 4), pageCount = { 5 })

    val isSeller = user?.isSeller == true

    // Search & Filter State
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Floating Added-to-Cart Capsule Notification state
    var showAddedBanner by remember { mutableStateOf(false) }
    var bannerProduct by remember { mutableStateOf<ProductModel?>(null) }
    var lastHandledBannerEvent by remember { mutableStateOf(cartAnimationEvent) }

    var showLocationPermissionDialog by remember { mutableStateOf(false) }

    // Automatic location detection on launch if permission already granted, or prompt if first launch
    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            viewModel.detectLocation(context)
        } else {
            delay(1500)
            showLocationPermissionDialog = true
        }
    }

    LaunchedEffect(cartAnimationEvent) {
        if (cartAnimationEvent > 0L && cartAnimationEvent != lastHandledBannerEvent && lastAddedProduct != null) {
            lastHandledBannerEvent = cartAnimationEvent
            bannerProduct = lastAddedProduct
            showAddedBanner = true
            delay(2600)
            showAddedBanner = false
            viewModel.consumeCartAnimationEvent()
        }
    }

    // Handle Android back button: navigate to Home Tab first
    BackHandler(enabled = pagerState.currentPage != 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(0, animationSpec = tween(350, easing = FastOutSlowInEasing))
        }
    }

    // Curated Living Heritage Exhibitions of Bharat (SIH 26197)
    val bannerItems = remember(currentLang) {
        listOf(
            HomeBannerItem(
                id = 1,
                title = com.dhaaga.app.utils.AppLanguageManager.translate("banner_warli_title", currentLang, "Warli Ritual Cosmogony"),
                subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("banner_warli_sub", currentLang, "2,500-Year Sacred Geometric Lineage • Palghar, Maharashtra"),
                badge = com.dhaaga.app.utils.AppLanguageManager.translate("banner_warli_badge", currentLang, "LIVING HERITAGE EXHIBITION"),
                imageUrl = R.drawable.banner_warli_art,
                craftHeritageId = "heritage_warli_01"
            ),
            HomeBannerItem(
                id = 2,
                title = com.dhaaga.app.utils.AppLanguageManager.translate("banner_madhubani_title", currentLang, "Mithila Kohbar & Folk Epics"),
                subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("banner_madhubani_sub", currentLang, "Natural Pigments & Double-Line Rekha • Madhubani, Bihar"),
                badge = com.dhaaga.app.utils.AppLanguageManager.translate("banner_madhubani_badge", currentLang, "GI REGISTERED TRADITION"),
                imageUrl = R.drawable.banner_madhubani_art,
                craftHeritageId = "heritage_madhubani_02"
            ),
            HomeBannerItem(
                id = 3,
                title = com.dhaaga.app.utils.AppLanguageManager.translate("banner_pashmina_title", currentLang, "Kashmir Pashmina Handloom"),
                subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("banner_pashmina_sub", currentLang, "12-Micron Changthangi Himalayan Fleece • Srinagar, Kashmir"),
                badge = com.dhaaga.app.utils.AppLanguageManager.translate("banner_pashmina_badge", currentLang, "CENTURY-OLD CRAFT GUILD"),
                imageUrl = R.drawable.banner_pashmina_loom,
                craftHeritageId = "heritage_pashmina_04"
            ),
            HomeBannerItem(
                id = 4,
                title = com.dhaaga.app.utils.AppLanguageManager.translate("banner_dhokra_title", currentLang, "Bastar Lost-Wax Bell Metal"),
                subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("banner_dhokra_sub", currentLang, "4,000-Year Ancient Mohenjo-Daro Lineage • Bastar, Chhattisgarh"),
                badge = com.dhaaga.app.utils.AppLanguageManager.translate("banner_dhokra_badge", currentLang, "ANCIENT TRIBAL BRONZE"),
                imageUrl = R.drawable.banner_dhokra_metal,
                craftHeritageId = "heritage_dhokra_05"
            ),
            HomeBannerItem(
                id = 5,
                title = com.dhaaga.app.utils.AppLanguageManager.translate("banner_bluepottery_title", currentLang, "Jaipur Turquoise Blue Pottery"),
                subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("banner_bluepottery_sub", currentLang, "Clayless Quartz & Cobalt Glaze Firing • Kot Jewar, Rajasthan"),
                badge = com.dhaaga.app.utils.AppLanguageManager.translate("banner_bluepottery_badge", currentLang, "ROYAL RAJPUT GUILD"),
                imageUrl = R.drawable.banner_blue_pottery,
                craftHeritageId = "heritage_blue_pottery_03"
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Horizontal Swipeable Pager for all 5 Tabs
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HomeFeedTab(
                    viewModel = viewModel,
                    user = user,
                    isSeller = isSeller,
                    products = products,
                    wishlist = wishlist,
                    cart = cart,
                    bannerItems = bannerItems,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onProductClick = onProductClick,
                    onChatList = onChatList,
                    onNavigateToCart = {
                        if (isSeller) {
                            onCart()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    currentLang = currentLang,
                    onAvatarClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(4, animationSpec = tween(350, easing = FastOutSlowInEasing))
                        }
                    },
                    onAvatarLongClick = {
                        showAISettingsDialogWithKeys = true
                        Toast.makeText(context, "Developer Mode: API Keys Unlocked", Toast.LENGTH_SHORT).show()
                    },
                    onLocationClick = {
                        if (LocationHelper.hasLocationPermission(context)) {
                            viewModel.detectLocation(context)
                        } else {
                            showLocationPermissionDialog = true
                        }
                    },
                    onCraftClick = onCraftClick,
                    onHeritageAtlas = onHeritageAtlas,
                    onLearnPractice = onLearnPractice,
                    onHeritageStudio = onHeritageStudio
                )
                1 -> if (isSeller) {
                    MyListingsTabContent(
                        viewModel = viewModel,
                        onProductClick = { productId -> onProductClick(productId, "product-image-grid-$productId") },
                        onAddProduct = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    )
                } else {
                    WishlistTabContent(
                        viewModel = viewModel,
                        onProductClick = { productId -> onProductClick(productId, "product-image-grid-$productId") },
                        onExploreProducts = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    )
                }
                2 -> if (isSeller) {
                    AddProductTabContent(
                        viewModel = viewModel,
                        onProductAdded = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    )
                } else {
                    CartTabContent(
                        viewModel = viewModel,
                        onExplore = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        },
                        onCheckout = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(3, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    )
                }
                3 -> if (isSeller) {
                    SellerDashboardTabContent(
                        viewModel = viewModel,
                        onViewOrders = { onOrders() }
                    )
                } else {
                    MyOrdersTabContent(
                        viewModel = viewModel,
                        onExplore = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    )
                }
                4 -> ProfileTabContent(
                    viewModel = viewModel,
                    onMyListings = { onMyListings() },
                    onMyOrders = { onOrders() },
                    onLogout = onLogout
                )
            }
        }

        // Spotify-Style Bottom Fading Edge Overlay (#FCFCFC Canvas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(130.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DhaagaBackground.copy(alpha = 0.65f),
                            DhaagaBackground.copy(alpha = 0.95f),
                            DhaagaBackground
                        )
                    )
                )
        )

        // Floating Added-to-Bag Toast Capsule
        AnimatedVisibility(
            visible = showAddedBanner && bannerProduct != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)
            ) + fadeIn(tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(200)
            ) + fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 102.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = PaletteDarkGreen,
                shadowElevation = 12.dp,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PaletteForest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = com.dhaaga.app.utils.AppLanguageManager.translate("added_to_bag", currentLang, "Added to Bag"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        val bannerTitle = if (currentLang != "en" && !bannerProduct?.titleHi.isNullOrBlank()) bannerProduct?.titleHi else bannerProduct?.titleEn
                        Text(
                            text = bannerTitle ?: "",
                            fontSize = 11.sp,
                            color = PaletteGreenTint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = {
                            showAddedBanner = false
                            if (isSeller) {
                                onCart()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(2, animationSpec = tween(350, easing = FastOutSlowInEasing))
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = com.dhaaga.app.utils.AppLanguageManager.translate("view_bag", currentLang, "View Bag"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Lore Floating Navigation Bar (Shared across all swipeable tabs with Cart Animation!)
        LoreExactFloatingBottomNav(
            selectedTab = pagerState.currentPage,
            isSeller = isSeller,
            cartCount = cart.size,
            wishlistCount = wishlist.size,
            cartAnimationTrigger = cartAnimationEvent,
            currentLang = currentLang,
            onTabSelected = { targetTab ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        targetTab,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (showAISettingsDialogWithKeys) {
            com.dhaaga.app.ui.seller.AISettingsDialog(
                initialShowDeveloperKeys = true,
                onDismiss = { showAISettingsDialogWithKeys = false }
            )
        }

        if (showLocationPermissionDialog) {
            LocationPermissionDialog(
                onDismiss = { showLocationPermissionDialog = false },
                onPermissionGranted = {
                    viewModel.detectLocation(context)
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HomeFeedTab(
    viewModel: AppViewModel,
    user: UserModel?,
    isSeller: Boolean,
    products: List<ProductModel>,
    wishlist: Set<String>,
    cart: List<com.dhaaga.app.data.model.CartItemModel>,
    bannerItems: List<HomeBannerItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onProductClick: (String, String) -> Unit,
    onChatList: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    currentLang: String = "en",
    onAvatarClick: () -> Unit = {},
    onAvatarLongClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onCraftClick: (String) -> Unit = {},
    onHeritageAtlas: () -> Unit = {},
    onLearnPractice: (String) -> Unit = {},
    onHeritageStudio: () -> Unit = {}
) {
    val isSearching = searchQuery.isNotBlank() || selectedCategory != "All"
    val userLocation by viewModel.userLocation.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                product.titleEn.lowercase().contains(q) ||
                product.titleHi.lowercase().contains(q) ||
                product.craftType.lowercase().contains(q) ||
                product.material.lowercase().contains(q) ||
                product.technique.lowercase().contains(q) ||
                product.region.lowercase().contains(q) ||
                product.sellerName.lowercase().contains(q) ||
                product.sellerVillage.lowercase().contains(q) ||
                product.descriptionEn.lowercase().contains(q) ||
                (q == "gi" && product.hasGITag) ||
                (q.contains("gi") && product.hasGITag)
            }

            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "GI Certified" -> product.hasGITag
                else -> product.craftType.contains(selectedCategory, ignoreCase = true) ||
                        product.material.contains(selectedCategory, ignoreCase = true) ||
                        product.titleEn.contains(selectedCategory, ignoreCase = true) ||
                        product.region.contains(selectedCategory, ignoreCase = true)
            }

            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        containerColor = DhaagaBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Vibrant Forest Sage Header with Interactive Search & Filter Chips
            item {
                HeaderBlock(
                    user = user,
                    isSeller = isSeller,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    onChatClick = onChatList,
                    onNotificationClick = {},
                    cartCount = cart.size,
                    onCartClick = onNavigateToCart,
                    currentLang = currentLang,
                    onAvatarClick = onAvatarClick,
                    onAvatarLongClick = onAvatarLongClick,
                    userLocation = userLocation,
                    isLocating = isLocating,
                    onLocationClick = onLocationClick
                )
            }

            if (isSearching) {
                // Search Results Header
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val resultsPrefix = com.dhaaga.app.utils.AppLanguageManager.translate("results_for", currentLang, "RESULTS FOR")
                            val catLabel = com.dhaaga.app.utils.AppLanguageManager.translate("category_label", currentLang, "CATEGORY")
                            Text(
                                text = if (searchQuery.isNotBlank()) "$resultsPrefix \"$searchQuery\"" else "$catLabel: ${selectedCategory.uppercase()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen,
                                letterSpacing = 0.5.sp
                            )
                            val itemsSuffix = com.dhaaga.app.utils.AppLanguageManager.translate("items_suffix", currentLang, "crafts found")
                            Text(
                                text = "${filteredProducts.size} $itemsSuffix",
                                fontSize = 11.5.sp,
                                color = DhaagaTextLight
                            )
                        }
                        Text(
                            text = com.dhaaga.app.utils.AppLanguageManager.translate("clear_filter", currentLang, "Clear Filter"),
                            fontSize = 12.sp,
                            color = PaletteForest,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onSearchQueryChange("")
                                onCategorySelected("All")
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (filteredProducts.isEmpty()) {
                    // Empty Search Results State
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(PaletteGreenTint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No crafts found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Try searching for \"Madhubani\", \"Warli\", \"Handloom\", or \"Terracotta\"",
                                    fontSize = 12.5.sp,
                                    color = DhaagaTextMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = {
                                        onSearchQueryChange("")
                                        onCategorySelected("All")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Explore All Crafts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    // 2-Column Filtered Product Grid
                    items(filteredProducts.chunked(2)) { rowProducts ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowProducts.forEach { product ->
                                val inCart = cart.any { it.productId == product.productId }
                                val searchKey = "product-image-search-${product.productId}"
                                ProductCardCreative(
                                    product = product,
                                    sharedKey = searchKey,
                                    isWishlisted = wishlist.contains(product.productId),
                                    isInCart = inCart,
                                    onWishlist = { viewModel.toggleWishlist(product.productId) },
                                    onAddToCart = { viewModel.addToCart(product) },
                                    onNavigateToCart = onNavigateToCart,
                                    onClick = { onProductClick(product.productId, searchKey) },
                                    modifier = Modifier.weight(1f),
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    currentLang = currentLang
                                )
                            }
                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // ── SIH 26197: Living Traditions & Oral Histories Story Reel ─────────
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    LivingTraditionsStoryReel(
                        currentLang = currentLang,
                        onCraftClick = onCraftClick
                    )
                }

                // ── SIH 26197: Bharat Heritage Exploration Portals ───────────────────
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    HeritagePortalsRow(
                        onHeritageAtlas = onHeritageAtlas,
                        onLearnPractice = { onLearnPractice("") },
                        onHeritageStudio = onHeritageStudio,
                        isSeller = isSeller
                    )
                }

                // Interactive Auto-Loop Image Banner Slider with deep link to living heritage
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HeroBannerSlider(
                        banners = bannerItems,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onBannerClick = { banner ->
                            if (banner.craftHeritageId.isNotBlank()) {
                                onCraftClick(banner.craftHeritageId)
                            }
                        }
                    )
                }

                // ── SIH 26197: Living Craft Traditions of Bharat Carousel ─────────────
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    LivingTraditionsCarouselRow(
                        currentLang = currentLang,
                        onCraftClick = onCraftClick
                    )
                }

                // ── SIH 26197: Interactive Sacred Motifs & Iconography Explorer ────────
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SacredMotifsExplorerRow(
                        currentLang = currentLang,
                        onLearnPractice = onLearnPractice
                    )
                }

                // ── SIH 26197: Featured Living Tradition Spotlight ───────────────────
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    FeaturedLivingTraditionSpotlight(
                        currentLang = currentLang,
                        onExplore = { onCraftClick("heritage_warli_01") }
                    )
                }

                // ── Section 1: Direct Artisan Workshops & Master Lineages ─────────────
                item {
                    Spacer(modifier = Modifier.height(22.dp))
                    SectionHeaderRow(
                        title = com.dhaaga.app.utils.AppLanguageManager.translate("artisan_workshops_heading", currentLang, "DIRECT ARTISAN WORKSHOPS (शिल्पकार घराने)"),
                        subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("artisan_workshops_sub", currentLang, "Direct patronage supporting generational master artisan families"),
                        icon = Icons.Default.WorkspacePremium,
                        iconColor = PaletteForest,
                        onSeeAll = { onCategorySelected("All") }
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(products.take(4), key = { it.productId }) { product ->
                            val inCart = cart.any { it.productId == product.productId }
                            val trendingKey = "product-image-trending-${product.productId}"
                            ProductCardCreative(
                                product = product,
                                sharedKey = trendingKey,
                                isWishlisted = wishlist.contains(product.productId),
                                isInCart = inCart,
                                onWishlist = { viewModel.toggleWishlist(product.productId) },
                                onAddToCart = { viewModel.addToCart(product) },
                                onNavigateToCart = onNavigateToCart,
                                onClick = { onProductClick(product.productId, trendingKey) },
                                modifier = Modifier.width(180.dp),
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                currentLang = currentLang
                            )
                        }
                    }
                }

                // ── Section 2: Sustainable Patronage Creations ───────────────────────
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeaderRow(
                        title = com.dhaaga.app.utils.AppLanguageManager.translate("patronage_creations_heading", currentLang, "SUSTAINABLE PATRONAGE CREATIONS (धरोहर संरक्षण कृतियाँ)"),
                        subtitle = com.dhaaga.app.utils.AppLanguageManager.translate("patronage_creations_sub", currentLang, "100% direct artisan proceeds • Verified GI provenance • Zero middleman cut"),
                        icon = Icons.Default.VolunteerActivism,
                        iconColor = PaletteForest,
                        onSeeAll = {}
                    )
                }

                // 2-Column Product Grid
                items(products.chunked(2)) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowProducts.forEach { product ->
                            val inCart = cart.any { it.productId == product.productId }
                            val gridKey = "product-image-grid-${product.productId}"
                            ProductCardCreative(
                                product = product,
                                sharedKey = gridKey,
                                isWishlisted = wishlist.contains(product.productId),
                                isInCart = inCart,
                                onWishlist = { viewModel.toggleWishlist(product.productId) },
                                onAddToCart = { viewModel.addToCart(product) },
                                onNavigateToCart = onNavigateToCart,
                                onClick = { onProductClick(product.productId, gridKey) },
                                modifier = Modifier.weight(1f),
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                currentLang = currentLang
                            )
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top Header going seamlessly under the status bar (#60734E -> #738861 Forest Sage)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeaderBlock(
    user: UserModel?,
    isSeller: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onChatClick: () -> Unit,
    onNotificationClick: () -> Unit,
    cartCount: Int = 0,
    onCartClick: () -> Unit = {},
    currentLang: String = "en",
    onAvatarClick: () -> Unit = {},
    onAvatarLongClick: () -> Unit = {},
    userLocation: com.dhaaga.app.utils.UserLocationInfo? = null,
    isLocating: Boolean = false,
    onLocationClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Android Voice Search Recognition Launcher
    val voiceSearchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                onSearchQueryChange(spokenText)
                Toast.makeText(context, "Voice Search: \"$spokenText\"", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp)
        ) {
            // Row 1: Compact Transparent Dhaaga Text Logo + Location + Actions + User Notion Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.dhaaga_logo),
                        contentDescription = "Dhaaga Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(48.dp)
                            .wrapContentWidth(Alignment.Start)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onLocationClick() }
                            .padding(vertical = 2.dp)
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(10.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.95f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(3.dp))
                        val userLabel = if (isSeller) com.dhaaga.app.utils.AppLanguageManager.translate("artisan_label", currentLang, "Artisan") else com.dhaaga.app.utils.AppLanguageManager.translate("craft_lover", currentLang, "Craft Lover")
                        val placeText = when {
                            isLocating -> "Detecting GPS..."
                            userLocation != null -> userLocation.displayLocation
                            isSeller && !user?.village.isNullOrBlank() -> "${user.village}, ${user.state.ifBlank { "India" }}"
                            user != null && user.state.isNotBlank() -> user.state
                            else -> "Tap to enable location"
                        }
                        Text(
                            text = "$userLabel • $placeText",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Chat button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onChatClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Chat,
                        contentDescription = "Chat",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Shopping Bag button (Unclipped badge container so numbers render perfectly without truncation)
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onCartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.BagShopping,
                            contentDescription = "Shopping Bag",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-3).dp)
                                .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                                .border(1.2.dp, Color.White, CircleShape)
                                .padding(horizontal = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cartCount > 99) "99+" else "$cartCount",
                                fontSize = 8.5.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Notification button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // User Notion Avatar
                if (user != null) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .combinedClickable(
                                onClick = onAvatarClick,
                                onLongClick = onAvatarLongClick
                            )
                    ) {
                        NotionAvatar(
                            name = user.name,
                            size = 34.dp,
                            borderWidth = 1.5.dp,
                            imageUrl = user.profilePhotoUrl
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Active Search Bar (#FCFCFC Pure White Pill)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.MagnifyingGlass,
                    contentDescription = "Search",
                    tint = if (searchQuery.isNotEmpty()) PaletteForest else Color(0xFF888888),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = com.dhaaga.app.utils.AppLanguageManager.translate("search_placeholder_heritage", currentLang, "Search living traditions, master artisans, motifs..."),
                            color = Color(0xFF888888),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = PaletteDarkGreen
                        ),
                        cursorBrush = SolidColor(PaletteForest),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF777777),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(
                                    RecognizerIntent.EXTRA_PROMPT,
                                    if (currentLang != "en") "शिल्प या परम्परा का नाम बोलें..." else "Speak craft or tradition name (e.g. Warli, Madhubani, Pashmina)..."
                                )
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE,
                                    if (currentLang != "en") "hi-IN" else java.util.Locale.getDefault().toLanguageTag()
                                )
                            }
                            try {
                                voiceSearchLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Voice search not available on this device", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Microphone,
                            contentDescription = "Voice Search",
                            tint = PaletteForest,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Living Traditions & Craft Filter Chips
            val categories = listOf("All", "Warli Art", "Madhubani", "Pashmina", "Dhokra", "Blue Pottery", "GI Certified")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    val localizedLabel = when (cat) {
                        "All" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_all", currentLang, "All Traditions")
                        "Warli Art" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_warli", currentLang, "Warli Art")
                        "Madhubani" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_madhubani", currentLang, "Madhubani")
                        "Pashmina" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_pashmina", currentLang, "Pashmina")
                        "Dhokra" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_dhokra", currentLang, "Bastar Dhokra")
                        "Blue Pottery" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_blue_pottery", currentLang, "Blue Pottery")
                        "GI Certified" -> com.dhaaga.app.utils.AppLanguageManager.translate("cat_gi", currentLang, "GI Certified")
                        else -> cat
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PaletteTerracotta else Color.White.copy(alpha = 0.15f)
                            )
                            .clickable {
                                onCategorySelected(cat)
                            }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = localizedLabel,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Auto-Loop Image Banner Slider replacing the static Heritage Banner
 */
@Composable
private fun HeroBannerSlider(
    banners: List<HomeBannerItem>,
    modifier: Modifier = Modifier,
    onBannerClick: (HomeBannerItem) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Auto-loop / Auto-slide effect every 3.5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)
            if (banners.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp) // Increased slider height to 230.dp
            .clip(RoundedCornerShape(22.dp))
            .shadow(6.dp, RoundedCornerShape(22.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val banner = banners[pageIndex]

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onBannerClick(banner)
                    }
            ) {
                // Background Image with themed loader
                CardAsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    indicatorSize = 28.dp
                )

                // Dark Translucent Gradient Scrim for Contrast & Legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Banner Details Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PaletteForest)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = banner.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = banner.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = banner.subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Explore Exhibition & Oral Archive",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }

        // Animated Dot Page Indicator (Bottom Right)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.indices.forEach { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 18.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) PaletteForest else Color.White.copy(alpha = 0.6f))
                )
            }
        }
    }
}

// ── SIH 26197: Heritage Showcase Components ─────────────────────────────────

@Composable
private fun LivingTraditionsStoryReel(
    currentLang: String,
    onCraftClick: (String) -> Unit
) {
    val traditions = HeritageRegistry.livingTraditions

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(PaletteTerracotta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = com.dhaaga.app.utils.AppLanguageManager.translate("oral_histories_heading", currentLang, "ORAL HISTORIES & LIVING MASTERS"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = PaletteDarkGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Listen",
                fontSize = 11.5.sp,
                color = PaletteTerracotta,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(traditions, key = { it.craftId }) { craft ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(76.dp)
                        .clickable { onCraftClick(craft.craftId) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Outer decorative ring with warm terracotta accent
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, PaletteTerracotta, CircleShape)
                                .padding(3.dp)
                        ) {
                            Image(
                                painter = painterResource(id = com.dhaaga.app.ui.components.getCraftFallbackDrawable(craft.craftId)),
                                contentDescription = craft.masterArtisanName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        // Audio mic badge indicator
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .clip(CircleShape)
                                .background(PaletteTerracotta)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Audio story",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = craft.masterArtisanName.take(11),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (currentLang == "hi") craft.craftNameHi.take(9) else craft.craftNameEn.take(10),
                        fontSize = 10.sp,
                        color = DhaagaTextMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun HeritagePortalsRow(
    onHeritageAtlas: () -> Unit,
    onLearnPractice: () -> Unit,
    onHeritageStudio: () -> Unit,
    isSeller: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Portal 1: Heritage Atlas
        Surface(
            onClick = onHeritageAtlas,
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PaletteTerracotta.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = PaletteTerracotta,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Regional Atlas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen
                )
                Text(
                    text = "GI Clusters",
                    fontSize = 10.sp,
                    color = DhaagaTextMedium
                )
            }
        }

        // Portal 2: Learn & Practice Motifs
        Surface(
            onClick = onLearnPractice,
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PaletteTerracotta.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = PaletteTerracotta,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Learn Motifs",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen
                )
                Text(
                    text = "Practice Hub",
                    fontSize = 10.sp,
                    color = DhaagaTextMedium
                )
            }
        }

        // Portal 3: AI Documentation Studio (or Artisan Studio)
        Surface(
            onClick = onHeritageStudio,
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PaletteTerracotta.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PaletteTerracotta,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isSeller) "Document" else "Artisan AI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen
                )
                Text(
                    text = "Heritage Studio",
                    fontSize = 10.sp,
                    color = DhaagaTextMedium
                )
            }
        }
    }
}

@Composable
private fun FeaturedLivingTraditionSpotlight(
    currentLang: String,
    onExplore: () -> Unit
) {
    Surface(
        onClick = onExplore,
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_warli_art),
                    contentDescription = "Warli Tribal Tradition",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = PaletteTerracotta,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LIVING TRADITION SPOTLIGHT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Warli Tribal Folk Painting",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen
                )
                Text(
                    text = "2,500-Year Sacred Geometric Tradition • Palghar, Maharashtra",
                    fontSize = 11.sp,
                    color = DhaagaTextMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Listen to Oral History & Explore",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteTerracotta
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PaletteTerracotta,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

// ── SIH 26197: Living Craft Traditions of Bharat Carousel ───────────────────
@Composable
private fun LivingTraditionsCarouselRow(
    currentLang: String,
    onCraftClick: (String) -> Unit
) {
    val traditions = HeritageRegistry.livingTraditions

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(PaletteTerracotta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = com.dhaaga.app.utils.AppLanguageManager.translate("living_traditions_heading", currentLang, "LIVING TRADITIONS OF BHARAT"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = PaletteDarkGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "5 Hubs",
                fontSize = 11.5.sp,
                color = PaletteTerracotta,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(traditions, key = { it.craftId }) { craft ->
                Surface(
                    onClick = { onCraftClick(craft.craftId) },
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 3.dp,
                    modifier = Modifier.width(260.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            Image(
                                painter = painterResource(id = com.dhaaga.app.ui.components.getCraftFallbackDrawable(craft.craftId)),
                                contentDescription = craft.craftNameEn,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                        )
                                    )
                            )
                            // Top Badges
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = PaletteTerracotta,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${craft.traditionAgeYears} Yrs Old",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.95f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "GI Registered",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteDarkGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            // Bottom Title on Image
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (currentLang == "hi") craft.craftNameHi else craft.craftNameEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${craft.district}, ${craft.state}",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (currentLang == "hi") craft.summaryHi else craft.summaryEn,
                                fontSize = 11.sp,
                                color = DhaagaTextMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = PaletteTerracotta,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = craft.masterArtisanName,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PaletteDarkGreen
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "Oral Archive",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteTerracotta
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = PaletteTerracotta,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SIH 26197: Interactive Sacred Motifs & Iconography Explorer ──────────────
@Composable
private fun SacredMotifsExplorerRow(
    currentLang: String,
    onLearnPractice: (String) -> Unit
) {
    val motifs = listOf(
        Triple(
            "Tarpa Spiral Dance",
            "तारपा नृत्य चक्र • Warli",
            "Unbroken circle of life, cosmic harmony, and community solidarity without hierarchy."
        ),
        Triple(
            "Kohbar Lotus & Bamboo",
            "कोहबर कमल व बांस • Mithila",
            "Sacred fertility and longevity; lotus symbolizes female purity, bamboo symbolizes male lineage."
        ),
        Triple(
            "Kalka Paisley (Boteh)",
            "काल्का बादाम बूटा • Kashmir",
            "Ancient cypress tree and young shoot motif representing immortality, vitality, and renewal."
        ),
        Triple(
            "Dancing Deer Totem",
            "वन्य हिरण प्रतीक • Dhokra",
            "4,000-year tribal animism symbol representing communion with forest spirits."
        ),
        Triple(
            "Arabesque Foliage (Bel-Boote)",
            "बेल-बूटे लताएं • Jaipur",
            "Intertwined natural vine florals symbolizing boundless prosperity and grace."
        )
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(PaletteTerracotta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = com.dhaaga.app.utils.AppLanguageManager.translate("sacred_motifs_heading", currentLang, "SACRED FOLK MOTIFS & ICONOGRAPHY"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = PaletteDarkGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Studio >",
                fontSize = 11.5.sp,
                color = PaletteTerracotta,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.clickable { onLearnPractice("") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(motifs) { (titleEn, titleHi, meaning) ->
                Surface(
                    onClick = { onLearnPractice(titleEn) },
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.35f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.width(220.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = PaletteMintCard,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Brush,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Digital Canvas",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteForest
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (currentLang == "hi") titleHi else titleEn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = meaning,
                            fontSize = 11.sp,
                            color = DhaagaTextMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Practice Drawing",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteForest
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = PaletteForest,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Creative Product Card
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ProductCardCreative(
    product: ProductModel,
    isWishlisted: Boolean,
    isInCart: Boolean,
    onWishlist: () -> Unit,
    onAddToCart: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedKey: String = "product-image-${product.productId}",
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    currentLang: String = "en"
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image with Overlays (Clickable to morph into details)
            val imageBoxModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
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
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        )
                        .fillMaxWidth()
                        .height(155.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .clickable { onClick() }
                }
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .clickable { onClick() }
            }

            Box(modifier = imageBoxModifier) {
                CardAsyncImage(
                    model = product.primaryImageUrl,
                    contentDescription = product.titleEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    indicatorSize = 24.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )

                // Badges Row (Top Left): GI Certification Tag
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.hasGITag) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PaletteForest)
                                .padding(horizontal = 6.dp, vertical = 2.5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.ShieldCheck,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "GI",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Wishlist Heart Button (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable { onWishlist() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWishlisted) com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Heart else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color.Red else Color(0xFF555555),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Craft Tag (Bottom Left Overlay)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = product.craftType,
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }

                // Rating Badge (Bottom Right Overlay)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PaletteForest)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "%.1f".format(product.avgRating),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Product Details
            Column(modifier = Modifier.padding(12.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick() }
                ) {
                    val displayTitle = if (currentLang != "en" && product.titleHi.isNotBlank()) product.titleHi else product.titleEn
                    Text(
                        text = displayTitle,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = product.sellerVillage,
                        fontSize = 10.5.sp,
                        color = DhaagaTextLight,
                        minLines = 1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Fair Patronage Value Row (SpaceBetween ensures price & badge never collide or wrap)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = product.priceDisplay,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaletteDarkGreen,
                            maxLines = 1
                        )
                        Surface(
                            color = PaletteTerracotta.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Fair Value",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteTerracotta,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = PaletteTerracotta,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "100% Direct to Artisan",
                            fontSize = 9.sp,
                            color = DhaagaTextMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                var isButtonBouncing by remember { mutableStateOf(false) }
                val buttonScale by animateFloatAsState(
                    targetValue = if (isButtonBouncing) 0.90f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
                    finishedListener = { isButtonBouncing = false },
                    label = "btnScale"
                )

                // Sustainable Patronage Action Button
                Button(
                    onClick = {
                        if (isInCart) {
                            onNavigateToCart()
                        } else {
                            isButtonBouncing = true
                            onAddToCart()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isInCart) PaletteTerracotta else Color(0xFFF1F5F9)
                    ),
                    border = if (isInCart) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isInCart) com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Check else Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = if (isInCart) Color.White else PaletteTerracotta,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isInCart) "In Bag" else "Patronize",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInCart) Color.White else PaletteDarkGreen,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section Header Row with stacked title and subtitle
 */
@Composable
private fun SectionHeaderRow(
    title: String,
    subtitle: String = "",
    icon: ImageVector,
    iconColor: Color,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(15.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen,
                    letterSpacing = 0.4.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        color = DhaagaTextMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Explore >",
            fontSize = 11.5.sp,
            color = PaletteTerracotta,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

/**
 * Lore-App Floating Navigation Bar in Richer Soft Green Tint (#E2EAD9) - ZERO BROWN
 * Exact Compact Dimensions: 56.dp height, 28.dp corner radius, 24.dp icons, 56.dp FAB
 */
/**
 * Lore-App Floating Navigation Bar in Richer Soft Green Tint (#E2EAD9) - ZERO BROWN
 * Exact Compact Dimensions: 56.dp height, 28.dp corner radius, 24.dp icons, 56.dp FAB
 * Shared across all swipeable tabs with fluid active highlight state & rich Add-to-Cart micro-animations
 */
@Composable
fun LoreExactFloatingBottomNav(
    selectedTab: Int,
    isSeller: Boolean,
    cartCount: Int,
    wishlistCount: Int,
    cartAnimationTrigger: Long = 0L,
    currentLang: String = "en",
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Micro-animation states for Cart FAB
    val fabAnimScale = remember { Animatable(1f) }
    val fabAnimRotation = remember { Animatable(0f) }
    val ringScale = remember { Animatable(1f) }
    val ringAlpha = remember { Animatable(0f) }
    val badgePopScale = remember { Animatable(1f) }
    var lastHandledNavAnim by remember { mutableStateOf(cartAnimationTrigger) }

    LaunchedEffect(cartAnimationTrigger) {
        if (cartAnimationTrigger > 0L && cartAnimationTrigger != lastHandledNavAnim) {
            lastHandledNavAnim = cartAnimationTrigger
            launch {
                ringScale.snapTo(1f)
                ringAlpha.snapTo(0.85f)
                ringScale.animateTo(2.4f, tween(550, easing = FastOutSlowInEasing))
                ringAlpha.animateTo(0f, tween(550, easing = FastOutSlowInEasing))
            }
            launch {
                fabAnimScale.snapTo(1f)
                fabAnimScale.animateTo(1.38f, spring(dampingRatio = 0.35f, stiffness = 600f))
                fabAnimScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 400f))
            }
            launch {
                fabAnimRotation.snapTo(0f)
                fabAnimRotation.animateTo(-14f, tween(60, easing = LinearEasing))
                fabAnimRotation.animateTo(14f, tween(90, easing = LinearEasing))
                fabAnimRotation.animateTo(-8f, tween(70, easing = LinearEasing))
                fabAnimRotation.animateTo(0f, tween(90, easing = LinearEasing))
            }
            launch {
                badgePopScale.snapTo(0.4f)
                badgePopScale.animateTo(1.6f, spring(dampingRatio = 0.4f, stiffness = 500f))
                badgePopScale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val navIconSize = 24.dp

        // Sleek Floating Pill Container (Pristine White with subtle border)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0x33000000),
                    ambientColor = Color(0x1A000000)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(28.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home
                val isHomeSelected = selectedTab == 0
                val tab0Scale by animateFloatAsState(
                    targetValue = if (isHomeSelected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = 420f),
                    label = "tab0Scale"
                )
                val tab0Tint by animateColorAsState(
                    targetValue = if (isHomeSelected) PaletteTerracotta else Color(0xFF64748B),
                    animationSpec = tween(220),
                    label = "tab0Tint"
                )

                IconButton(
                    onClick = { onTabSelected(0) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isHomeSelected) PaletteTerracotta.copy(alpha = 0.12f) else Color.Transparent)
                ) {
                    Icon(
                        imageVector = if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = com.dhaaga.app.utils.AppLanguageManager.translate("home", currentLang, "Home"),
                        tint = tab0Tint,
                        modifier = Modifier
                            .size(navIconSize)
                            .graphicsLayer(scaleX = tab0Scale, scaleY = tab0Scale)
                    )
                }

                // Tab 1: Listings or Wishlist
                val isTab1Selected = selectedTab == 1
                val tab1Scale by animateFloatAsState(
                    targetValue = if (isTab1Selected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = 420f),
                    label = "tab1Scale"
                )
                val tab1Tint by animateColorAsState(
                    targetValue = if (isTab1Selected) PaletteTerracotta else Color(0xFF64748B),
                    animationSpec = tween(220),
                    label = "tab1Tint"
                )

                IconButton(
                    onClick = { onTabSelected(1) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isTab1Selected) PaletteTerracotta.copy(alpha = 0.12f) else Color.Transparent)
                ) {
                    Box {
                        Icon(
                            imageVector = if (isSeller) {
                                if (isTab1Selected) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2
                            } else {
                                if (isTab1Selected) com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Heart else Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (isSeller) com.dhaaga.app.utils.AppLanguageManager.translate("listings", currentLang, "My Crafts") else com.dhaaga.app.utils.AppLanguageManager.translate("wishlist", currentLang, "Wishlist"),
                            tint = tab1Tint,
                            modifier = Modifier
                                .size(navIconSize)
                                .graphicsLayer(scaleX = tab1Scale, scaleY = tab1Scale)
                        )
                        if (!isSeller && wishlistCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-4).dp)
                                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935))
                                    .border(1.2.dp, Color.White, CircleShape)
                                    .padding(horizontal = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (wishlistCount > 99) "99+" else "$wishlistCount",
                                    fontSize = 8.5.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Center Spacer for Protruding FAB
                Spacer(modifier = Modifier.width(56.dp))

                // Tab 3: Dashboard or Orders
                val isTab3Selected = selectedTab == 3
                val tab3Scale by animateFloatAsState(
                    targetValue = if (isTab3Selected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = 420f),
                    label = "tab3Scale"
                )
                val tab3Tint by animateColorAsState(
                    targetValue = if (isTab3Selected) PaletteTerracotta else Color(0xFF64748B),
                    animationSpec = tween(220),
                    label = "tab3Tint"
                )

                IconButton(
                    onClick = { onTabSelected(3) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isTab3Selected) PaletteTerracotta.copy(alpha = 0.12f) else Color.Transparent)
                ) {
                    Icon(
                        imageVector = if (isSeller) {
                            if (isTab3Selected) Icons.Filled.Dashboard else Icons.Outlined.Dashboard
                        } else {
                            if (isTab3Selected) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping
                        },
                        contentDescription = if (isSeller) com.dhaaga.app.utils.AppLanguageManager.translate("dashboard", currentLang, "Dashboard") else com.dhaaga.app.utils.AppLanguageManager.translate("orders", currentLang, "Orders"),
                        tint = tab3Tint,
                        modifier = Modifier
                            .size(navIconSize)
                            .graphicsLayer(scaleX = tab3Scale, scaleY = tab3Scale)
                    )
                }

                // Tab 4: Profile
                val isTab4Selected = selectedTab == 4
                val tab4Scale by animateFloatAsState(
                    targetValue = if (isTab4Selected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = 420f),
                    label = "tab4Scale"
                )
                val tab4Tint by animateColorAsState(
                    targetValue = if (isTab4Selected) PaletteTerracotta else Color(0xFF64748B),
                    animationSpec = tween(220),
                    label = "tab4Tint"
                )

                IconButton(
                    onClick = { onTabSelected(4) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isTab4Selected) PaletteTerracotta.copy(alpha = 0.12f) else Color.Transparent)
                ) {
                    Icon(
                        imageVector = if (isTab4Selected) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = com.dhaaga.app.utils.AppLanguageManager.translate("profile", currentLang, "Profile"),
                        tint = tab4Tint,
                        modifier = Modifier
                            .size(navIconSize)
                            .graphicsLayer(scaleX = tab4Scale, scaleY = tab4Scale)
                    )
                }
            }
        }

        // Expanding Glowing Ripple Ring behind FAB when Item is Added
        if (ringAlpha.value > 0.01f) {
            Box(
                modifier = Modifier
                    .offset(y = (-22).dp)
                    .size(56.dp)
                    .graphicsLayer(
                        scaleX = ringScale.value,
                        scaleY = ringScale.value,
                        alpha = ringAlpha.value
                    )
                    .clip(CircleShape)
                    .background(PaletteTerracotta.copy(alpha = 0.6f))
            )
        }

        // Center Floating Action Button (Elevated Cultural Terracotta FAB)
        val isTab2Selected = selectedTab == 2
        val baseFabScale by animateFloatAsState(
            targetValue = if (isTab2Selected) 1.08f else 1.0f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
            label = "baseFabScale"
        )
        val combinedScale = baseFabScale * fabAnimScale.value

        Box(
            modifier = Modifier
                .offset(y = (-22).dp)
                .size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main FAB Circle Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer(
                        scaleX = combinedScale,
                        scaleY = combinedScale,
                        rotationZ = fabAnimRotation.value
                    )
                    .shadow(
                        elevation = if (isTab2Selected || fabAnimScale.value > 1.05f) 18.dp else 12.dp,
                        shape = CircleShape,
                        spotColor = PaletteTerracotta,
                        ambientColor = Color(0x33000000)
                    )
                    .clip(CircleShape)
                    .background(PaletteTerracotta)
                    .clickable { onTabSelected(2) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSeller) Icons.Default.Add else com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.BagShopping,
                    contentDescription = if (isSeller) "Add Product" else "Cart",
                    tint = Color.White,
                    modifier = Modifier.size(navIconSize)
                )
            }

            // Elegant Notification Badge on top-right perimeter of the FAB
            if (!isSeller && cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-1).dp, y = 1.dp)
                        .graphicsLayer(
                            scaleX = badgePopScale.value,
                            scaleY = badgePopScale.value
                        )
                        .shadow(4.dp, CircleShape)
                        .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                        .border(2.dp, Color.White, CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartCount > 99) "99+" else "$cartCount",
                        fontSize = 10.5.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}
