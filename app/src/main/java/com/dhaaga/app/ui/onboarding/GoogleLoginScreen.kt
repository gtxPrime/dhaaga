package com.dhaaga.app.ui.onboarding

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.R
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.components.AudioGuideCard
import com.dhaaga.app.ui.components.FontAwesomeIcons
import com.dhaaga.app.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "GoogleLoginScreen"
private const val DEFAULT_WEB_CLIENT_ID = "319419834074-9qlal5v4iase8ov9mhupacmd80arrl7h.apps.googleusercontent.com"

@Composable
fun GoogleLoginScreen(
    viewModel: AppViewModel,
    onReturningUser: (UserModel) -> Unit,
    onNewUser: (uid: String, email: String, name: String, photoUrl: String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val firebaseAuth = remember {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth initialization warning: ${e.message}")
            null
        }
    }

    // Resolve Web Client ID from resources or fallback constant
    val webClientId = remember {
        try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) context.getString(resId) else DEFAULT_WEB_CLIENT_ID
        } catch (e: Exception) {
            DEFAULT_WEB_CLIENT_ID
        }
    }

    // Google Sign-In Client
    val googleSignInClient = remember(webClientId) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()
        activity?.let { GoogleSignIn.getClient(it, gso) }
    }

    fun getHighQualityGooglePhotoUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        return when {
            url.contains(Regex("=s\\d+(-c)?$")) -> url.replace(Regex("=s\\d+(-c)?$"), "=s800-c")
            url.contains(Regex("=s\\d+")) -> url.replace(Regex("=s\\d+"), "=s800")
            url.contains("/s96-c/") -> url.replace("/s96-c/", "/s800-c/")
            url.contains(Regex("/s\\d+(-c)?/")) -> url.replace(Regex("/s\\d+(-c)?/"), "/s800-c/")
            else -> url
        }
    }

    fun handleFirebaseUser(uid: String, email: String, displayName: String, rawPhotoUrl: String) {
        val photoUrl = getHighQualityGooglePhotoUrl(rawPhotoUrl)
        isLoading = true
        errorMessage = null

        viewModel.checkExistingUserByUid(uid) { existingUser ->
            isLoading = false
            if (existingUser != null && existingUser.name.isNotBlank() && existingUser.role.isNotBlank()) {
                // Returning registered user: Account type is locked, bypass role selection & profile
                Log.i(TAG, "🔒 Returning user found ($uid, ${existingUser.name}, Role=${existingUser.role}). Direct login!")
                val userToLogin = if (photoUrl.isNotBlank() && (existingUser.profilePhotoUrl.isBlank() || !existingUser.profilePhotoUrl.contains("=s800"))) {
                    existingUser.copy(profilePhotoUrl = photoUrl)
                } else {
                    existingUser
                }
                viewModel.loginAs(userToLogin)
                onReturningUser(userToLogin)
            } else {
                // Brand new user: Proceed to Role Selection (Artisan or Buyer)
                Log.i(TAG, "🆕 New Google user ($uid, email=$email, name=$displayName, photo=$photoUrl). Navigating to Role Selection.")
                onNewUser(uid, email, displayName, photoUrl)
            }
        }
    }

    // Activity Result Launcher for Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (!idToken.isNullOrEmpty() && firebaseAuth != null) {
                    isLoading = true
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener { authResult ->
                            val fbUser = authResult.user
                            val uid = fbUser?.uid ?: account.id ?: "google_${System.currentTimeMillis()}"
                            val email = fbUser?.email ?: account.email ?: ""
                            val name = fbUser?.displayName ?: account.displayName ?: ""
                            val photo = fbUser?.photoUrl?.toString() ?: account.photoUrl?.toString() ?: ""
                            handleFirebaseUser(uid, email, name, photo)
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Log.e(TAG, "Firebase auth with Google credential failed", e)
                            errorMessage = "Authentication failed: ${e.localizedMessage ?: "Please try again."}"
                        }
                } else {
                    // Sign in succeeded locally with Google Account without Firebase token
                    val uid = account.id ?: "google_${System.currentTimeMillis()}"
                    val email = account.email ?: ""
                    val name = account.displayName ?: ""
                    val photo = account.photoUrl?.toString() ?: ""
                    handleFirebaseUser(uid, email, name, photo)
                }
            } catch (e: ApiException) {
                isLoading = false
                Log.e(TAG, "Google Sign-In ApiException: code ${e.statusCode}, message: ${e.message}")
                if (e.statusCode == 12501) {
                    // User explicitly cancelled the dialog
                    errorMessage = null
                } else {
                    errorMessage = "Google sign-in error (${e.statusCode}). You can use Demo Login below for instant access."
                }
            }
        } else {
            isLoading = false
        }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Back button (if available)
            if (onBack != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DhaagaTextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top Hero Illustration / Logo Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(DhaagaPrimaryLight, DhaagaPrimary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Subtitle
            Text(
                text = viewModel.tr("welcome_dhaaga", "Welcome to Dhaaga"),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DhaagaTextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = viewModel.tr("cultural_tagline", "Connecting Indian Artisans & Authentic Craft Lovers"),
                fontSize = 14.sp,
                color = DhaagaTextMedium,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Guide Card
            AudioGuideCard(
                englishText = "Welcome to Dhaaga! Please sign in with your Google account. If you are an existing user, you will be taken directly to the app. If you are new, you can choose whether you are an Artisan or a Buyer.",
                hindiText = "धागा में आपका स्वागत है! कृपया अपने गूगल खाते से लॉगिन करें। यदि आप पहले से पंजीकृत हैं, तो सीधे ऐप खुल जाएगा। यदि आप नए हैं, तो आप कारीगर या खरीदार चुन सकेंगे।",
                initialLanguage = viewModel.selectedLanguage.value
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error Message (if any)
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEBEE))
                            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                color = Color(0xFFC62828),
                                lineHeight = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Google Sign-In Card Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), clip = false)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(1.dp, DhaagaDivider.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .clickable(enabled = !isLoading) {
                        isLoading = true
                        errorMessage = null
                        if (googleSignInClient != null) {
                            // Ensure clean account chooser
                            googleSignInClient.signOut().addOnCompleteListener {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        } else {
                            isLoading = false
                            errorMessage = "Google Play Services is not available on this device. Use Demo Login below."
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = DhaagaPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = viewModel.tr("signing_in", "Signing in with Google..."),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = DhaagaTextMedium
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Brands.Google,
                            contentDescription = "Google",
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = viewModel.tr("continue_with_google", "Continue with Google"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F1F1F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Trust badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = DhaagaTextLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Fast, secure 1-tap sign in linked permanently to your account",
                    fontSize = 12.sp,
                    color = DhaagaTextLight
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Divider for Quick Testing
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DhaagaDivider.copy(alpha = 0.5f))
                Text(
                    text = " Quick Demo / Offline Access ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DhaagaTextLight
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = DhaagaDivider.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Demo Buttons for Artisan and Buyer (ensures zero blockers on emulators without Play Services)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick Artisan
                OutlinedButton(
                    onClick = {
                        val artisan = UserModel(
                            uid = "artisan_google_demo",
                            email = "savita.artisan@gmail.com",
                            phoneNumber = "+91 9876543210",
                            name = "Savita Dhodi",
                            role = "seller",
                            village = "Mokhada",
                            state = "Maharashtra",
                            craftTypes = listOf("Warli Art")
                        )
                        viewModel.loginAs(artisan)
                        onReturningUser(artisan)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DhaagaPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Demo Artisan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // Quick Buyer
                OutlinedButton(
                    onClick = {
                        val buyer = UserModel(
                            uid = "buyer_google_demo",
                            email = "rahul.buyer@gmail.com",
                            phoneNumber = "+91 9123456780",
                            name = "Rahul Sharma",
                            role = "buyer",
                            village = "Bengaluru",
                            state = "Karnataka"
                        )
                        viewModel.loginAs(buyer)
                        onReturningUser(buyer)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DhaagaPrimary)
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Demo Buyer", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Test New User Onboarding Button
            TextButton(
                onClick = {
                    val randomId = (1000..9999).random()
                    onNewUser(
                        "new_google_user_$randomId",
                        "user$randomId@gmail.com",
                        "Priya Verma",
                        ""
                    )
                }
            ) {
                Text(
                    text = "Test New User Flow (Choose Artisan / Buyer)",
                    fontSize = 12.sp,
                    color = DhaagaTextMedium
                )
            }
        }
    }
}

/**
 * Official FontAwesome Brand Google Logo
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = FontAwesomeIcons.Brands.Google,
        contentDescription = "Google",
        tint = Color(0xFF4285F4),
        modifier = modifier
    )
}
