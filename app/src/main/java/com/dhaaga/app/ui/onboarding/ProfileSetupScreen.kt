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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.data.mock.MockData
import com.dhaaga.app.data.model.UserModel
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
    uid: String = "",
    onComplete: (UserModel) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("") }
    var craft by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    var isStateDropdownExpanded by remember { mutableStateOf(false) }

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
                        borderWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isSeller) "Set up your Artisan Profile" else "Tell Us About Yourself",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Fields with Title Case capitalization
            DhaagaTextField(
                value = name,
                onValueChange = { name = it },
                label = "Your Name",
                placeholder = if (isSeller) "e.g. Savita Dhodi" else "e.g. Rahul Sharma",
                capitalization = KeyboardCapitalization.Words
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

                // State Dropdown List
                ExposedDropdownMenuBox(
                    expanded = isStateDropdownExpanded,
                    onExpandedChange = { isStateDropdownExpanded = !isStateDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select State") },
                        placeholder = { Text("Choose your state", color = DhaagaTextLight) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStateDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = DhaagaPrimary,
                            focusedLabelColor = DhaagaPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isStateDropdownExpanded,
                        onDismissRequest = { isStateDropdownExpanded = false }
                    ) {
                        MockData.indianStates.forEach { stateItem ->
                            DropdownMenuItem(
                                text = { Text(stateItem) },
                                onClick = {
                                    selectedState = stateItem
                                    isStateDropdownExpanded = false
                                }
                            )
                        }
                    }
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
                    val trimmedName = name.trim().toTitleCase().ifBlank { if (isSeller) "Artisan" else "Buyer" }
                    val trimmedVillage = (if (isSeller) village else city).trim().toTitleCase().ifBlank { "India" }
                    val trimmedState = selectedState.trim().ifBlank { "India" }
                    val trimmedCraft = craft.trim().toTitleCase()

                    val effectiveUid = uid.ifEmpty { "user_${System.currentTimeMillis()}" }
                    val effectivePhone = phone.ifEmpty { "+919876543210" }

                    val user = if (isSeller) {
                        MockData.mockSeller.copy(
                            uid = effectiveUid,
                            phoneNumber = effectivePhone,
                            name = trimmedName,
                            village = trimmedVillage,
                            state = trimmedState,
                            role = "seller",
                            craftTypes = if (trimmedCraft.isNotBlank()) listOf(trimmedCraft) else listOf("Handicrafts")
                        )
                    } else {
                        MockData.mockBuyer.copy(
                            uid = effectiveUid,
                            phoneNumber = effectivePhone,
                            name = trimmedName,
                            village = trimmedVillage,
                            role = "buyer"
                        )
                    }
                    onComplete(user)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
            ) {
                Text(
                    text = "Complete Setup",
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DhaagaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = DhaagaTextLight) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = capitalization),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DhaagaPrimary,
            focusedLabelColor = DhaagaPrimary,
            cursorColor = DhaagaPrimary
        )
    )
}
