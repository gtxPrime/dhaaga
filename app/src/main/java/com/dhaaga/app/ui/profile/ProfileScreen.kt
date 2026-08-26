package com.dhaaga.app.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()

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
                    Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile header card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                DhaagaPrimary.copy(alpha = 0.05f),
                                DhaagaBackground
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Notion-style dynamic avatar
                    com.dhaaga.app.ui.components.NotionAvatar(
                        name = user?.name ?: "User",
                        size = 88.dp,
                        borderWidth = 3.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user?.name ?: "User",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark
                    )

                    Text(
                        text = user?.phoneNumber ?: "",
                        fontSize = 14.sp,
                        color = DhaagaTextMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DhaagaPrimary.copy(alpha = 0.1f))
                            .border(1.dp, DhaagaPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user?.isSeller == true) Icons.Default.Storefront else Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = DhaagaPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (user?.isSeller == true) "Artisan Seller" else "Craft Buyer",
                                fontSize = 13.sp,
                                color = DhaagaPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Seller stats
                    if (user?.isSeller == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("82", "Shilpi\nScore")
                            StatItem("6", "Products")
                            StatItem("₹1.85L", "Earnings")
                            StatItem("4.8 / 5", "Rating")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Menu items
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (user?.isSeller == true) {
                    ProfileMenuSection(
                        title = "Seller",
                        items = listOf(
                            Triple(Icons.Default.Storefront, "My Storefront", "View your public shop"),
                            Triple(Icons.Default.VerifiedUser, "Aadhaar Verification", if (user?.isAadhaarVerified == true) "Verified" else "Not verified"),
                            Triple(Icons.Default.AccountBalance, "Bank Account", "Payout settings"),
                            Triple(Icons.Default.QrCode, "UPI / QR Code", "Payment methods")
                        )
                    )
                } else {
                    ProfileMenuSection(
                        title = "Buyer",
                        items = listOf(
                            Triple(Icons.Default.LocationOn, "Delivery Addresses", "Manage addresses"),
                            Triple(Icons.Default.Payment, "Payment Methods", "UPI, Cards, Wallets"),
                            Triple(Icons.Default.ReceiptLong, "Order History", "Past orders")
                        )
                    )
                }

                ProfileMenuSection(
                    title = "App Settings",
                    items = listOf(
                        Triple(Icons.Default.Language, "Language", user?.languagePref?.uppercase() ?: "EN"),
                        Triple(Icons.Default.Notifications, "Notifications", "Manage alerts"),
                        Triple(Icons.Default.Help, "Help & Support", "FAQs, contact us"),
                        Triple(Icons.Default.Policy, "Privacy Policy", "Data usage"),
                        Triple(Icons.Default.Info, "About Dhaaga", "SIH 2026 Project")
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Logout
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DhaagaError)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = DhaagaError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = DhaagaError, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Dhaaga v0.1.0 • Smart India Hackathon 2026",
                    fontSize = 11.sp,
                    color = DhaagaTextLight,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
        Text(label, fontSize = 11.sp, color = DhaagaTextMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 15.sp)
    }
}

@Composable
private fun ProfileMenuSection(
    title: String,
    items: List<Triple<ImageVector, String, String>>
) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = DhaagaTextLight,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DhaagaSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        items.forEachIndexed { i, (icon, label, subtitle) ->
            if (i > 0) Divider(color = DhaagaDivider.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DhaagaTextDark)
                    if (subtitle.isNotBlank()) Text(subtitle, fontSize = 12.sp, color = DhaagaTextLight)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DhaagaTextLight.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
