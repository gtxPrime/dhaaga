package com.dhaaga.app.ui.onboarding

import android.app.Activity
import android.util.Log
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.model.UserModel
import com.dhaaga.app.ui.components.AudioGuideCard
import com.dhaaga.app.ui.theme.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val TAG = "PhoneOtpScreen"

@Composable
fun PhoneOtpScreen(
    viewModel: AppViewModel,
    onVerified: (phone: String, uid: String, existingUser: UserModel?) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val keyboardController = LocalSoftwareKeyboardController.current

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(0) }

    var storedVerificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    val currentLang by viewModel.selectedLanguage.collectAsState()

    val firebaseAuth = remember {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth initialization: ${e.message}")
            null
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    fun handleSuccessfulAuth(formattedPhone: String, uid: String) {
        isLoading = true
        viewModel.checkExistingUserByPhone(formattedPhone) { existingUser ->
            isLoading = false
            if (existingUser != null) {
                Log.i(TAG, "[Auth] Number $formattedPhone already exists as ${existingUser.role}. Re-login directly!")
                onVerified(formattedPhone, uid, existingUser)
            } else {
                Log.i(TAG, "[Auth] Number $formattedPhone is new. Proceeding to Role Selection.")
                onVerified(formattedPhone, uid, null)
            }
        }
    }

    fun sendVerificationCode() {
        val sanitizedPhone = phone.trim().removePrefix("+91").removePrefix("+").trim()
        if (sanitizedPhone.length != 10) {
            errorMsg = if (currentLang == "hi") "कृपया मान्य 10 अंकों का मोबाइल नंबर दर्ज करें" else "Please enter a valid 10-digit mobile number"
            return
        }

        isLoading = true
        errorMsg = ""
        val formattedPhone = "+91$sanitizedPhone"

        if (firebaseAuth != null && activity != null) {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i(TAG, "[Auth] Phone Auth auto-verification completed")
                    firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid ?: "user_${sanitizedPhone}"
                            handleSuccessfulAuth(formattedPhone, uid)
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            errorMsg = e.localizedMessage ?: "Auto verification failed"
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(TAG, "[Auth] Phone Auth verification failed: ${e.message}")
                    isLoading = false
                    errorMsg = e.localizedMessage ?: "Phone verification failed. Please try again."
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.i(TAG, "[Auth] OTP code sent successfully. ID: $verificationId")
                    storedVerificationId = verificationId
                    resendToken = token
                    isLoading = false
                    otpSent = true
                    countdown = 60
                }
            }

            val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)

            if (resendToken != null) {
                optionsBuilder.setForceResendingToken(resendToken!!)
            }

            try {
                PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
            } catch (e: Exception) {
                Log.w(TAG, "Phone verification trigger failed: ${e.message}")
                isLoading = false
                errorMsg = e.localizedMessage ?: "Failed to send verification SMS. Please check your connection."
            }
        } else {
            isLoading = false
            errorMsg = "Firebase Authentication is not available on this device."
        }
    }

    fun verifyOtp() {
        if (otp.length != 6) {
            errorMsg = if (currentLang == "hi") "कृपया 6 अंकों का पूरा कोड दर्ज करें" else "Please enter the complete 6-digit code"
            return
        }

        isLoading = true
        errorMsg = ""
        keyboardController?.hide()
        val sanitizedPhone = phone.trim().removePrefix("+91").removePrefix("+").trim()
        val formattedPhone = "+91$sanitizedPhone"

        if (firebaseAuth != null && storedVerificationId.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: "user_${sanitizedPhone}"
                    Log.i(TAG, "[Auth] Firebase Phone Auth successful! UID: $uid")
                    handleSuccessfulAuth(formattedPhone, uid)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "[Auth] Phone Auth OTP verification failed: ${e.message}")
                    isLoading = false
                    errorMsg = if (currentLang == "hi") "अमान्य ओटीपी कोड। कृपया पुनः प्रयास करें।" else "Invalid OTP code. Please check and try again."
                }
        } else {
            isLoading = false
            errorMsg = "Verification session expired. Please request a new code."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DhaagaBackground)
    ) {
        // Subtle top ambient gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PaletteMintCard.copy(alpha = 0.85f),
                            PaletteCanvas
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top Navigation Bar ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.25f)),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PaletteDarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Dhaaga Heritage Emblem
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "धागा",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PaletteDarkGreen
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(PaletteForest)
                    )
                    Text(
                        text = "DHAAGA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = PaletteForest
                    )
                }

                // Language Toggle Pill
                Surface(
                    onClick = {
                        val nextLang = if (currentLang == "hi") "en" else "hi"
                        viewModel.setLanguage(nextLang)
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = PaletteForest,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (currentLang == "hi") "हिंदी" else "EN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaletteDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Hero Header ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(PaletteForest.copy(alpha = 0.18f), PaletteMintCard)
                        )
                    )
                    .border(2.dp, PaletteForest.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!otpSent) Icons.Default.PhoneIphone else Icons.Default.Security,
                    contentDescription = null,
                    tint = PaletteForest,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (!otpSent) {
                    if (currentLang == "hi") "मोबाइल नंबर से प्रवेश करें" else "Artisan & Explorer Sign In"
                } else {
                    if (currentLang == "hi") "सत्यापन कोड दर्ज करें" else "Verify 6-Digit Code"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PaletteDarkGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!otpSent) {
                Text(
                    text = if (currentLang == "hi")
                        "भारत की जीवित सांस्कृतिक विरासत से जुड़ने के लिए अपना नंबर दर्ज करें"
                    else
                        "Enter your mobile number to connect with Bharat's living cultural heritage",
                    fontSize = 13.sp,
                    color = DhaagaTextMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${if (currentLang == "hi") "कोड भेजा गया:" else "Code sent to:"} +91 $phone",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PaletteDarkGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        onClick = {
                            otpSent = false
                            otp = ""
                            errorMsg = ""
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = PaletteForest.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, PaletteForest.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit phone", tint = PaletteForest, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (currentLang == "hi") "बदलें" else "Edit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaletteForest
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Main Card (Step 1 or Step 2) ────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, PaletteSage.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!otpSent) {
                        // ── Phone Input Row ──
                        Text(
                            text = if (currentLang == "hi") "मोबाइल नंबर" else "Mobile Number",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DhaagaTextMedium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF9FAF8))
                                .border(1.5.dp, if (phone.length == 10) PaletteForest else PaletteSage.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Country Code Chip
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PaletteForest.copy(alpha = 0.08f))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = PaletteForest,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("+91", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PaletteDarkGreen)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Phone input field
                            BasicTextField(
                                value = phone,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(10)
                                    phone = digits
                                    errorMsg = ""
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (phone.length == 10) sendVerificationCode()
                                    }
                                ),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteDarkGreen,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (phone.isEmpty()) {
                                        Text(
                                            text = "98765 43210",
                                            fontSize = 17.sp,
                                            color = PaletteSage.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            if (phone.isNotEmpty()) {
                                IconButton(
                                    onClick = { phone = ""; errorMsg = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = DhaagaTextLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (errorMsg.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DhaagaError, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = errorMsg, color = DhaagaError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Continue Button
                        Button(
                            onClick = { sendVerificationCode() },
                            enabled = phone.length == 10 && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PaletteForest,
                                disabledContainerColor = PaletteSage.copy(alpha = 0.4f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (currentLang == "hi") "ओटीपी प्राप्त करें" else "Get Verification Code",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // ── OTP Entry Step ──
                        Text(
                            text = if (currentLang == "hi") "6 अंकों का कोड दर्ज करें" else "Enter 6-Digit OTP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DhaagaTextMedium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 6-Box PIN Visual Input
                        OtpInputField(
                            otp = otp,
                            onOtpChange = {
                                otp = it
                                errorMsg = ""
                            },
                            onComplete = {
                                verifyOtp()
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Resend Countdown or Action
                        if (countdown > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = PaletteForest, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLang == "hi") "पुनः कोड भेजें: ${countdown}s" else "Resend code in ${countdown}s",
                                    color = DhaagaTextMedium,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            TextButton(
                                onClick = { sendVerificationCode() },
                                colors = ButtonDefaults.textButtonColors(contentColor = PaletteForest)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentLang == "hi") "ओटीपी पुनः भेजें (SMS)" else "Resend OTP via SMS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        if (errorMsg.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DhaagaError, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = errorMsg, color = DhaagaError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { verifyOtp() },
                            enabled = otp.length == 6 && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PaletteForest,
                                disabledContainerColor = PaletteSage.copy(alpha = 0.4f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (currentLang == "hi") "सत्यापित करें और आगे बढ़ें" else "Verify & Continue",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Trust & Security Badges ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecurityTrustPill(icon = Icons.Default.Lock, text = "256-Bit SSL")
                Text("•", color = PaletteSage.copy(alpha = 0.5f), fontSize = 12.sp)
                SecurityTrustPill(icon = Icons.Default.VerifiedUser, text = "TRAI Compliant")
                Text("•", color = PaletteSage.copy(alpha = 0.5f), fontSize = 12.sp)
                SecurityTrustPill(icon = Icons.Default.ElectricBolt, text = "Instant SMS")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 6-Digit Individual Cell OTP Input Field
 */
@Composable
private fun OtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(200)
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier.clickable {
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        },
        contentAlignment = Alignment.Center
    ) {
        // Underlying invisible text input field that captures soft keyboard keystrokes
        BasicTextField(
            value = otp,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }.take(6)
                onOtpChange(digitsOnly)
                if (digitsOnly.length == 6) {
                    onComplete()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (otp.length == 6) onComplete()
                }
            ),
            modifier = Modifier
                .size(1.dp)
                .alpha(0.01f)
                .focusRequester(focusRequester)
        )

        // 6 visually distinct, modern rounded PIN cells
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val digit = otp.getOrNull(i)?.toString() ?: ""
                val isFocused = otp.length == i || (i == 5 && otp.length == 6)
                val isFilled = digit.isNotEmpty()

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        isFilled -> PaletteMintCard
                        isFocused -> Color.White
                        else -> Color(0xFFF9FAF8)
                    },
                    border = BorderStroke(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = when {
                            isFocused -> PaletteForest
                            isFilled -> PaletteForest.copy(alpha = 0.6f)
                            else -> PaletteSage.copy(alpha = 0.35f)
                        }
                    ),
                    shadowElevation = if (isFocused) 3.dp else 0.dp,
                    modifier = Modifier.size(width = 46.dp, height = 56.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (isFilled) {
                            Text(
                                text = digit,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PaletteDarkGreen
                            )
                        } else if (isFocused) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(20.dp)
                                    .background(PaletteForest, RoundedCornerShape(1.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PaletteSage.copy(alpha = 0.45f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityTrustPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PaletteSage,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PaletteSage
        )
    }
}
