package com.dhaaga.app.ui.buyer

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dhaaga.app.ui.components.CardAsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.model.OrderModel
import com.dhaaga.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
fun MyOrdersScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val orders by viewModel.buyerOrders.collectAsState()
    var selectedOrder by remember { mutableStateOf<OrderModel?>(null) }

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
        if (orders.isEmpty()) {
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
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = DhaagaPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No orders yet", fontSize = 18.sp, color = DhaagaTextMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your placed orders will appear here", fontSize = 14.sp, color = DhaagaTextLight)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                    ) {
                        Text("Continue Shopping", color = Color.White)
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
                orders.forEach { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOrder = order
                                onOrderClick(order.orderId)
                            },
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
                                    CardAsyncImage(
                                        model = order.productImageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        indicatorSize = 18.dp
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

    selectedOrder?.let { order ->
        OrderDetailSheet(
            order = order,
            viewModel = viewModel,
            onDismiss = { selectedOrder = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailSheet(
    order: OrderModel,
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val formattedDate = remember(order.createdAt) {
        try {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
        } catch (e: Exception) {
            "Recent"
        }
    }

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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark
                    )
                    Text(
                        text = "Order #${order.orderId}",
                        fontSize = 12.sp,
                        color = DhaagaTextLight,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = order.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DhaagaTextMedium)
                    }
                }
            }

            Text(
                text = "Placed on: $formattedDate",
                fontSize = 12.sp,
                color = DhaagaTextLight,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Timeline / Step Tracker
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Shipment Journey",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (order.status == "cancelled") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Order Cancelled", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD32F2F))
                                Text("Stock automatically restored to the artisan workshop.", fontSize = 11.sp, color = DhaagaTextDark)
                            }
                        }
                    } else {
                        val steps = listOf(
                            Triple("Order Placed", "Payment verified & order notified", true),
                            Triple("Confirmed", "Artisan preparing your handcrafted item", order.status in listOf("confirmed", "packed", "shipped", "delivered")),
                            Triple("Shipped", if (order.trackingId.isNotBlank()) "Via ${order.shippingCarrier} • ${order.trackingId}" else "Handed to courier partner", order.status in listOf("shipped", "delivered")),
                            Triple("Delivered", "Delivered directly to your door", order.status == "delivered")
                        )

                        steps.forEachIndexed { index, (title, desc, isDone) ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(if (isDone) DhaagaSuccess else DhaagaTextLight.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isDone) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        } else {
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                                        }
                                    }
                                    if (index < steps.lastIndex) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(30.dp)
                                                .background(if (isDone) DhaagaSuccess else DhaagaTextLight.copy(alpha = 0.3f))
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.padding(bottom = if (index < steps.lastIndex) 16.dp else 0.dp)) {
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isDone) DhaagaTextDark else DhaagaTextLight
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = DhaagaTextLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Item Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Item Purchased",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            CardAsyncImage(
                                model = order.productImageUrl,
                                contentDescription = order.productTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                indicatorSize = 18.dp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = order.productTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DhaagaTextDark,
                                maxLines = 2
                            )
                            Text(
                                text = "Artisan: ${order.sellerName}",
                                fontSize = 12.sp,
                                color = DhaagaTextLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Qty: ${order.quantity} × ₹${order.unitPrice / 100}",
                                fontSize = 12.sp,
                                color = DhaagaTextMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery & Courier Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Delivery Address",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DhaagaTextDark
                        )
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val recipientName = order.deliveryAddress.name.ifBlank { order.buyerName.ifBlank { "Buyer" } }
                    Text(recipientName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DhaagaTextDark)
                    if (order.deliveryAddress.phone.isNotBlank()) {
                        Text("Phone: ${order.deliveryAddress.phone}", fontSize = 12.sp, color = DhaagaTextLight)
                    }
                    val fullAddress = listOf(
                        order.deliveryAddress.line1,
                        order.deliveryAddress.line2,
                        order.deliveryAddress.city,
                        order.deliveryAddress.state,
                        order.deliveryAddress.pincode
                    ).filter { it.isNotBlank() }.joinToString(", ")
                    Text(
                        text = fullAddress.ifBlank { "Verified Primary Delivery Address" },
                        fontSize = 12.sp,
                        color = DhaagaTextMedium,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    if (order.trackingId.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DhaagaTextLight.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DhaagaAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(order.shippingCarrier, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DhaagaTextDark)
                                Text("Tracking AWB: ${order.trackingId}", fontSize = 11.sp, color = DhaagaAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Item Total", fontSize = 12.sp, color = DhaagaTextMedium)
                        Text("₹${(order.unitPrice * order.quantity) / 100}", fontSize = 12.sp, color = DhaagaTextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Fee", fontSize = 12.sp, color = DhaagaTextMedium)
                        Text("FREE", fontSize = 12.sp, color = DhaagaSuccess, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment Method", fontSize = 12.sp, color = DhaagaTextMedium)
                        Text("Prepaid (Mock UPI)", fontSize = 12.sp, color = DhaagaTextDark, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DhaagaTextLight.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Paid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                        Text(order.totalDisplay, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = DhaagaPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            if (order.status != "cancelled" && order.status != "delivered") {
                OutlinedButton(
                    onClick = {
                        viewModel.cancelOrder(order.orderId)
                        Toast.makeText(context, "Order #${order.orderId} cancelled & stock restored to artisan!", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Order (Auto-Restock)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = {
                    Toast.makeText(context, "Invoice downloaded for Order #${order.orderId}", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Tax Invoice (Demo)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(28.dp))
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
