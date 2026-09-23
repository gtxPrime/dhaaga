package com.dhaaga.app.ui.seller

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.ui.theme.*
import com.dhaaga.app.utils.AppLanguageManager

@Composable
fun SellerDashboardScreen(
    onBack: () -> Unit,
    viewModel: AppViewModel? = null
) {
    val currentLang by viewModel?.selectedLanguage?.collectAsState() ?: remember { mutableStateOf("en") }
    fun tr(key: String, fallback: String): String =
        viewModel?.tr(key, fallback) ?: AppLanguageManager.translate(key, currentLang, fallback)

    val user by viewModel?.currentUser?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val orders by viewModel?.artisanOrders?.collectAsState()
        ?: remember { mutableStateOf(emptyList()) }
    val sellerProducts by viewModel?.sellerProducts?.collectAsState()
        ?: remember { mutableStateOf(emptyList()) }

    // Computed real earnings from Firestore orders
    val totalEarningsPaise = orders.filter { it.status != "cancelled" }.sumOf { it.sellerPayout }
    val totalEarningsRupees = totalEarningsPaise / 100
    val confirmedOrders = orders.filter { it.status == "confirmed" || it.status == "packed" }
    val recentOrders = orders.take(3)

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
                        Text(
                            tr("artisan_dashboard", "Artisan Dashboard"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (user != null) {
                            Text(
                                "${user!!.name} • ${user!!.village.ifBlank { user!!.state }}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Earnings Summary ────────────────────────────────────────────────
            Text(tr("earnings_title", "Earnings"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EarningsCard(
                    period = tr("total_orders", "Total Orders"),
                    amount = "${orders.size}",
                    change = "${confirmedOrders.size} ${tr("ready_to_ship", "ready to ship")}",
                    color = DhaagaPrimary,
                    modifier = Modifier.weight(1f)
                )
                EarningsCard(
                    period = tr("total_earnings", "Total Earnings"),
                    amount = "₹$totalEarningsRupees",
                    change = "${sellerProducts.size} ${tr("products_live", "products live")}",
                    color = DhaagaAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Shilpi Score ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DhaagaCardBg)
                    .border(1.dp, DhaagaDivider.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = DhaagaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(tr("shilpi_score", "Shilpi Score"), fontSize = 14.sp, color = DhaagaTextMedium)
                            Text("82 / 100", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = DhaagaSuccess, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(tr("top_seller", "Top Seller"), fontSize = 12.sp, color = DhaagaSuccess, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 0.82f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = DhaagaPrimary,
                        trackColor = DhaagaDivider.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            tr("shilpi_tip", "Tip: Add more product photos to boost score by 5 points"),
                            fontSize = 12.sp,
                            color = DhaagaTextMedium
                        )
                    }
                }
            }

            // ── Recent Orders (real data) ───────────────────────────────────────
            Text(tr("recent_orders", "Recent Orders"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)

            if (recentOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DhaagaSurface)
                        .border(1.dp, DhaagaDivider.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = DhaagaTextLight,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            tr("no_orders_yet", "No orders yet — share your crafts to attract buyers!"),
                            fontSize = 13.sp,
                            color = DhaagaTextMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                recentOrders.forEach { order ->
                    val statusColor = when (order.status) {
                        "confirmed" -> DhaagaAccent
                        "packed"    -> DhaagaAccent
                        "shipped"   -> DhaagaPrimary
                        "delivered" -> DhaagaSuccess
                        "cancelled" -> DhaagaError
                        else        -> DhaagaTextMedium
                    }
                    val statusLabel = when (order.status) {
                        "confirmed" -> tr("status_confirmed", "Confirmed")
                        "packed"    -> tr("status_packed", "Packed")
                        "shipped"   -> tr("status_shipped", "Shipped")
                        "delivered" -> tr("status_delivered", "Delivered")
                        "cancelled" -> tr("status_cancelled", "Cancelled")
                        else        -> order.status.replaceFirstChar { it.uppercase() }
                    }
                    OrderRow(
                        orderId = order.orderId,
                        item = "${order.productTitle} x${order.quantity}",
                        status = "₹${order.totalAmount / 100} • $statusLabel",
                        statusColor = statusColor
                    )
                }
            }

            // ── Price Alerts ────────────────────────────────────────────────────
            Text(tr("price_alerts", "Price Alerts"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DhaagaError.copy(alpha = 0.07f))
                    .border(1.dp, DhaagaError.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = DhaagaError, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(tr("price_alert_tip", "AI Tip: Review your prices to stay competitive"), fontSize = 13.sp, color = DhaagaError, fontWeight = FontWeight.SemiBold)
                        Text(tr("price_alert_sub", "Use the Pricing Tool when adding products to get the best price"), fontSize = 12.sp, color = DhaagaTextMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(tr("open_pricing_tool", "Open Pricing Tool →"), color = DhaagaPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EarningsCard(
    period: String,
    amount: String,
    change: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(period, fontSize = 12.sp, color = DhaagaTextMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(change, fontSize = 11.sp, color = DhaagaTextMedium)
        }
    }
}

@Composable
private fun OrderRow(orderId: String, item: String, status: String, statusColor: Color = DhaagaAccent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DhaagaSurface)
            .border(1.dp, DhaagaDivider.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("#$orderId", fontSize = 11.sp, color = DhaagaTextLight)
                Text(item, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DhaagaTextDark)
                Text(status, fontSize = 12.sp, color = statusColor)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DhaagaTextLight)
        }
    }
}
