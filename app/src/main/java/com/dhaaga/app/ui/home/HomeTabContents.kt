package com.dhaaga.app.ui.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.data.model.CartItemModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.components.NotionAvatar
import com.dhaaga.app.ui.theme.*

/**
 * Common Top Header for Swipeable Tabs in Dhaaga Forest Sage Palette
 */
@Composable
fun TabHeaderBlock(
    title: String,
    subtitle: String,
    actionIcon: ImageVector? = null,
    onActionClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PaletteForest, PaletteSage)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
            if (actionIcon != null) {
                IconButton(
                    onClick = onActionClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                ) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. WISHLIST TAB (Buyer Tab 1)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WishlistTabContent(
    viewModel: AppViewModel,
    onProductClick: (String) -> Unit,
    onExploreProducts: () -> Unit
) {
    val context = LocalContext.current
    val wishlist by viewModel.wishlist.collectAsState()
    val products by viewModel.products.collectAsState()
    val wishlistedProducts = products.filter { wishlist.contains(it.productId) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "My Wishlist",
            subtitle = "${wishlistedProducts.size} Saved Crafts"
        )

        if (wishlistedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(PaletteGreenTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = PaletteForest,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Wishlist is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Save the heritage pieces you love by tapping the heart icon on any craft.",
                        fontSize = 13.sp,
                        color = PaletteSage,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreProducts,
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("← Explore Crafts", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wishlistedProducts, key = { it.productId }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick(product.productId) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = product.primaryImageUrl,
                                    contentDescription = product.titleEn,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (product.hasGITag) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PaletteForest)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("GI", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.titleEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "by ${product.sellerName}",
                                    fontSize = 11.sp,
                                    color = PaletteSage
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.priceDisplay,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaletteForest
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.addToCart(product)
                                            Toast.makeText(context, "Added to Bag: ${product.titleEn}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
                                    ) {
                                        Text("Add to Bag", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleWishlist(product.productId) },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFEBEE))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFD32F2F),
                                            modifier = Modifier.size(18.dp)
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
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. CART TAB (Buyer Tab 2 - Center FAB)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CartTabContent(
    viewModel: AppViewModel,
    onExplore: () -> Unit,
    onCheckout: () -> Unit
) {
    val context = LocalContext.current
    val cart by viewModel.cart.collectAsState()
    val total = cart.sumOf { it.totalPrice }
    val platformFee = (total * 0.08).toLong()
    val shipping = if (total > 100000L || cart.isEmpty()) 0L else 5000L
    val grandTotal = total + platformFee + shipping

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "Shopping Bag",
            subtitle = "${cart.size} Handcrafted ${if (cart.size == 1) "Item" else "Items"}"
        )

        if (cart.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(PaletteGreenTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = PaletteForest,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Bag is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Discover authentic tribal arts, handcrafted textiles, and direct artisan treasures.",
                        fontSize = 13.sp,
                        color = PaletteSage,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExplore,
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("← Start Shopping", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Free Shipping Progress
                item {
                    if (total < 100000L) {
                        val remaining = (100000L - total) / 100
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaletteGreenTint)
                                .border(1.dp, PaletteSage.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add ₹$remaining more for FREE Shipping nationwide!",
                                    fontSize = 12.sp,
                                    color = PaletteDarkGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaletteGreenTint)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "You've unlocked FREE Guaranteed Express Shipping!",
                                    fontSize = 12.sp,
                                    color = PaletteForest,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Cart Items
                items(cart, key = { it.productId }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = item.productImageUrl,
                                    contentDescription = item.productTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.productTitle,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "Artisan: ${item.sellerName}",
                                            fontSize = 11.sp,
                                            color = PaletteSage
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(item.productId) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = PaletteSage, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.totalDisplay,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PaletteForest
                                    )

                                    // Stepper
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PaletteGreenTint)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateCartQuantity(item.productId, item.quantity - 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Minus", tint = PaletteForest, modifier = Modifier.size(14.dp))
                                        }
                                        Text(
                                            "${item.quantity}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaletteDarkGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateCartQuantity(item.productId, item.quantity + 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Plus", tint = PaletteForest, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bill Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PaletteGreenTint.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Price Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Craft Subtotal", fontSize = 13.sp, color = PaletteSage)
                                Text("₹${total / 100}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PaletteDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Artisan Welfare & Platform (8%)", fontSize = 13.sp, color = PaletteSage)
                                Text("₹${platformFee / 100}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PaletteDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Express Delivery", fontSize = 13.sp, color = PaletteSage)
                                Text(if (shipping == 0L) "FREE" else "₹${shipping / 100}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = PaletteSage.copy(alpha = 0.3f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                                Text("₹${grandTotal / 100}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PaletteForest)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                                    viewModel.clearCart()
                                    onCheckout()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Checkout Securely • ₹${grandTotal / 100}", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. ORDERS TAB (Buyer Tab 3)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MyOrdersTabContent(
    onExplore: () -> Unit
) {
    val orders = MockData.mockOrders

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "My Orders",
            subtitle = "Live Artisan Shipments & History"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders, key = { it.orderId }) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Order #${order.orderId}",
                                fontSize = 12.sp,
                                color = PaletteSage,
                                fontWeight = FontWeight.Bold
                            )
                            val statusColor = when (order.status) {
                                "shipped" -> PaletteForest
                                "delivered" -> Color(0xFF2E7D32)
                                else -> Color(0xFFE65100)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (order.status) {
                                        "shipped" -> "Shipped"
                                        "delivered" -> "Delivered"
                                        else -> "In Workshop"
                                    },
                                    fontSize = 11.sp,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = order.productImageUrl,
                                    contentDescription = order.productTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    order.productTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("Artisan: ${order.sellerName}", fontSize = 11.sp, color = PaletteSage)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(order.totalDisplay, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PaletteForest)
                            }
                        }
                        if (order.trackingId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PaletteGreenTint)
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.LocalShipping,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Courier: ${order.shippingCarrier}", fontSize = 11.sp, color = PaletteSage)
                                        Text(
                                            "Tracking No: ${order.trackingId}",
                                            fontSize = 11.sp,
                                            color = PaletteForest,
                                            fontWeight = FontWeight.Bold
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
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. MY LISTINGS TAB (Seller Tab 1)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MyListingsTabContent(
    viewModel: AppViewModel,
    onProductClick: (String) -> Unit,
    onAddProduct: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    var selectedFilter by remember { mutableStateOf(0) }
    val filters = listOf("All (${products.size})", "Active (${products.size})", "Draft (0)", "Sold (12)")

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "My Craft Listings",
            subtitle = "${products.size} Products in Online Store",
            actionIcon = Icons.Default.Add,
            onActionClick = onAddProduct
        )

        // Filter chips
        ScrollableTabRow(
            selectedTabIndex = selectedFilter,
            containerColor = Color.White,
            contentColor = PaletteForest,
            edgePadding = 16.dp,
            divider = {}
        ) {
            filters.forEachIndexed { index, filter ->
                Tab(
                    selected = selectedFilter == index,
                    onClick = { selectedFilter = index },
                    text = {
                        Text(
                            filter,
                            fontWeight = if (selectedFilter == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFilter == index) PaletteForest else PaletteSage
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.productId }) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProductClick(product.productId) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = product.primaryImageUrl,
                                contentDescription = product.titleEn,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                product.titleEn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteDarkGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "Category: ${product.craftType}",
                                fontSize = 11.sp,
                                color = PaletteSage
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    product.priceDisplay,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaletteForest
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PaletteGreenTint)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("In Stock: ${product.stockQuantity}", fontSize = 10.sp, color = PaletteForest, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. ADD PRODUCT TAB (Seller Tab 2 - Center FAB)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AddProductTabContent(
    onProductAdded: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var craftType by remember { mutableStateOf("Warli Painting") }
    var material by remember { mutableStateOf("Natural Earth Pigments") }
    var isGI by remember { mutableStateOf(true) }
    var quantity by remember { mutableStateOf("5") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "List New Craft",
            subtitle = "Direct Artisan-to-Marketplace Listing"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Photo Upload Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PaletteGreenTint)
                    .border(1.5.dp, PaletteSage.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable {
                        Toast.makeText(context, "Camera & Gallery Picker Ready", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tap to capture or upload craft photo", fontSize = 13.sp, color = PaletteDarkGreen, fontWeight = FontWeight.Bold)
                    Text("High resolution natural lighting recommended", fontSize = 11.sp, color = PaletteSage)
                }
            }

            // Fields
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Craft Title (English / Regional)") },
                placeholder = { Text("e.g. Handpainted Madhubani Peacock Canvas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PaletteForest,
                    unfocusedBorderColor = PaletteSage.copy(alpha = 0.4f)
                )
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Selling Price (₹)") },
                placeholder = { Text("e.g. 1200") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PaletteForest,
                    unfocusedBorderColor = PaletteSage.copy(alpha = 0.4f)
                )
            )

            OutlinedTextField(
                value = craftType,
                onValueChange = { craftType = it },
                label = { Text("Craft Tradition") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PaletteForest,
                    unfocusedBorderColor = PaletteSage.copy(alpha = 0.4f)
                )
            )

            // GI Tag Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PaletteGreenTint)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GI Tag Certified Craft", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                        Text("Authentic geographical origin verification", fontSize = 11.sp, color = PaletteSage)
                    }
                    Switch(
                        checked = isGI,
                        onCheckedChange = { isGI = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PaletteForest
                        )
                    )
                }
            }

            Button(
                onClick = {
                    Toast.makeText(context, "Craft Published to Marketplace!", Toast.LENGTH_LONG).show()
                    onProductAdded()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publish Craft to Marketplace", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. SELLER DASHBOARD TAB (Seller Tab 3)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SellerDashboardTabContent(
    onViewOrders: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "Artisan Dashboard",
            subtitle = "Direct Revenue & Performance"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Earnings Row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaletteForest)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("This Month", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹32,450", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("+18% vs last month", fontSize = 10.sp, color = Color(0xFFA5D6A7), fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaletteGreenTint)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Orders", fontSize = 12.sp, color = PaletteSage)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("14 Orders", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PaletteDarkGreen)
                        Text("6 ready to ship", fontSize = 10.sp, color = PaletteForest, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Shilpi Score Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(PaletteForest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("94", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Master Shilpi Verified", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
                        }
                        Text("Top 5% authentic guild craft score nationwide", fontSize = 11.sp, color = PaletteSage)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. PROFILE TAB (Buyer & Seller Tab 4)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ProfileTabContent(
    viewModel: AppViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val isSeller = user?.isSeller == true

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = "My Profile",
            subtitle = if (isSeller) "Master Artisan Guild" else "Craft Connoisseur"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User ID Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NotionAvatar(
                        name = user?.name ?: "Artisan",
                        size = 64.dp,
                        borderWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            user?.name ?: "Savita Dhodi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            user?.phoneNumber ?: "+91 98765 43210",
                            fontSize = 12.sp,
                            color = PaletteSage
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PaletteForest)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSeller) Icons.Default.Storefront else Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isSeller) "Artisan Seller" else "Verified Buyer",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }



            // Settings List
            ProfileOptionRow(icon = Icons.Outlined.Language, label = "Language (भाषा)", value = "English (हिंदी)") {
                Toast.makeText(context, "Language: English", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.LocationOn, label = "Saved Addresses", value = "Palghar, MH") {
                Toast.makeText(context, "Addresses Managed", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.AccountBalance, label = "Bank & UPI Payouts", value = "Active") {
                Toast.makeText(context, "UPI Verified", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.SupportAgent, label = "Artisan Guild Helpline", value = "24x7") {
                Toast.makeText(context, "Helpline: 1800-DHAAGA", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.VerifiedUser, label = "GI & Fair Trade Guarantee", value = "100%") {
                Toast.makeText(context, "GI Certified", Toast.LENGTH_SHORT).show()
            }

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of Dhaaga", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PaletteDarkGreen)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, fontSize = 12.sp, color = PaletteSage)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PaletteSage, modifier = Modifier.size(16.dp))
            }
        }
    }
}
