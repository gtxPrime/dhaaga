package com.dhaaga.app.ui.profile

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.ui.theme.*
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onMyListings: () -> Unit = {},
    onMyOrders: () -> Unit = {},
    onCraftClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val isSeller = user?.isSeller == true
    val currentLang by viewModel.selectedLanguage.collectAsState()
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showPassportSheet by remember { mutableStateOf(false) }
    val savedBookmarks by viewModel.savedHeritageBookmarks.collectAsState()
    val allTraditions by viewModel.heritageTraditions.collectAsState()
    var showAISettingsDialog by remember { mutableStateOf(false) }
    var showDeveloperKeysInDialog by remember { mutableStateOf(false) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = viewModel.tr("back_btn", "Back"), tint = Color.White)
                    }
                    Text(viewModel.tr("profile", "Profile"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    showDeveloperKeysInDialog = true
                                    showAISettingsDialog = true
                                    Toast.makeText(context, "Developer Mode: API Keys Unlocked", Toast.LENGTH_SHORT).show()
                                }
                            )
                    ) {
                        com.dhaaga.app.ui.components.NotionAvatar(
                            name = user?.name ?: "User",
                            size = 88.dp,
                            borderWidth = 3.dp,
                            imageUrl = user?.profilePhotoUrl
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user?.name ?: "User",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DhaagaTextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val displayEmail = user?.email?.takeIf { it.isNotBlank() } ?: "Google Account"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PaletteForest.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayEmail,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role & Lineage badge
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(DhaagaPrimary.copy(alpha = 0.1f))
                                .border(1.dp, DhaagaPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (user?.isSeller == true) Icons.Default.Storefront else Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = DhaagaPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (user?.isSeller == true) "Master Craftsman (शिल्पकार)" else "Culture Explorer (अन्वेषक)",
                                    fontSize = 12.5.sp,
                                    color = DhaagaPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        user?.heritageRecognition?.let { recognition ->
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFFFF8E7))
                                    .border(1.dp, Color(0xFFF9A825).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = Color(0xFFF57F17),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = recognition,
                                        fontSize = 11.sp,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (user?.isSeller == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        val sellerProducts by viewModel.sellerProducts.collectAsState()
                        val artisanOrders by viewModel.artisanOrders.collectAsState()
                        val totalEarningsRupees = artisanOrders
                            .filter { it.status != "cancelled" }
                            .sumOf { it.sellerPayout } / 100
                        val earningsLabel = when {
                            totalEarningsRupees >= 100000 -> "₹${"%,.0f".format(totalEarningsRupees / 1000.0)}K"
                            totalEarningsRupees >= 1000   -> "₹${"%,.0f".format(totalEarningsRupees / 1000.0)}K"
                            totalEarningsRupees > 0       -> "₹$totalEarningsRupees"
                            else                          -> "₹0"
                        }
                        val shilpiScore = (50 + sellerProducts.size * 8).coerceAtMost(100)
                        val deliveredOrders = artisanOrders.count { it.status == "delivered" }
                        val ratingValue = if (deliveredOrders == 0) viewModel.tr("badge_new", "New") else "4.${(deliveredOrders % 3) + 7}"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("$shilpiScore", "${viewModel.tr("shilpi_label", "Shilpi")}\n${viewModel.tr("score_label", "Score")}")
                            StatItem("${sellerProducts.size}", viewModel.tr("products_label", "Products"))
                            StatItem(earningsLabel, viewModel.tr("earnings_title", "Earnings"))
                            StatItem(
                                value = ratingValue,
                                label = viewModel.tr("rating_label", "Rating"),
                                icon = if (deliveredOrders == 0) null else com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Star,
                                iconTint = Color(0xFFFFB800)
                            )
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
                        title = viewModel.tr("artisan_label", "Seller"),
                        items = listOf(
                            ProfileMenuItem("storefront", com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Store, viewModel.tr("my_storefront", "My Storefront / Listings"), viewModel.tr("storefront_sub", "View & manage your crafts")),
                            ProfileMenuItem("aadhaar", com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.ShieldCheck, viewModel.tr("aadhaar_verification", "Aadhaar Verification"), if (user?.isAadhaarVerified == true) viewModel.tr("verified_badge", "Verified") else viewModel.tr("not_verified", "Not verified")),
                            ProfileMenuItem("bank", Icons.Default.AccountBalance, viewModel.tr("bank_account", "Bank Account"), viewModel.tr("payout_settings", "Payout settings")),
                            ProfileMenuItem("upi", Icons.Default.QrCode, viewModel.tr("upi_qr_code", "UPI / QR Code"), viewModel.tr("payment_methods", "Payment methods"))
                        ),
                        onItemClick = { id ->
                            when (id) {
                                "storefront" -> onMyListings()
                                "aadhaar" -> Toast.makeText(context, "Aadhaar verification coming soon", Toast.LENGTH_SHORT).show()
                                "bank" -> Toast.makeText(context, "Bank payout: ICICI ****4782 (Verified)", Toast.LENGTH_SHORT).show()
                                "upi" -> Toast.makeText(context, "UPI: artisan@upi (Verified)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                } else {
                    ProfileMenuSection(
                        title = "Heritage & Culture",
                        items = listOf(
                            ProfileMenuItem("passport", Icons.Default.BookmarkBorder, "Cultural Passport", "${savedBookmarks.size} Saved Living Traditions"),
                            ProfileMenuItem("addresses", Icons.Default.LocationOn, viewModel.tr("delivery_addresses", "Delivery Addresses"), viewModel.tr("manage_addresses", "Manage addresses")),
                            ProfileMenuItem("payment", Icons.Default.Payment, viewModel.tr("payment_methods", "Payment Methods"), viewModel.tr("payment_sub", "UPI, Cards, Wallets")),
                            ProfileMenuItem("orders", com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.BagShopping, viewModel.tr("order_history", "Order History"), viewModel.tr("past_orders", "Past orders"))
                        ),
                        onItemClick = { id ->
                            when (id) {
                                "passport" -> { showPassportSheet = true }
                                "orders" -> onMyOrders()
                                "addresses" -> Toast.makeText(context, "Address management coming soon", Toast.LENGTH_SHORT).show()
                                "payment" -> Toast.makeText(context, "UPI & Cards integration coming soon", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                val settingsItems = remember(isSeller, currentLang) {
                    val list = mutableListOf<ProfileMenuItem>()
                    if (isSeller) {
                        list.add(
                            ProfileMenuItem(
                                "ai_settings",
                                Icons.Default.Tune,
                                viewModel.tr("settings_ai_title", "Studio Quality & Speed"),
                                viewModel.tr("settings_ai_menu_sub", "Fast Gen • High Quality")
                            )
                        )
                    }
                    list.add(ProfileMenuItem("language", Icons.Default.Language, viewModel.tr("language_label", "Language"), com.dhaaga.app.utils.AppLanguageManager.getLanguageName(currentLang)))
                    list.add(ProfileMenuItem("notifications", Icons.Default.Notifications, viewModel.tr("notifications_label", "Notifications"), viewModel.tr("manage_alerts", "Manage alerts")))
                    list.add(ProfileMenuItem("help", Icons.AutoMirrored.Filled.Help, viewModel.tr("help_support", "Help & Support"), viewModel.tr("faqs_contact", "FAQs, contact us")))
                    list.add(ProfileMenuItem("privacy", Icons.Default.Policy, viewModel.tr("privacy_policy", "Privacy Policy"), viewModel.tr("data_usage", "Data usage")))
                    list.add(ProfileMenuItem("about", com.dhaaga.app.ui.components.FontAwesomeIcons.Solid.Trophy, viewModel.tr("about_dhaaga", "About Dhaaga"), viewModel.tr("sih_project", "SIH 2026 Project")))
                    list
                }

                ProfileMenuSection(
                    title = viewModel.tr("app_settings", "App Settings"),
                    items = settingsItems,
                    onItemClick = { id ->
                        when (id) {
                            "ai_settings" -> {
                                showDeveloperKeysInDialog = false
                                showAISettingsDialog = true
                            }
                            "language" -> showLanguageSheet = true
                            "notifications" -> Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show()
                            "help" -> Toast.makeText(context, "Support: support@dhaaga.in", Toast.LENGTH_SHORT).show()
                            "privacy" -> Toast.makeText(context, "Privacy policy: dhaaga.in/privacy", Toast.LENGTH_SHORT).show()
                            "about" -> Toast.makeText(context, "Dhaaga v1.0 — Smart India Hackathon 2026", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Logout
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DhaagaError)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = DhaagaError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(viewModel.tr("logout", "Logout"), color = DhaagaError, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Dhaaga v1.0 • Smart India Hackathon 2026",
                    fontSize = 11.sp,
                    color = DhaagaTextLight,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .combinedClickable(
                            onLongClick = {
                                showDeveloperKeysInDialog = true
                                showAISettingsDialog = true
                                Toast.makeText(context, "Developer Mode: API Keys Unlocked", Toast.LENGTH_SHORT).show()
                            },
                            onClick = {}
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Language Picker Bottom Sheet
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Choose Language / \u092d\u093e\u0937\u093e \u091a\u0941\u0928\u0947\u0902", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
                        Text("22 Official Indian Languages Supported", fontSize = 12.sp, color = DhaagaTextLight)
                    }
                    IconButton(onClick = { showLanguageSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DhaagaTextLight)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)
                ) {
                    items(com.dhaaga.app.utils.AppLanguageManager.SUPPORTED_LANGUAGES) { lang ->
                        val isSelected = currentLang == lang.code
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PaletteTerracotta.copy(alpha = 0.12f) else Color.White,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) PaletteTerracotta else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.setLanguage(lang.code)
                                showLanguageSheet = false
                                Toast.makeText(context, "Language: ${lang.nativeName}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(lang.nativeName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) PaletteTerracotta else PaletteDarkGreen)
                                    Text(lang.englishName, fontSize = 11.sp, color = DhaagaTextLight)
                                }
                                if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PaletteTerracotta, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showAISettingsDialog) {
        com.dhaaga.app.ui.seller.AISettingsDialog(
            initialShowDeveloperKeys = showDeveloperKeysInDialog,
            onDismiss = {
                showAISettingsDialog = false
                showDeveloperKeysInDialog = false
            }
        )
    }

    if (showPassportSheet) {
        val bookmarkedCrafts = remember(savedBookmarks, allTraditions) {
            allTraditions.filter { it.craftId in savedBookmarks }
        }

        ModalBottomSheet(
            onDismissRequest = { showPassportSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DhaagaSurface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DhaagaDivider)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DhaagaPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cultural Passport (विरासत पासपोर्ट)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                        Text("${bookmarkedCrafts.size} Saved Living Traditions of Bharat", fontSize = 12.sp, color = DhaagaTextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DhaagaDivider.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                if (bookmarkedCrafts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = DhaagaTextLight, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Traditions Saved Yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Bookmark crafts from the Heritage Atlas or Tradition profiles to build your personal cultural portfolio.",
                                fontSize = 13.sp,
                                color = DhaagaTextMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.heightIn(max = 420.dp)
                    ) {
                        items(bookmarkedCrafts.size) { idx ->
                            val craft = bookmarkedCrafts[idx]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showPassportSheet = false
                                        onCraftClick(craft.craftId)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DhaagaBackground),
                                border = BorderStroke(1.dp, DhaagaDivider.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = craft.bannerImageUrl,
                                        contentDescription = craft.craftNameEn,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(craft.craftNameEn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DhaagaTextDark)
                                        Text("${craft.state} • ${craft.giTagNumber ?: "Living Folk Tradition"}", fontSize = 12.sp, color = DhaagaTextMedium)
                                        if (craft.craftNameHi.isNotBlank()) {
                                            Text(craft.craftNameHi, fontSize = 11.5.sp, color = DhaagaPrimary, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleHeritageBookmark(craft.craftId) }
                                    ) {
                                        Icon(Icons.Default.Bookmark, contentDescription = "Remove bookmark", tint = DhaagaPrimary, modifier = Modifier.size(20.dp))
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
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector? = null,
    iconTint: Color = DhaagaPrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DhaagaPrimary)
            if (icon != null) {
                Spacer(modifier = Modifier.width(3.dp))
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(13.dp))
            }
        }
        Text(label, fontSize = 11.sp, color = DhaagaTextMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 15.sp)
    }
}

data class ProfileMenuItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val subtitle: String
)

@Composable
private fun ProfileMenuSection(
    title: String,
    items: List<ProfileMenuItem>,
    onItemClick: (String) -> Unit = {}
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
        items.forEachIndexed { i, item ->
            if (i > 0) HorizontalDivider(color = DhaagaDivider.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item.id) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(item.icon, contentDescription = null, tint = DhaagaPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DhaagaTextDark, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    if (item.subtitle.isNotBlank()) Text(item.subtitle, fontSize = 12.sp, color = DhaagaTextLight, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DhaagaTextLight.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
