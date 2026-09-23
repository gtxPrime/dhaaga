package com.dhaaga.app.ui.seller

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.ui.components.CardAsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager

@Composable
fun MyListingsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onAddProduct: () -> Unit
) {
    val currentLang by viewModel.selectedLanguage.collectAsState()
    fun tr(key: String, fallback: String): String = viewModel.tr(key, fallback)

    val sellerProducts by viewModel.sellerProducts.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    val activeProducts = remember(sellerProducts) { sellerProducts.filter { it.status == "active" && it.stockQuantity > 0 } }
    val draftProducts = remember(sellerProducts) { sellerProducts.filter { it.status == "draft" } }
    val soldProducts = remember(sellerProducts) { sellerProducts.filter { it.status == "sold" || it.stockQuantity <= 0 } }

    val displayedProducts = when (selectedTab) {
        0 -> activeProducts
        1 -> draftProducts
        2 -> soldProducts
        else -> activeProducts
    }

    val tabs = listOf(
        "${tr("tab_active", "Active")} (${activeProducts.size})",
        "${tr("tab_draft", "Draft")} (${draftProducts.size})",
        "${tr("tab_sold", "Sold")} (${soldProducts.size})"
    )

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("back_btn", "Back"), tint = Color.White)
                    }
                    Column {
                        Text(tr("listings", "My Listings"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${sellerProducts.size} ${tr("products_label", "products")}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = DhaagaPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = tr("add_product_title", "Add Product"), tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DhaagaSurface,
                contentColor = DhaagaPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = DhaagaPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == i) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedTab == i) DhaagaPrimary else DhaagaTextMedium
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (displayedProducts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(DhaagaPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory,
                                    contentDescription = null,
                                    tint = DhaagaPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                when (selectedTab) {
                                    0 -> tr("no_active_crafts", "No Active Crafts")
                                    1 -> tr("no_draft_crafts", "No Draft Crafts")
                                    2 -> tr("no_sold_crafts", "No Sold Crafts")
                                    else -> tr("no_listings_yet", "No Listings Yet")
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DhaagaTextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onAddProduct,
                                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                            ) {
                                Text(tr("add_first_product", "Add Your First Product"), color = Color.White)
                            }
                        }
                    }
                } else {
                    displayedProducts.forEach { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(product.productId) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                // Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    CardAsyncImage(
                                        model = product.primaryImageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        indicatorSize = 20.dp
                                    )
                                    if (product.hasGITag) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(4.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DhaagaGIBadge)
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("GI", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.titleEn,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DhaagaTextDark,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = product.priceDisplay,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DhaagaPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Chip(text = "${product.viewCount} ${tr("views_suffix", "views")}", icon = Icons.Default.RemoveRedEye)
                                        Chip(text = "${product.reviewCount} ${tr("reviews_suffix", "reviews")}", icon = Icons.Default.Star)
                                        Chip(text = "${product.stockQuantity} ${tr("left_in_stock", "left")}", icon = Icons.Default.Inventory)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {},
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, DhaagaPrimary)
                                        ) {
                                            Text(tr("edit_btn", "Edit"), fontSize = 12.sp, color = DhaagaPrimary)
                                        }
                                        OutlinedButton(
                                            onClick = {},
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, DhaagaError)
                                        ) {
                                            Text(tr("pause_btn", "Pause"), fontSize = 12.sp, color = DhaagaError)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DhaagaSurface)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = DhaagaTextMedium, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text, fontSize = 10.sp, color = DhaagaTextMedium)
        }
    }
}
