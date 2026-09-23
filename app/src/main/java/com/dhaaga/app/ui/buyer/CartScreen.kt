package com.dhaaga.app.ui.buyer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
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
import com.dhaaga.app.ui.components.CardAsyncImage
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager

@Composable
fun CartScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    val currentLang by viewModel.selectedLanguage.collectAsState()
    fun tr(key: String, fallback: String): String = viewModel.tr(key, fallback)

    val cart by viewModel.cart.collectAsState()
    val total = cart.sumOf { it.totalPrice }
    val platformFee = (total * 0.08).toLong()
    val shipping = if (total > 100000L) 0L else 5000L
    val grandTotal = total + platformFee + shipping

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
                        Text(tr("my_cart_title", "My Cart"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${cart.size} ${tr("items_count_suffix", "items")}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(shadowElevation = 8.dp, color = DhaagaSurface) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tr("subtotal", "Subtotal"), fontSize = 14.sp, color = DhaagaTextMedium)
                            Text("₹${total / 100}", fontSize = 14.sp, color = DhaagaTextDark)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tr("platform_fee_label", "Platform fee (8%)"), fontSize = 14.sp, color = DhaagaTextMedium)
                            Text("₹${platformFee / 100}", fontSize = 14.sp, color = DhaagaTextDark)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tr("shipping_label", "Shipping"), fontSize = 14.sp, color = DhaagaTextMedium)
                            Text(if (shipping == 0L) tr("shipping_free_badge", "FREE") else "₹${shipping / 100}", fontSize = 14.sp, color = if (shipping == 0L) DhaagaSuccess else DhaagaTextDark)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = DhaagaDivider.copy(alpha = 0.3f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tr("total_label", "Total"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            Text("₹${grandTotal / 100}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onCheckout,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${tr("proceed_to_checkout", "Proceed to Checkout")} • ₹${grandTotal / 100}", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cart.isEmpty()) {
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
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = DhaagaPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(tr("your_cart_empty", "Your cart is empty"), fontSize = 18.sp, color = DhaagaTextMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tr("discover_handmade_crafts", "Discover authentic handmade crafts"), fontSize = 14.sp, color = DhaagaTextLight)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                    ) {
                        Text(tr("explore_products_btn", "Explore Products"), color = Color.White)
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
                // Free shipping notice
                if (total < 100000L) {
                    val remaining = (100000L - total) / 100
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DhaagaAccent.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DhaagaAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Add ₹$remaining ${tr("add_more_for_free_shipping", "more for FREE shipping!")}",
                                fontSize = 13.sp,
                                color = DhaagaAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DhaagaSuccess.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DhaagaSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                tr("unlocked_free_shipping", "You've unlocked FREE shipping!"),
                                fontSize = 13.sp,
                                color = DhaagaSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                cart.forEach { item ->
                    CartItemCard(
                        item = item,
                        currentLang = currentLang,
                        onRemove = { viewModel.removeFromCart(item.productId) },
                        onDecrement = { viewModel.updateCartQuantity(item.productId, item.quantity - 1) },
                        onIncrement = { viewModel.updateCartQuantity(item.productId, item.quantity + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: com.dhaaga.app.data.model.CartItemModel,
    currentLang: String,
    onRemove: () -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    fun tr(key: String, fallback: String): String = AppLanguageManager.translate(key, currentLang, fallback)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DhaagaCardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                CardAsyncImage(
                    model = item.productImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    indicatorSize = 20.dp
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
                        Text(item.productTitle, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DhaagaTextDark)
                        Text(item.sellerName, fontSize = 11.sp, color = DhaagaTextLight)
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = tr("remove_item_cd", "Remove"), tint = DhaagaTextLight, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.totalDisplay, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)

                    // Quantity stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DhaagaSurface)
                            .border(1.dp, DhaagaDivider.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = tr("decrease_qty_cd", "Decrease"), tint = DhaagaTextDark, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            "${item.quantity}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DhaagaTextDark,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, contentDescription = tr("increase_qty_cd", "Increase"), tint = DhaagaTextDark, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
