package com.dhaaga.app.ui.seller

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.dhaaga.app.data.repository.PricingAnalysisResult
import com.dhaaga.app.ui.theme.*

@Composable
fun DynamicPricingCard(
    pricingResult: PricingAnalysisResult?,
    isLoading: Boolean,
    onCalculateRequested: () -> Unit,
    onApplyPrice: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DhaagaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DhaagaAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Dynamic Pricing Assistant", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                        Text("AI Market Intelligence & Cost Analysis", fontSize = 11.sp, color = DhaagaTextMedium)
                    }
                }

                if (pricingResult != null) {
                    IconButton(
                        onClick = onCalculateRequested,
                        enabled = !isLoading,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = DhaagaPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (pricingResult == null) {
                // Initial prompt state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DhaagaAccent.copy(alpha = 0.06f))
                        .border(1.dp, DhaagaAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Get an AI-recommended selling price based on raw material costs, artisan labor hours, and competitor pricing on Amazon, Flipkart, & GeM.",
                            fontSize = 12.sp,
                            color = DhaagaTextMedium,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onCalculateRequested,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DhaagaAccent)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing Market Trends...", fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyze & Suggest Price", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            } else {
                // Pricing Analysis Result View
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Suggested Price Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(DhaagaPrimary, DhaagaPrimaryLight)))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Recommended Price", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            Text("₹${pricingResult.recommendedPrice}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("Floor (MOQ): ₹${pricingResult.floorPrice}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                        }

                        Button(
                            onClick = { onApplyPrice(pricingResult.recommendedPrice) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Apply ₹${pricingResult.recommendedPrice}", color = DhaagaPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Cost Structure Breakdown
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DhaagaCardBg)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Estimated Cost Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            Spacer(modifier = Modifier.height(8.dp))

                            CostRow(label = "Raw Materials", amount = pricingResult.costMaterials, color = Color(0xFFE57373))
                            CostRow(label = "Artisan Labor (Fair Wage)", amount = pricingResult.costLabor, color = Color(0xFF81C784))
                            CostRow(label = "Logistics & Platform", amount = pricingResult.costPlatform, color = Color(0xFFFFB74D))

                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = DhaagaDivider.copy(alpha = 0.4f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Artisan Net Profit Margin", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DhaagaSuccess)
                                Text("${pricingResult.marginPercent}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhaagaSuccess)
                            }
                        }
                    }

                    // Market Competitor Benchmarks
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DhaagaCardBg)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = DhaagaAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Marketplace Intelligence", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MarketplaceBadge("Amazon", "₹${pricingResult.amazonAvg}")
                                MarketplaceBadge("Flipkart", "₹${pricingResult.flipkartAvg}")
                                MarketplaceBadge("Meesho", "₹${pricingResult.meeshoAvg}")
                                MarketplaceBadge("GeM", "₹${pricingResult.gemAvg}")
                            }
                        }
                    }

                    // Gemini Pricing Insight
                    if (pricingResult.pricingInsight.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DhaagaAccent.copy(alpha = 0.08f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = DhaagaAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(pricingResult.pricingInsight, fontSize = 11.sp, color = DhaagaTextDark, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CostRow(label: String, amount: Long, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, color = DhaagaTextMedium)
        }
        Text("₹$amount", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DhaagaTextDark)
    }
}

@Composable
private fun MarketplaceBadge(name: String, price: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, DhaagaDivider.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(name, fontSize = 10.sp, color = DhaagaTextLight, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(price, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
    }
}
