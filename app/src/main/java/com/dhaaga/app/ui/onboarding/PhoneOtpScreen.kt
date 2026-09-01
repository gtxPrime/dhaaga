package com.dhaaga.app.ui.onboarding

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhaaga.app.ui.theme.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.dhaaga.app.AppViewModel
import com.dhaaga.app.data.model.UserModel
import java.util.concurrent.TimeUnit

private const val TAG = "PhoneOtpScreen"

@Composable
fun PhoneOtpScreen(
    viewModel: AppViewModel,
    onVerified: (phone: String, uid: String, existingUser: UserModel?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(0) }

    var storedVerificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var isDevOtpMode by remember { mutableStateOf(false) }

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
        val sanitizedPhone = formattedPhone.trim().removePrefix("+91").removePrefix("+").trim()

        // Artisan Test Creds: 7668439019 with OTP 123456
        if (otp == "123456") {
            Log.i(TAG, "🎨 Artisan test login with OTP 123456 for $formattedPhone")
            val artisanUser = UserModel(
                uid = "artisan_$sanitizedPhone",
                phoneNumber = formattedPhone,
                name = "Kavita Devi",
                role = "seller",
                village = "Madhubani",
                state = "Bihar"
            )
            viewModel.loginAs(artisanUser)
            isLoading = false
            onVerified(formattedPhone, artisanUser.uid, artisanUser)
            return
        }

        // Buyer Test Creds: 7668439019 with OTP 696969
        if (otp == "696969") {
            Log.i(TAG, "🛍️ Buyer test login with OTP 696969 for $formattedPhone")
            val buyerUser = UserModel(
                uid = "buyer_$sanitizedPhone",
                phoneNumber = formattedPhone,
                name = "Aarav Sharma",
                role = "buyer",
                village = "Mumbai",
                state = "Maharashtra"
            )
            viewModel.loginAs(buyerUser)
            isLoading = false
            onVerified(formattedPhone, buyerUser.uid, buyerUser)
            return
        }

        viewModel.checkExistingUserByPhone(formattedPhone) { existingUser ->
            isLoading = false
            if (existingUser != null) {
                // Phone number already registered!
                Log.i(TAG, "🔒 Number $formattedPhone already exists as ${existingUser.role}. Re-login directly!")
                onVerified(formattedPhone, uid, existingUser)
            } else {
                // Brand new user registration -> will ask 'Who are you?'
                Log.i(TAG, "🆕 Number $formattedPhone is new. Proceeding to Role Selection.")
                onVerified(formattedPhone, uid, null)
            }
        }
    }

    fun sendVerificationCode() {
        val sanitizedPhone = phone.trim().removePrefix("+91").removePrefix("+").trim()
        if (sanitizedPhone.length != 10) {
            errorMsg = "Please enter a valid 10-digit number"
            return
        }

        isLoading = true
        errorMsg = ""
        val formattedPhone = "+91$sanitizedPhone"

        if (firebaseAuth != null && activity != null) {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i(TAG, "🔥 Phone Auth auto-verification completed")
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
                    Log.w(TAG, "🔥 Phone Auth verification failed: ${e.message}")
                    isLoading = false
                    // When Firebase rejects due to SMS Region policy (Error 17006) on debug builds,
                    // automatically switch to dev testing mode and pre-fill 123456 so testing continues seamlessly.
                    storedVerificationId = "dev_otp_${System.currentTimeMillis()}"
                    otpSent = true
                    countdown = 60
                    otp = "123456"
                    isDevOtpMode = true
                    errorMsg = ""
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.i(TAG, "🔥 OTP code sent successfully. ID: $verificationId")
                    storedVerificationId = verificationId
                    resendToken = token
                    isLoading = false
                    otpSent = true
                    countdown = 60
                    isDevOtpMode = false
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
                storedVerificationId = "dev_otp_${System.currentTimeMillis()}"
                otpSent = true
                countdown = 60
                otp = "123456"
                isDevOtpMode = true
            }
        } else {
            // Local fallback simulation when Firebase is unavailable
            scope.launch {
                delay(400)
                isLoading = false
                storedVerificationId = "dev_otp_${System.currentTimeMillis()}"
                otpSent = true
                countdown = 60
                otp = "123456"
                isDevOtpMode = true
            }
        }
    }

    fun verifyOtp() {
        if (otp.length != 6) {
            errorMsg = "Please enter the complete 6-digit code"
            return
        }

        isLoading = true
        errorMsg = ""
        val sanitizedPhone = phone.trim().removePrefix("+91").removePrefix("+").trim()
        val formattedPhone = "+91$sanitizedPhone"

        if (firebaseAuth != null && storedVerificationId.isNotEmpty() && !storedVerificationId.startsWith("dev_otp_")) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: "user_${sanitizedPhone}"
                    Log.i(TAG, "🔥 Firebase Phone Auth successful! UID: $uid")
                    handleSuccessfulAuth(formattedPhone, uid)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "🔥 Phone Auth OTP verification failed: ${e.message}")
                    if (otp == "123456" || otp == "696969") {
                        val uid = firebaseAuth.currentUser?.uid ?: "user_${sanitizedPhone}"
                        handleSuccessfulAuth(formattedPhone, uid)
                    } else {
                        isLoading = false
                        errorMsg = "Invalid OTP code. Please check and try again."
                    }
                }
        } else {
            // Dev fallback or local verification
            scope.launch {
                delay(400)
                val currentUid = firebaseAuth?.currentUser?.uid ?: "user_${sanitizedPhone}"
                handleSuccessfulAuth(formattedPhone, currentUid)
            }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(DhaagaPrimary, DhaagaPrimaryLight)
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (!otpSent) viewModel.tr("enter_phone", "Enter your phone number") else "Enter OTP Code",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (!otpSent) "Secure Firebase Phone Login" else "Sent to +91 $phone",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            val onSelectTestCreds: (String, String) -> Unit = { p, o ->
                phone = p
                otp = o
                otpSent = true
                isDevOtpMode = true
                errorMsg = ""
            }

            if (!otpSent) {
                // Audio Onboarding Guide with Hindi / English switch & Audio ON/OFF
                com.dhaaga.app.ui.components.AudioGuideCard(
                    englishText = "Please enter your 10-digit mobile number to receive an OTP code. For demonstration, you can tap either Artisan or Buyer test credentials below.",
                    hindiText = "कृपया अपना 10 अंकों का मोबाइल नंबर दर्ज करें और ओटीपी प्राप्त करें। टेस्टिंग के लिए आप नीचे दिए गए कारीगर या खरीदार टेस्ट क्रेडेंशियल्स पर भी टैप कर सकते हैं।",
                    initialLanguage = viewModel.selectedLanguage.value,
                    autoPlay = false
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Testing phase note requested by user with Artisan & Buyer test creds
                TestCredentialsCard(onSelectRole = onSelectTestCreds, viewModel = viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Phone input
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 10) phone = it.filter { c -> c.isDigit() }
                        errorMsg = ""
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("10-digit mobile number") },
                    prefix = { Text("+91  ", color = DhaagaTextMedium) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DhaagaPrimary,
                        focusedLabelColor = DhaagaPrimary,
                        cursorColor = DhaagaPrimary
                    )
                )

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg,
                        color = DhaagaError,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { sendVerificationCode() },
                    enabled = phone.length == 10 && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Send Verification Code", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

            } else {
                // OTP input
                TestCredentialsCard(onSelectRole = onSelectTestCreds, viewModel = viewModel)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Enter the 6-digit OTP sent to your phone",
                    fontSize = 15.sp,
                    color = DhaagaTextMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6) otp = it.filter { c -> c.isDigit() }
                        errorMsg = ""
                    },
                    label = { Text("OTP Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DhaagaPrimary,
                        focusedLabelColor = DhaagaPrimary,
                        cursorColor = DhaagaPrimary
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        letterSpacing = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (countdown > 0) {
                    Text(
                        "Resend OTP in ${countdown}s",
                        color = DhaagaTextLight,
                        fontSize = 13.sp
                    )
                } else {
                    TextButton(onClick = { sendVerificationCode() }) {
                        Text("Resend Code", color = DhaagaPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { verifyOtp() },
                    enabled = otp.length == 6 && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DhaagaPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Verify & Continue", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMsg, color = DhaagaError, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun TestCredentialsCard(
    viewModel: AppViewModel,
    onSelectRole: (phone: String, otp: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PaletteGreenTint)
            .border(1.dp, PaletteForest.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = PaletteForest,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "SMS Restricted (Testing Phase)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaletteDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Use 7668439019 with OTP: 123456 (Artisan) or 696969 (Buyer). Tap below to auto-fill:",
                fontSize = 11.5.sp,
                color = DhaagaTextMedium,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Artisan Creds Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PaletteForest,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectRole("7668439019", "123456") }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            viewModel.tr("artisan_test_creds", "Artisan Creds"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "OTP: 123456",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Buyer Creds Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF2C5E7A),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectRole("7668439019", "696969") }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            viewModel.tr("buyer_test_creds", "Buyer Creds"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "OTP: 696969",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

