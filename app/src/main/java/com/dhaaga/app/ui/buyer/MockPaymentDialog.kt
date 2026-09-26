package com.dhaaga.app.ui.buyer

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dhaaga.app.ui.theme.*
import kotlinx.coroutines.delay

enum class PaymentMethodType(val title: String, val subtitle: String) {
    UPI("UPI (GPay / PhonePe / Paytm)", "Instant zero-fee transfer"),
    CARD("Credit / Debit Card", "Visa, Mastercard, RuPay"),
    NETBANKING("Net Banking", "All major Indian banks supported"),
    COD("Cash on Delivery", "Pay in cash or UPI on delivery")
}

@Composable
fun MockPaymentDialog(
    totalAmountPaise: Long,
    onDismiss: () -> Unit,
    onPaymentSuccess: (paymentMethod: String) -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.UPI) }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var upiIdInput by remember { mutableStateOf("artisan.buyer@okaxis") }

    // Card state
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8912") }
    var cardExpiry by remember { mutableStateOf("08/29") }
    var cardCvv by remember { mutableStateOf("842") }
    var cardName by remember { mutableStateOf("Garvit Verma") }

    // Bank state
    var selectedBank by remember { mutableStateOf("State Bank of India") }

    // Processing & Success State
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var processingStage by remember { mutableStateOf("Initiating secure banking link...") }
    val txnRef = remember { "DHG-TXN-${(10000000..99999999).random()}" }

    val formattedTotal = "₹${totalAmountPaise / 100}"

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(500)
            processingStage = "Verifying UPI & Bank credentials..."
            delay(600)
            processingStage = "Authorizing mock payment with RBI gateway..."
            delay(600)
            isProcessing = false
            isSuccess = true
            delay(1200)
            val methodName = when (selectedMethod) {
                PaymentMethodType.UPI -> "UPI ($selectedUpiApp)"
                PaymentMethodType.CARD -> "Card (ending 8912)"
                PaymentMethodType.NETBANKING -> "NetBanking ($selectedBank)"
                PaymentMethodType.COD -> "Cash on Delivery"
            }
            onPaymentSuccess(methodName)
        }
    }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            AnimatedContent(
                targetState = when {
                    isSuccess -> 2
                    isProcessing -> 1
                    else -> 0
                },
                label = "PaymentStateTransition"
            ) { state ->
                when (state) {
                    1 -> {
                        // ── PROCESSING STATE ──────────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .background(PaletteGreenTint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(54.dp),
                                        strokeWidth = 4.dp,
                                        color = PaletteForest
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = PaletteForest,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Processing Mock Payment",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = processingStage,
                                    fontSize = 13.sp,
                                    color = DhaagaTextMedium,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PaletteGreenTint.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "Amount: $formattedTotal",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaletteForest,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // ── SUCCESS STATE ─────────────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape)
                                        .background(DhaagaSuccess),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Payment Verified & Successful!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Paid $formattedTotal to Master Artisan Guild",
                                    fontSize = 13.5.sp,
                                    color = PaletteForest,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Reference ID: $txnRef",
                                    fontSize = 11.5.sp,
                                    color = DhaagaTextLight
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PaletteGreenTint)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Order synchronized live to Cloud Firestore", fontSize = 11.sp, color = PaletteForest, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    else -> {
                        // ── PAYMENT SELECTION & CHECKOUT SHEET ────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            // Top Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PaletteForest.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Dhaaga SafePay", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                                        Text("256-Bit Encrypted Mock Payment Gateway", fontSize = 11.sp, color = DhaagaTextMedium)
                                    }
                                }
                                IconButton(onClick = onDismiss) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DhaagaTextMedium)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Total Due Card
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = PaletteGreenTint),
                                border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("PAYABLE AMOUNT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PaletteSage, letterSpacing = 0.8.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(formattedTotal, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PaletteForest)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .border(1.dp, PaletteForest.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Mock Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PaletteForest)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Scrollable Payment Methods
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("SELECT PAYMENT METHOD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DhaagaTextLight, letterSpacing = 0.6.sp)

                                PaymentMethodType.values().forEach { method ->
                                    val isSelected = selectedMethod == method
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { selectedMethod = method },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) PaletteGreenTint.copy(alpha = 0.45f) else Color(0xFFF9FBF8),
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) PaletteForest else DhaagaDivider.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = when (method) {
                                                            PaymentMethodType.UPI -> Icons.Default.QrCodeScanner
                                                            PaymentMethodType.CARD -> Icons.Default.CreditCard
                                                            PaymentMethodType.NETBANKING -> Icons.Default.AccountBalance
                                                            PaymentMethodType.COD -> Icons.Default.LocalShipping
                                                        },
                                                        contentDescription = null,
                                                        tint = if (isSelected) PaletteForest else DhaagaTextDark,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(method.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                                                        Text(method.subtitle, fontSize = 10.5.sp, color = DhaagaTextMedium)
                                                    }
                                                }
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { selectedMethod = method },
                                                    colors = RadioButtonDefaults.colors(selectedColor = PaletteForest)
                                                )
                                            }

                                            // Expanded Inputs for selected method
                                            if (isSelected) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                when (method) {
                                                    PaymentMethodType.UPI -> {
                                                        // UPI Apps row
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            listOf("Google Pay", "PhonePe", "Paytm", "BHIM").forEach { appName ->
                                                                val isAppSelected = selectedUpiApp == appName
                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .clip(RoundedCornerShape(8.dp))
                                                                        .background(if (isAppSelected) PaletteForest else Color.White)
                                                                        .border(1.dp, if (isAppSelected) PaletteForest else DhaagaDivider, RoundedCornerShape(8.dp))
                                                                        .clickable { selectedUpiApp = appName }
                                                                        .padding(vertical = 6.dp),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text(
                                                                        text = appName,
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = if (isAppSelected) Color.White else DhaagaTextDark
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        OutlinedTextField(
                                                            value = upiIdInput,
                                                            onValueChange = { upiIdInput = it },
                                                            label = { Text("Virtual Payment Address (VPA)", fontSize = 11.sp) },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(10.dp),
                                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                                            singleLine = true,
                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = PaletteForest,
                                                                focusedLabelColor = PaletteForest,
                                                                cursorColor = PaletteForest
                                                            )
                                                        )
                                                    }

                                                    PaymentMethodType.CARD -> {
                                                        OutlinedTextField(
                                                            value = cardNumber,
                                                            onValueChange = { cardNumber = it },
                                                            label = { Text("Card Number", fontSize = 11.sp) },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(10.dp),
                                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                                            singleLine = true
                                                        )
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            OutlinedTextField(
                                                                value = cardExpiry,
                                                                onValueChange = { cardExpiry = it },
                                                                label = { Text("MM/YY", fontSize = 11.sp) },
                                                                modifier = Modifier.weight(1f),
                                                                shape = RoundedCornerShape(10.dp),
                                                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                                                singleLine = true
                                                            )
                                                            OutlinedTextField(
                                                                value = cardCvv,
                                                                onValueChange = { cardCvv = it },
                                                                label = { Text("CVV", fontSize = 11.sp) },
                                                                modifier = Modifier.weight(1f),
                                                                shape = RoundedCornerShape(10.dp),
                                                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                                                singleLine = true
                                                            )
                                                        }
                                                    }

                                                    PaymentMethodType.NETBANKING -> {
                                                        listOf("State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank").forEach { bank ->
                                                            val isBank = selectedBank == bank
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .clickable { selectedBank = bank }
                                                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                RadioButton(selected = isBank, onClick = { selectedBank = bank })
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text(bank, fontSize = 12.sp, color = PaletteDarkGreen, fontWeight = FontWeight.Medium)
                                                            }
                                                        }
                                                    }

                                                    PaymentMethodType.COD -> {
                                                        Text(
                                                            "Pay with Cash or Doorstep UPI upon receipt of your handcrafted artisan goods.",
                                                            fontSize = 11.sp,
                                                            color = DhaagaTextMedium,
                                                            modifier = Modifier.padding(top = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bottom Pay Button
                            Button(
                                onClick = {
                                    isProcessing = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PaletteForest)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pay $formattedTotal (Mock Payment)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
