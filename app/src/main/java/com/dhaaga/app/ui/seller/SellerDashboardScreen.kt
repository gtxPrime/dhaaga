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
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.ui.theme.*

@Composable
fun SellerDashboardScreen(
    onBack: () -> Unit,
    viewModel: com.dhaaga.app.AppViewModel? = null
) {
    val user = MockData.mockSeller

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
                    Text(viewModel?.tr("artisan_dashboard", "Artisan Dashboard") ?: "Artisan Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            // Earnings cards
            Text("Earnings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EarningsCard("Today", "₹2,400", "+12%", DhaagaPrimary, modifier = Modifier.weight(1f))
                EarningsCard("This Week", "₹14,800", "+8%", DhaagaAccent, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EarningsCard("This Month", "₹58,200", "+22%", DhaagaPrimaryLight, modifier = Modifier.weight(1f))
                EarningsCard("All Time", "₹1,85,000", "Top 5%", DhaagaSuccess, modifier = Modifier.weight(1f))
            }

            // Shilpi Score
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
                            Text("Shilpi Score", fontSize = 14.sp, color = DhaagaTextMedium)
                            Text("82 / 100", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = DhaagaSuccess, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Top Seller", fontSize = 12.sp, color = DhaagaSuccess, fontWeight = FontWeight.SemiBold)
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
                            "Tip: Add 2 more product photos to boost score by 5 points",
                            fontSize = 12.sp,
                            color = DhaagaTextMedium
                        )
                    }
                }
            }

            // Orders summary
            Text("Recent Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)

            listOf(
                Triple("SIH2026ABC", "Warli Painting x1", "₹941 • Confirmed"),
                Triple("SIH2026XYZ", "Madhubani x2", "₹2,400 • Packed"),
                Triple("SIH2026DEF", "Bandhani Dupatta x1", "₹3,499 • Delivered")
            ).forEach { (id, item, status) ->
                OrderRow(orderId = id, item = item, status = status)
            }

            // Price Alerts
            Text("Price Alerts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
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
                        Text("Warli Painting is priced 18% below market", fontSize = 13.sp, color = DhaagaError, fontWeight = FontWeight.SemiBold)
                        Text("Market price: ₹1,020 • Your price: ₹850", fontSize = 12.sp, color = DhaagaTextMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Update Price →", color = DhaagaPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
private fun OrderRow(orderId: String, item: String, status: String) {
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
                Text(status, fontSize = 12.sp, color = DhaagaAccent)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DhaagaTextLight)
        }
    }
}
