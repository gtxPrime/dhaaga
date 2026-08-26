package com.dhaaga.app.ui.buyer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.ui.theme.*

@Composable
fun WishlistScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val wishlist by viewModel.wishlist.collectAsState()
    val products by viewModel.products.collectAsState()
    val wishlisted = products.filter { wishlist.contains(it.productId) }

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
                        Text("Wishlist", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${wishlisted.size} items saved", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    ) { padding ->
        if (wishlisted.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(DhaagaPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = DhaagaPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your wishlist is empty", fontSize = 18.sp, color = DhaagaTextMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Save items you love by tapping the heart icon", fontSize = 14.sp, color = DhaagaTextLight)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                    ) {
                        Text("Explore Products", color = Color.White)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                wishlisted.forEach { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick(product.productId) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = product.primaryImageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (product.hasGITag) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DhaagaGIBadge)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) { Text("GI", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold) }
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.titleEn, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DhaagaTextDark, maxLines = 2)
                                Text(product.sellerName, fontSize = 11.sp, color = DhaagaTextLight)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(product.priceDisplay, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.addToCart(product) },
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                                    ) { Text("Add to Cart", fontSize = 12.sp, color = Color.White) }
                                    IconButton(
                                        onClick = { viewModel.toggleWishlist(product.productId) },
                                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(DhaagaError.copy(alpha = 0.1f))
                                    ) {
                                        Icon(Icons.Default.Favorite, contentDescription = "Remove", tint = DhaagaError, modifier = Modifier.size(16.dp))
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
fun MyOrdersScreen(onBack: () -> Unit, onOrderClick: (String) -> Unit) {
    val orders = MockData.mockOrders

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
                    Text("My Orders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            orders.forEach { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOrderClick(order.orderId) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("#${order.orderId}", fontSize = 12.sp, color = DhaagaTextLight, fontWeight = FontWeight.Medium)
                            StatusBadge(status = order.status)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Box(
                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = order.productImageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.productTitle, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DhaagaTextDark, maxLines = 2)
                                Text("by ${order.sellerName}", fontSize = 11.sp, color = DhaagaTextLight)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(order.totalDisplay, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // Tracking row
                        if (order.trackingId.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DhaagaAccent.copy(alpha = 0.08f))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DhaagaAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(order.shippingCarrier, fontSize = 11.sp, color = DhaagaTextMedium)
                                        Text("Tracking: ${order.trackingId}", fontSize = 11.sp, color = DhaagaAccent, fontWeight = FontWeight.SemiBold)
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
private fun StatusBadge(status: String) {
    val (color, label) = when (status) {
        "pending"   -> DhaagaWarning to "Pending"
        "confirmed" -> DhaagaAccent to "Confirmed"
        "packed"    -> DhaagaAccent to "Packed"
        "shipped"   -> DhaagaPrimary to "Shipped"
        "delivered" -> DhaagaSuccess to "Delivered"
        "cancelled" -> DhaagaError to "Cancelled"
        else -> DhaagaTextMedium to status
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}
