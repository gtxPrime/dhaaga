package com.dhaaga.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.ui.theme.*

/**
 * Standard preset durations from 10 minutes (minimum) to 3 months (maximum)
 */
val COUPON_TIME_PRESETS = listOf(
    Pair("10 Mins", 10L),
    Pair("30 Mins", 30L),
    Pair("1 Hour", 60L),
    Pair("1 Day", 1440L),
    Pair("7 Days", 10080L),
    Pair("1 Month", 43200L),
    Pair("3 Months", 129600L) // 90 days max
)

val COUPON_USAGE_PRESETS = listOf(
    Pair("Unlimited", 0),
    Pair("10 Uses", 10),
    Pair("25 Uses", 25),
    Pair("50 Uses", 50),
    Pair("100 Uses", 100)
)

fun generateCouponCode(prefix: String = "CRAFT"): String {
    val randomSuffix = (100..999).random()
    val words = listOf("HERITAGE", "SAVE", "FESTIVE", "HANDMADE", "ARTISAN", "SPECIAL")
    val selectedWord = words.random()
    return "${selectedWord}${randomSuffix}"
}

@Composable
fun ProductDiscountCouponCard(
    basePriceRupees: Long,
    enableSalePrice: Boolean,
    onEnableSalePriceChange: (Boolean) -> Unit,
    salePriceRupees: String,
    onSalePriceChange: (String) -> Unit,
    enableCoupon: Boolean,
    onEnableCouponChange: (Boolean) -> Unit,
    discountType: String, // "percentage" vs "fixed"
    onDiscountTypeChange: (String) -> Unit,
    discountValue: String,
    onDiscountValueChange: (String) -> Unit,
    couponCode: String,
    onCouponCodeChange: (String) -> Unit,
    durationMinutes: Long,
    onDurationMinutesChange: (Long) -> Unit,
    usageLimit: Int,
    onUsageLimitChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DhaagaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PaletteForest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Discounts, Coupons & Special Offers", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                    Text("Direct price reduction, promo codes, timer & usage limits", fontSize = 11.5.sp, color = DhaagaTextMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─────────────────────────────────────────────────────────────
            // 1. DIRECT SALE PRICE OPTION
            // ─────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, PaletteSage.copy(alpha = 0.25f)), RoundedCornerShape(12.dp))
                    .clickable { onEnableSalePriceChange(!enableSalePrice) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sell,
                    contentDescription = null,
                    tint = PaletteForest,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Direct Sale Price", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Show strike-through price to attract buyers", fontSize = 11.sp, color = DhaagaTextLight)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = enableSalePrice,
                    onCheckedChange = onEnableSalePriceChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PaletteForest)
                )
            }

            AnimatedVisibility(visible = enableSalePrice) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    OutlinedTextField(
                        value = salePriceRupees,
                        onValueChange = { onSalePriceChange(it.filter { c -> c.isDigit() }) },
                        label = { Text("Special Sale Price (₹)") },
                        placeholder = { Text(if (basePriceRupees > 100) "${(basePriceRupees * 0.85).toLong()}" else "699") },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PaletteForest,
                            focusedLabelColor = PaletteForest
                        )
                    )

                    val enteredSale = salePriceRupees.toLongOrNull() ?: 0L
                    if (basePriceRupees > 0 && enteredSale > 0 && enteredSale < basePriceRupees) {
                        val savings = basePriceRupees - enteredSale
                        val pct = ((savings.toDouble() / basePriceRupees) * 100).toInt()
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✨ Buyers save ₹$savings ($pct% OFF) with instant sale pricing!",
                            fontSize = 11.5.sp,
                            color = PaletteForest,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (enteredSale >= basePriceRupees && enteredSale > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sale price should be less than regular price (₹$basePriceRupees)",
                            fontSize = 11.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ─────────────────────────────────────────────────────────────
            // 2. PROMO / DISCOUNT COUPON OPTION
            // ─────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, PaletteSage.copy(alpha = 0.25f)), RoundedCornerShape(12.dp))
                    .clickable { onEnableCouponChange(!enableCoupon) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = PaletteForest,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Add Discount Coupon Code", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Create limited-time or limited-use promotional codes", fontSize = 11.sp, color = DhaagaTextLight)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = enableCoupon,
                    onCheckedChange = onEnableCouponChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PaletteForest)
                )
            }

            AnimatedVisibility(visible = enableCoupon) {
                Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // A. Discount Type: Percentage vs Flat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { onDiscountTypeChange("percentage") },
                            shape = RoundedCornerShape(10.dp),
                            color = if (discountType == "percentage") PaletteForest else Color.White,
                            border = BorderStroke(1.dp, if (discountType == "percentage") PaletteForest else PaletteSage.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "% Percentage Off",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (discountType == "percentage") Color.White else PaletteDarkGreen
                                )
                            }
                        }

                        Surface(
                            onClick = { onDiscountTypeChange("fixed") },
                            shape = RoundedCornerShape(10.dp),
                            color = if (discountType == "fixed") PaletteForest else Color.White,
                            border = BorderStroke(1.dp, if (discountType == "fixed") PaletteForest else PaletteSage.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "₹ Direct Flat Less",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (discountType == "fixed") Color.White else PaletteDarkGreen
                                )
                            }
                        }
                    }

                    // B. Discount Value & Coupon Code
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = discountValue,
                            onValueChange = { onDiscountValueChange(it.filter { c -> c.isDigit() }) },
                            label = { Text(if (discountType == "percentage") "Discount %" else "Amount (₹ Less)") },
                            prefix = { Text(if (discountType == "percentage") "% " else "₹ ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaletteForest,
                                focusedLabelColor = PaletteForest
                            )
                        )

                        Column(modifier = Modifier.weight(1.3f)) {
                            OutlinedTextField(
                                value = couponCode,
                                onValueChange = { onCouponCodeChange(it.uppercase().filter { c -> c.isLetterOrDigit() }) },
                                label = { Text("Coupon Code") },
                                placeholder = { Text("e.g. DHAAGA20") },
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PaletteForest,
                                    focusedLabelColor = PaletteForest
                                )
                            )
                        }
                    }

                    // Auto-Generate Button Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (couponCode.isNotBlank()) "Code: $couponCode" else "Tap button to auto-create code",
                            fontSize = 11.5.sp,
                            color = PaletteSage,
                            fontWeight = FontWeight.Medium
                        )

                        TextButton(
                            onClick = { onCouponCodeChange(generateCouponCode()) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Generate", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                        }
                    }

                    // C. Expiry Time (10 mins to 3 months)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coupon Validity Duration (Min 10 Mins • Max 3 Months)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(COUPON_TIME_PRESETS) { (label, mins) ->
                                val isSelected = durationMinutes == mins
                                Surface(
                                    onClick = { onDurationMinutesChange(mins) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PaletteForest else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) PaletteForest else PaletteSage.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else PaletteDarkGreen,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // D. Usage Limit
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coupon Usage Limit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(COUPON_USAGE_PRESETS) { (label, limit) ->
                                val isSelected = usageLimit == limit
                                Surface(
                                    onClick = { onUsageLimitChange(limit) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PaletteForest else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) PaletteForest else PaletteSage.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else PaletteDarkGreen,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
