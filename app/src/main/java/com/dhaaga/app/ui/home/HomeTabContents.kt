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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
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
            title = viewModel.tr("wishlist", "My Wishlist"),
            subtitle = "${wishlistedProducts.size} ${viewModel.tr("items_suffix", "Saved Crafts")}"
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
            title = viewModel.tr("bag", "Shopping Bag"),
            subtitle = "${cart.size} ${viewModel.tr("items_suffix", "Items")}"
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
                        text = viewModel.tr("your_bag_empty", "Your Bag is Empty"),
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
                            Text(viewModel.tr("order_summary", "Order Summary"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(viewModel.tr("subtotal", "Craft Subtotal"), fontSize = 13.sp, color = PaletteSage)
                                Text("₹${total / 100}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PaletteDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Platform & Guild Welfare (8%)", fontSize = 13.sp, color = PaletteSage)
                                Text("₹${platformFee / 100}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PaletteDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(viewModel.tr("delivery", "Delivery"), fontSize = 13.sp, color = PaletteSage)
                                Text(
                                    if (shipping == 0L) viewModel.tr("free", "Free") else "₹${shipping / 100}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (shipping == 0L) PaletteForest else PaletteDarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = PaletteSage.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(viewModel.tr("total_amount", "Total Amount"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                                Text("₹${grandTotal / 100}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = PaletteForest)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val currentUser = viewModel.currentUser.value
                                    viewModel.placeDemoOrder(cart, currentUser) {
                                        Toast.makeText(context, "Order Placed Successfully! Artisan Dashboard Updated.", Toast.LENGTH_LONG).show()
                                        onCheckout()
                                    }
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
    viewModel: AppViewModel,
    onExplore: () -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.artisanOrders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = viewModel.tr("orders", "My Orders"),
            subtitle = "${orders.size} ${viewModel.tr("items_suffix", "Orders Tracked")}"
        )

        if (orders.isEmpty()) {
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
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = PaletteForest,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Orders Placed Yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore authentic handcrafted pieces directly from rural artisans.",
                        fontSize = 13.sp,
                        color = PaletteSage,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExplore,
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(viewModel.tr("explore_crafts_btn", "Explore Crafts"), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
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
                                val isCancelled = order.status == "cancelled"
                                val statusColor = when (order.status) {
                                    "shipped" -> PaletteForest
                                    "delivered" -> Color(0xFF2E7D32)
                                    "cancelled" -> Color(0xFFD32F2F)
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
                                            "cancelled" -> "Cancelled • Stock Restored"
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
                                    Text("Artisan: ${order.sellerName} • Qty: ${order.quantity}", fontSize = 11.sp, color = PaletteSage)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(order.totalDisplay, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PaletteForest)
                                }
                            }

                            // Interactive Cancellation Button (Auto-restores stock on both sides)
                            if (order.status != "cancelled" && order.status != "delivered") {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.cancelOrder(order.orderId)
                                        Toast.makeText(context, "Order #${order.orderId} cancelled! Stock automatically restored on both sides.", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                    border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cancel Order (Restores Stock)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD32F2F))
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
    val sellerProducts by viewModel.sellerProducts.collectAsState()
    var selectedFilter by remember { mutableStateOf(0) }
    var productToEdit by remember { mutableStateOf<ProductModel?>(null) }

    val allProducts = sellerProducts
    val activeProducts = remember(sellerProducts) { sellerProducts.filter { it.status == "active" && it.stockQuantity > 0 } }
    val draftProducts = remember(sellerProducts) { sellerProducts.filter { it.status == "draft" } }
    val soldProducts = remember(sellerProducts) { sellerProducts.filter { it.status == "sold" || it.stockQuantity <= 0 } }

    val displayedProducts = when (selectedFilter) {
        1 -> activeProducts
        2 -> draftProducts
        3 -> soldProducts
        else -> allProducts
    }

    val filters = listOf(
        "All (${allProducts.size})",
        "Active (${activeProducts.size})",
        "Draft (${draftProducts.size})",
        "Sold (${soldProducts.size})"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = viewModel.tr("my_craft_listings", "My Craft Listings"),
            subtitle = "${allProducts.size} ${viewModel.tr("products_in_store", "Products in Online Store")}",
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

        if (displayedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PaletteGreenTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = PaletteForest,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        when (selectedFilter) {
                            1 -> "No Active Crafts"
                            2 -> "No Draft Crafts"
                            3 -> "No Sold Crafts"
                            else -> "No Crafts Uploaded Yet"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Upload authentic handcrafted items to reach buyers directly.",
                        fontSize = 13.sp,
                        color = PaletteSage,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onAddProduct,
                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(viewModel.tr("list_new_craft", "List New Craft"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 130.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedProducts, key = { it.productId }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { productToEdit = product },
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
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { productToEdit = product },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Craft",
                                    tint = PaletteForest,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Edit Craft Bottom Sheet
        if (productToEdit != null) {
            val ctx = LocalContext.current
            EditCraftSheet(
                product = productToEdit!!,
                onDismiss = { productToEdit = null },
                onSave = { updatedProduct ->
                    viewModel.addUploadedProduct(updatedProduct)
                    productToEdit = null
                    Toast.makeText(ctx, "Craft updated successfully!", Toast.LENGTH_SHORT).show()
                },
                onViewPublic = {
                    val id = productToEdit!!.productId
                    productToEdit = null
                    onProductClick(id)
                },
                onDelete = {
                    viewModel.deleteUploadedProduct(productToEdit!!.productId)
                    productToEdit = null
                    Toast.makeText(ctx, "Craft removed from store.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCraftSheet(
    product: ProductModel,
    onDismiss: () -> Unit,
    onSave: (ProductModel) -> Unit,
    onViewPublic: () -> Unit,
    onDelete: () -> Unit
) {
    var titleEn by remember(product) { mutableStateOf(product.titleEn) }
    var titleHi by remember(product) { mutableStateOf(product.titleHi) }
    var descriptionEn by remember(product) { mutableStateOf(product.descriptionEn) }
    var descriptionHi by remember(product) { mutableStateOf(product.descriptionHi) }
    var priceRupees by remember(product) { mutableStateOf((product.priceListed / 100).toString()) }
    var stockQty by remember(product) { mutableStateOf(product.stockQuantity.toString()) }
    var craftType by remember(product) { mutableStateOf(product.craftType) }
    var material by remember(product) { mutableStateOf(product.material) }
    var sizeCm by remember(product) { mutableStateOf(product.sizeCm) }
    var technique by remember(product) { mutableStateOf(product.technique) }
    var region by remember(product) { mutableStateOf(product.region) }
    var status by remember(product) { mutableStateOf(product.status) }
    var enableSalePrice by remember(product) { mutableStateOf(product.salePricePaise != null && product.salePricePaise > 0L) }
    var salePriceRupees by remember(product) { mutableStateOf(product.salePricePaise?.let { (it / 100).toString() } ?: "") }
    var enableCoupon by remember(product) { mutableStateOf(!product.couponCode.isNullOrBlank()) }
    var discountType by remember(product) { mutableStateOf(product.discountType) }
    var discountValue by remember(product) { mutableStateOf(if (product.discountValue > 0) product.discountValue.toString() else "15") }
    var couponCode by remember(product) { mutableStateOf(product.couponCode ?: "") }
    var couponDurationMinutes by remember(product) {
        val remaining = if (product.couponExpiryTimestamp != null) {
            ((product.couponExpiryTimestamp - System.currentTimeMillis()) / (60 * 1000L)).coerceAtLeast(10L)
        } else {
            10080L
        }
        mutableStateOf(remaining)
    }
    var couponUsageLimit by remember(product) { mutableStateOf(product.couponUsageLimit) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Edit Craft Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaletteDarkGreen
                    )
                    Text(
                        text = "Tap any field to edit details and update live store",
                        fontSize = 11.5.sp,
                        color = PaletteSage
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = PaletteSage)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Item Preview Card (Show the item)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaletteGreenTint.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
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
                            text = product.titleEn,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Artisan: ${product.sellerName.ifEmpty { "Artisan" }} • ${product.sellerVillage}",
                            fontSize = 11.sp,
                            color = PaletteSage
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${priceRupees.ifBlank { "0" }}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PaletteForest
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (status == "active") PaletteForest else Color(0xFFF57C00)
                            ) {
                                Text(
                                    text = status.replaceFirstChar { it.uppercase() },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = onViewPublic,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Public Preview",
                            tint = PaletteForest,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Selector Chips
            Text("Listing Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PaletteDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("active" to "Active (Live)", "draft" to "Draft (Hidden)", "sold" to "Sold Out").forEach { (code, label) ->
                    val isSelected = status == code
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PaletteForest else Color(0xFFF1F4EE),
                        modifier = Modifier.weight(1f).clickable { status = code }
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PaletteDarkGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Form Fields
            OutlinedTextField(
                value = titleEn,
                onValueChange = { titleEn = it },
                label = { Text("Product Title (English)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = titleHi,
                onValueChange = { titleHi = it },
                label = { Text("Product Title (Hindi / स्थानीय भाषा)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = priceRupees,
                    onValueChange = { priceRupees = it.filter { c -> c.isDigit() } },
                    label = { Text("Price (₹)") },
                    prefix = { Text("₹ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = stockQty,
                    onValueChange = { stockQty = it.filter { c -> c.isDigit() } },
                    label = { Text("Stock Qty") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = craftType,
                    onValueChange = { craftType = it },
                    label = { Text("Craft Category") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = material,
                    onValueChange = { material = it },
                    label = { Text("Material") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = sizeCm,
                    onValueChange = { sizeCm = it },
                    label = { Text("Dimensions / Size") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Origin Region") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descriptionEn,
                onValueChange = { descriptionEn = it },
                label = { Text("Description (English)") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descriptionHi,
                onValueChange = { descriptionHi = it },
                label = { Text("Description (Hindi)") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Discounts, Coupons & Special Offers
            com.dhaaga.app.ui.components.ProductDiscountCouponCard(
                basePriceRupees = priceRupees.toLongOrNull() ?: 0L,
                enableSalePrice = enableSalePrice,
                onEnableSalePriceChange = { enableSalePrice = it },
                salePriceRupees = salePriceRupees,
                onSalePriceChange = { salePriceRupees = it },
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

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = {
                    val basePricePaise = (priceRupees.toLongOrNull() ?: 0L) * 100L
                    val salePriceVal = if (enableSalePrice) salePriceRupees.toLongOrNull() else null
                    val salePricePaise = if (salePriceVal != null && salePriceVal > 0 && salePriceVal * 100L < basePricePaise) salePriceVal * 100L else null
                    val expiryTimestamp = if (enableCoupon && couponCode.isNotBlank()) System.currentTimeMillis() + (couponDurationMinutes * 60 * 1000L) else null
                    val qty = stockQty.toIntOrNull() ?: 1
                    val updated = product.copy(
                        titleEn = titleEn,
                        titleHi = titleHi,
                        descriptionEn = descriptionEn,
                        descriptionHi = descriptionHi,
                        priceListed = basePricePaise,
                        priceMin = (basePricePaise * 0.9).toLong(),
                        stockQuantity = qty,
                        craftType = craftType,
                        material = material,
                        sizeCm = sizeCm,
                        technique = technique,
                        region = region,
                        status = status,
                        salePricePaise = salePricePaise,
                        hasDiscount = enableSalePrice || enableCoupon,
                        discountType = discountType,
                        discountValue = discountValue.toLongOrNull() ?: 0L,
                        couponCode = if (enableCoupon && couponCode.isNotBlank()) couponCode else null,
                        couponExpiryTimestamp = expiryTimestamp,
                        couponUsageLimit = if (enableCoupon) couponUsageLimit else 0,
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(updated)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Update Craft", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Remove Craft from Listings", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD32F2F))
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. ADD PRODUCT TAB (Seller Tab 2 - Center FAB)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AddProductTabContent(
    viewModel: AppViewModel,
    onProductAdded: () -> Unit
) {
    com.dhaaga.app.ui.seller.AddProductScreen(
        viewModel = viewModel,
        onBack = onProductAdded,
        onPublish = onProductAdded,
        showTopBar = false,
        bottomPadding = 150.dp
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. SELLER DASHBOARD TAB (Seller Tab 3)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SellerDashboardTabContent(
    viewModel: AppViewModel,
    onViewOrders: () -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.artisanOrders.collectAsState()
    val totalRevenuePaise = viewModel.dashboardEarningsPaise
    val activeOrdersCount = viewModel.dashboardOrdersCount
    val readyToShip = viewModel.dashboardReadyToShipCount

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeaderBlock(
            title = viewModel.tr("artisan_dashboard", "Artisan Dashboard"),
            subtitle = viewModel.tr("live_revenue_tracking", "Live Revenue & Order Tracking")
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
                        Text(viewModel.tr("this_month_payout", "This Month Payout"), fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${"%,d".format(totalRevenuePaise / 100)}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text(viewModel.tr("live_revenue_tracking", "Real-time Payout Balance"), fontSize = 10.sp, color = Color(0xFFA5D6A7), fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaletteGreenTint)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(viewModel.tr("active_orders_label", "Active Orders"), fontSize = 12.sp, color = PaletteSage)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$activeOrdersCount ${viewModel.tr("items_suffix", "Orders")}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PaletteDarkGreen)
                        Text("$readyToShip ${viewModel.tr("ready_to_ship_label", "ready to ship")}", fontSize = 10.sp, color = PaletteForest, fontWeight = FontWeight.Bold)
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
                        Text("98", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Master Shilpi Verified", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
                        }
                        Text("Top 2% authentic guild craft score nationwide", fontSize = 11.sp, color = PaletteSage)
                    }
                }
            }

            // Live Customer Orders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Live Customer Orders (${orders.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen
                )
                Text(
                    "Auto-Sync",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteForest
                )
            }

            if (orders.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No orders received yet. Place a demo purchase as buyer to see live updates!", fontSize = 12.sp, color = DhaagaTextMedium, textAlign = TextAlign.Center)
                    }
                }
            } else {
                orders.forEach { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PaletteGreenTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    order.productTitle.ifEmpty { "Handmade Artisan Craft" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Buyer: ${order.buyerName.ifEmpty { "Customer" }} • Qty: ${order.quantity}",
                                    fontSize = 11.5.sp,
                                    color = DhaagaTextLight
                                )
                                Text(
                                    "Order ID: ${order.orderId}",
                                    fontSize = 10.5.sp,
                                    color = PaletteSage
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "₹${order.sellerPayout / 100}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PaletteForest
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (order.status == "cancelled") Color(0xFFFFEBEE) else PaletteForest.copy(alpha = 0.12f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        if (order.status == "cancelled") "Cancelled • Restocked" else order.statusDisplay,
                                        fontSize = 9.5.sp,
                                        color = if (order.status == "cancelled") Color(0xFFD32F2F) else PaletteDarkGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Order Fulfillment & Auto-Restock Controls
                        if (order.status != "cancelled" && order.status != "delivered") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (order.status == "confirmed") {
                                    Button(
                                        onClick = {
                                            viewModel.updateOrderStatus(order.orderId, "shipped")
                                            Toast.makeText(context, "Order marked as Shipped!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PaletteForest),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ship Order", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                } else if (order.status == "shipped") {
                                    Button(
                                        onClick = {
                                            viewModel.updateOrderStatus(order.orderId, "delivered")
                                            Toast.makeText(context, "Order marked as Delivered!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Delivered", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.cancelOrder(order.orderId)
                                        Toast.makeText(context, "Order #${order.orderId} cancelled. ${order.quantity} unit(s) returned to stock.", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                    border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cancel & Restock", fontSize = 11.5.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
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
// 7. PROFILE TAB (Buyer & Seller Tab 4)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(
    viewModel: AppViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val isSeller = user?.isSeller == true
    val currentLang by viewModel.selectedLanguage.collectAsState()
    var showLanguageSheet by remember { mutableStateOf(false) }

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
                            user?.name ?: if (isSeller) "Artisan" else "Buyer",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = PaletteForest,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = viewModel.loggedInPhone,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PaletteDarkGreen
                            )
                        }
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
            ProfileOptionRow(
                icon = Icons.Outlined.PhoneAndroid,
                label = "Logged-in Phone Number",
                value = viewModel.loggedInPhone
            ) {
                Toast.makeText(context, "Logged in as ${viewModel.loggedInPhone}", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(
                icon = Icons.Outlined.Language,
                label = viewModel.tr("language", "Language (भाषा)"),
                value = com.dhaaga.app.utils.AppLanguageManager.getLanguageName(currentLang)
            ) {
                showLanguageSheet = true
            }
            ProfileOptionRow(icon = Icons.Outlined.LocationOn, label = viewModel.tr("saved_addresses", "Saved Addresses"), value = "Palghar, MH") {
                Toast.makeText(context, "Addresses Managed", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.AccountBalance, label = viewModel.tr("bank_upi", "Bank & UPI Payouts"), value = "Active") {
                Toast.makeText(context, "UPI Verified", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.SupportAgent, label = viewModel.tr("artisan_helpline", "Artisan Guild Helpline"), value = "24x7") {
                Toast.makeText(context, "Helpline: 1800-DHAAGA", Toast.LENGTH_SHORT).show()
            }
            ProfileOptionRow(icon = Icons.Outlined.VerifiedUser, label = viewModel.tr("gi_guarantee", "GI & Fair Trade Guarantee"), value = "100%") {
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
                Text(viewModel.tr("logout", "Log Out of Dhaaga"), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // 22 Indian Languages Selection Sheet
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Choose Language / भाषा चुनें",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                        Text(
                            text = "22 Official Indian Languages Supported",
                            fontSize = 12.sp,
                            color = PaletteSage
                        )
                    }
                    IconButton(onClick = { showLanguageSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PaletteSage)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    items(com.dhaaga.app.utils.AppLanguageManager.SUPPORTED_LANGUAGES) { lang ->
                        val isSelected = currentLang == lang.code
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PaletteGreenTint else Color(0xFFF7F9F6),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) PaletteForest else PaletteSage.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang.code)
                                    showLanguageSheet = false
                                    Toast.makeText(
                                        context,
                                        "Language changed to ${lang.nativeName} (${lang.englishName})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lang.nativeName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PaletteForest else PaletteDarkGreen
                                    )
                                    Text(
                                        text = lang.englishName,
                                        fontSize = 11.sp,
                                        color = DhaagaTextLight
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
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
