package com.dhaaga.app.ui.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.R
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.components.NotionAvatar
import com.dhaaga.app.ui.theme.*
import kotlinx.coroutines.delay

data class HomeBannerItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val badge: String,
    val imageUrl: String
)

@OptIn(ExperimentalSharedTransitionApi::class)
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
    onChatList: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val user by viewModel.currentUser.collectAsState()
    val products by viewModel.products.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartAnimationEvent by viewModel.cartAnimationEvent.collectAsState()
    val lastAddedProduct by viewModel.lastAddedProduct.collectAsState()
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

    // Demo Banners for Auto-Loop Slider (Uploaded to Live PHP Server)
    val bannerItems = remember {
        listOf(
            HomeBannerItem(
                id = 1,
                title = "Artisan Handloom Week",
                subtitle = "Preserving Centuries of Heritage Crafts",
                badge = "HANDLOOM SPECIAL",
                imageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190707_bf398ccf1376.jpg"
            ),
            HomeBannerItem(
                id = 2,
                title = "Authentic Madhubani Art",
                subtitle = "Handcrafted by Master Folk Artists",
                badge = "GI CERTIFIED CRAFTS",
                imageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194856_3422de9243b3.jpg"
            ),
            HomeBannerItem(
                id = 3,
                title = "Terracotta & Metalwork",
                subtitle = "Direct Workshop Pricing from Rural Artisans",
                badge = "DIRECT ARTISAN SALE",
                imageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190717_1baa44af0567.jpg"
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
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2, animationSpec = tween(350, easing = FastOutSlowInEasing))
                        }
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
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
                        onViewOrders = { }
                    )
                } else {
                    MyOrdersTabContent(
                        onExplore = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        }
                    )
                }
                4 -> ProfileTabContent(
                    viewModel = viewModel,
                    onLogout = {
                        viewModel.logout()
                        onProfile()
                    }
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
                            text = "Added to Bag",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = bannerProduct?.titleEn ?: "",
                            fontSize = 11.sp,
                            color = PaletteGreenTint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = {
                            showAddedBanner = false
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2, animationSpec = tween(350, easing = FastOutSlowInEasing))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("View Bag", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val isSearching = searchQuery.isNotBlank() || selectedCategory != "All"

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
                    onNotificationClick = {}
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
                            Text(
                                text = if (searchQuery.isNotBlank()) "RESULTS FOR \"$searchQuery\"" else "CATEGORY: ${selectedCategory.uppercase()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${filteredProducts.size} authentic craft${if (filteredProducts.size == 1) "" else "s"} found",
                                fontSize = 11.5.sp,
                                color = DhaagaTextLight
                            )
                        }
                        Text(
                            text = "Clear Filter",
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
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // Interactive Auto-Loop Image Banner Slider
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HeroBannerSlider(
                        banners = bannerItems,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Section 1: Trending This Week
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionHeaderRow(
                        title = "TRENDING THIS WEEK",
                        icon = Icons.Default.Whatshot,
                        iconColor = DhaagaPrimary,
                        onSeeAll = { onCategorySelected("Handloom") }
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
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }

                // Section 2: All Products
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeaderRow(
                        title = "ALL PRODUCTS",
                        subtitle = "${products.size} Items",
                        icon = Icons.Default.GridView,
                        iconColor = DhaagaPrimary,
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
                                animatedVisibilityScope = animatedVisibilityScope
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
@Composable
private fun HeaderBlock(
    user: UserModel?,
    isSeller: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onChatClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF60734E), Color(0xFF738861))
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (isSeller) "Artisan • ${user?.village ?: "India"}" else "Craft Lover • ${user?.name?.split(" ")?.firstOrNull() ?: "India"}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
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
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "Chat",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

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
                    NotionAvatar(
                        name = user.name,
                        size = 34.dp,
                        borderWidth = 1.5.dp
                    )
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
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (searchQuery.isNotEmpty()) PaletteForest else Color(0xFF888888),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search products, artisans, crafts...",
                            color = Color(0xFF888888),
                            fontSize = 13.sp
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
                            Toast.makeText(context, "Voice search: Listening for craft name...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint = PaletteForest,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Category / Craft Filter Chips
            val categories = listOf("All", "Warli Art", "Madhubani", "Handloom", "Terracotta", "Jewellery", "GI Certified")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.22f)
                            )
                            .clickable {
                                onCategorySelected(cat)
                            }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PaletteForest else Color.White
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                        Toast.makeText(context, "Clicked Banner Item ID: ${banner.id}", Toast.LENGTH_SHORT).show()
                    }
            ) {
                // Background Image
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
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
    animatedVisibilityScope: AnimatedVisibilityScope? = null
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
                AsyncImage(
                    model = product.primaryImageUrl,
                    contentDescription = product.titleEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // GI Tag Badge (Top Left)
                if (product.hasGITag) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PaletteForest)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "GI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
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
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color.Red else Color(0xFF555555),
                        modifier = Modifier.size(18.dp)
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
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
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
                    Text(
                        text = product.titleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = product.sellerVillage,
                        fontSize = 11.sp,
                        color = DhaagaTextLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = product.priceDisplay,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteForest
                        )
                        Text(
                            text = "₹${(product.priceListed * 1.3 / 100).toInt()}",
                            fontSize = 11.sp,
                            color = Color(0xFF999999),
                            textDecoration = TextDecoration.LineThrough
                        )
                        Text(
                            text = "23% off",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteForest
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                var isButtonBouncing by remember { mutableStateOf(false) }
                val buttonScale by animateFloatAsState(
                    targetValue = if (isButtonBouncing) 0.90f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
                    finishedListener = { isButtonBouncing = false },
                    label = "btnScale"
                )

                // Add to Cart Button
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
                        .height(36.dp)
                        .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isInCart) PaletteForest else Color(0xFFEFF4EB)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isInCart) Icons.Default.Check else Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = if (isInCart) Color.White else PaletteForest,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isInCart) "In Bag • View" else "Add to Bag",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInCart) Color.White else PaletteForest
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section Header Row
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PaletteDarkGreen,
            letterSpacing = 0.5.sp
        )
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = DhaagaTextLight
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "See all >",
            fontSize = 12.sp,
            color = PaletteForest,
            fontWeight = FontWeight.Bold,
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
        // Exact Lore App Floating Pill Container (#E2EAD9)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0x4460734E),
                    ambientColor = Color(0x1F000000)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFE2EAD9)) // Rich Soft Sage Green Tint
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
                IconButton(onClick = { onTabSelected(0) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = "Home",
                        tint = if (isHomeSelected) PaletteForest else Color(0xFF6E8260),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Tab 1: Listings or Wishlist
                val isTab1Selected = selectedTab == 1
                IconButton(
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Box {
                        Icon(
                            imageVector = if (isSeller) {
                                if (isTab1Selected) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2
                            } else {
                                if (isTab1Selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (isSeller) "Listings" else "Wishlist",
                            tint = if (isTab1Selected) PaletteForest else Color(0xFF6E8260),
                            modifier = Modifier.size(24.dp)
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
                IconButton(
                    onClick = { onTabSelected(3) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isSeller) {
                            if (isTab3Selected) Icons.Filled.BarChart else Icons.Outlined.BarChart
                        } else {
                            if (isTab3Selected) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping
                        },
                        contentDescription = if (isSeller) "Dashboard" else "Orders",
                        tint = if (isTab3Selected) PaletteForest else Color(0xFF6E8260),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Tab 4: Profile
                val isTab4Selected = selectedTab == 4
                IconButton(onClick = { onTabSelected(4) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (isTab4Selected) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = if (isTab4Selected) PaletteForest else Color(0xFF6E8260),
                        modifier = Modifier.size(24.dp)
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
                    .background(PaletteForest.copy(alpha = 0.6f))
            )
        }

        // Center Floating Action Button (Elevated Forest Sage FAB with Bounce & Rotation)
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
                        spotColor = PaletteForest,
                        ambientColor = PaletteSage
                    )
                    .clip(CircleShape)
                    .background(PaletteForest)
                    .clickable { onTabSelected(2) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSeller) Icons.Default.Add else Icons.Default.ShoppingCart,
                    contentDescription = if (isSeller) "Add Product" else "Cart",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
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
