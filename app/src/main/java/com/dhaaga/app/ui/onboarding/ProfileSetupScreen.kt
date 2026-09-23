package com.dhaaga.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.components.FontAwesomeIcons
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.dhaaga.app.ui.theme.*

fun String.toTitleCase(): String {
    return this.trim().split("\\s+".toRegex()).joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    role: String,
    phone: String = "",
    email: String = "",
    initialName: String = "",
    photoUrl: String = "",
    uid: String = "",
    viewModel: com.dhaaga.app.AppViewModel? = null,
    onComplete: (UserModel) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var inputPhone by remember {
        mutableStateOf(phone.trim().removePrefix("+91").removePrefix("+").trim())
    }
    var village by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("") }
    var craft by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    var showStateSheet by remember { mutableStateOf(false) }
    var stateSearchQuery by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val isSeller = role == "seller"
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DhaagaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(listOf(DhaagaPrimary, DhaagaPrimaryLight))
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    com.dhaaga.app.ui.components.NotionAvatar(
                        name = if (name.isNotBlank()) name else (if (isSeller) "Artisan" else "Buyer"),
                        size = 72.dp,
                        borderWidth = 2.dp,
                        imageUrl = photoUrl.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isSeller) (viewModel?.tr("artisan_profile", "Set up your Artisan Profile") ?: "Set up your Artisan Profile") else "Tell Us About Yourself",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isSeller) "Your digital storefront awaits" else "Discover authentic handmade crafts",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Onboarding Guide
            com.dhaaga.app.ui.components.AudioGuideCard(
                englishText = if (isSeller) "Please enter your name, village, and state to create your artisan profile." else "Please enter your name and city to complete your buyer profile.",
                hindiText = if (isSeller) "कृपया अपनी कारीगर प्रोफ़ाइल बनाने के लिए अपना नाम, गाँव और राज्य दर्ज करें।" else "कृपया अपनी प्रोफ़ाइल पूरी करने के लिए अपना नाम और शहर दर्ज करें।",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fields with Title Case capitalization
            DhaagaTextField(
                value = name,
                onValueChange = { name = it },
                label = "Your Name",
                placeholder = if (isSeller) "e.g. Savita Dhodi" else "e.g. Rahul Sharma",
                capitalization = KeyboardCapitalization.Words
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number (no OTP / verification needed)
            DhaagaTextField(
                value = inputPhone,
                onValueChange = { inputPhone = it.filter { c -> c.isDigit() }.take(10) },
                label = "Phone Number",
                placeholder = "e.g. 9876543210",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isSeller) {
                DhaagaTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = "Village / Town",
                    placeholder = "e.g. Mokhada",
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Themed State Selector Field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showStateSheet = true }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Select State / UT") },
                        placeholder = { Text("Choose your state", color = DhaagaTextLight) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select State",
                                tint = DhaagaPrimary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = DhaagaTextDark,
                            disabledBorderColor = DhaagaBorder,
                            disabledLabelColor = DhaagaPrimary,
                            disabledPlaceholderColor = DhaagaTextLight,
                            disabledTrailingIconColor = DhaagaPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DhaagaTextField(
                    value = craft,
                    onValueChange = { craft = it },
                    label = "Your Craft Type",
                    placeholder = "e.g. Warli Art, Madhubani",
                    capitalization = KeyboardCapitalization.Words
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Info box with vector icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DhaagaAccent.copy(alpha = 0.08f))
                        .border(1.dp, DhaagaAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = DhaagaAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI will auto-detect your GI Tag eligibility after setup",
                            fontSize = 13.sp,
                            color = DhaagaAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                DhaagaTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "Your City",
                    placeholder = "e.g. Bengaluru",
                    capitalization = KeyboardCapitalization.Words
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isSubmitting) return@Button
                    val trimmedName = name.trim().toTitleCase().ifBlank { if (isSeller) "Artisan" else "Buyer" }
                    val trimmedVillage = (if (isSeller) village else city).trim().toTitleCase().ifBlank { "India" }
                    val trimmedState = selectedState.trim().ifBlank { "India" }
                    val trimmedCraft = craft.trim().toTitleCase()

                    val effectiveUid = uid.ifEmpty { "user_${System.currentTimeMillis()}" }
                    val enteredDigits = inputPhone.filter { it.isDigit() }
                    val effectivePhone = when {
                        enteredDigits.length == 10 -> "+91$enteredDigits"
                        inputPhone.isNotBlank() -> inputPhone.trim()
                        phone.isNotBlank() -> phone.trim()
                        else -> "+919876543210"
                    }

                    val user = if (isSeller) {
                        UserModel(
                            uid = effectiveUid,
                            phoneNumber = effectivePhone,
                            email = email,
                            name = trimmedName,
                            profilePhotoUrl = photoUrl,
                            village = trimmedVillage,
                            state = trimmedState,
                            role = "seller",
                            craftTypes = if (trimmedCraft.isNotBlank()) listOf(trimmedCraft) else listOf("Handicrafts"),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    } else {
                        UserModel(
                            uid = effectiveUid,
                            phoneNumber = effectivePhone,
                            email = email,
                            name = trimmedName,
                            profilePhotoUrl = photoUrl,
                            village = trimmedVillage,
                            state = trimmedState,
                            role = "buyer",
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    }
                    isSubmitting = true
                    coroutineScope.launch {
                        delay(250L)
                        onComplete(user)
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.2.dp
                    )
                } else {
                    Text(
                        text = viewModel?.tr("continue_btn", "Complete Setup") ?: "Complete Setup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Themed State Selection Bottom Sheet
        if (showStateSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val filteredStates = remember(stateSearchQuery) {
                if (stateSearchQuery.isBlank()) {
                    MockData.indianStates
                } else {
                    MockData.indianStates.filter {
                        it.contains(stateSearchQuery.trim(), ignoreCase = true)
                    }
                }
            }

            ModalBottomSheet(
                onDismissRequest = {
                    showStateSheet = false
                    stateSearchQuery = ""
                },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 8.dp)
                            .width(44.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(DhaagaBorder)
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    // Header with title & close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Select State / UT",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DhaagaTextDark
                            )
                            Text(
                                text = "36 States & Union Territories of India",
                                fontSize = 12.sp,
                                color = DhaagaTextMedium
                            )
                        }
                        IconButton(
                            onClick = {
                                showStateSheet = false
                                stateSearchQuery = ""
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DhaagaBackground)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = DhaagaTextMedium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search field
                    OutlinedTextField(
                        value = stateSearchQuery,
                        onValueChange = { stateSearchQuery = it },
                        placeholder = { Text("Search state or UT...", fontSize = 14.sp, color = DhaagaTextLight) },
                        leadingIcon = {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.MagnifyingGlass,
                                contentDescription = "Search",
                                tint = DhaagaTextMedium,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (stateSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { stateSearchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = DhaagaTextMedium,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DhaagaPrimary,
                            unfocusedBorderColor = DhaagaBorder,
                            focusedContainerColor = DhaagaBackground.copy(alpha = 0.5f),
                            unfocusedContainerColor = DhaagaBackground.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // List of states
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (filteredStates.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No state matching \"$stateSearchQuery\"",
                                        fontSize = 14.sp,
                                        color = DhaagaTextMedium
                                    )
                                }
                            }
                        } else {
                            items(filteredStates) { stateItem ->
                                val isSelected = stateItem.equals(selectedState, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) PaletteGreenTint.copy(alpha = 0.15f) else Color.Transparent,
                                    border = if (isSelected) {
                                        androidx.compose.foundation.BorderStroke(1.5.dp, DhaagaPrimary)
                                    } else {
                                        androidx.compose.foundation.BorderStroke(1.dp, DhaagaBorder.copy(alpha = 0.5f))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedState = stateItem
                                            showStateSheet = false
                                            stateSearchQuery = ""
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 13.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stateItem,
                                            fontSize = 15.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) DhaagaPrimary else DhaagaTextDark
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = FontAwesomeIcons.Solid.Check,
                                                contentDescription = "Selected",
                                                tint = DhaagaPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DhaagaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = DhaagaTextLight) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = capitalization, keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DhaagaPrimary,
            focusedLabelColor = DhaagaPrimary,
            cursorColor = DhaagaPrimary
        )
    )
}
