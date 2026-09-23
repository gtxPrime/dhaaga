package com.dhaaga.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dhaaga.app.navigation.Routes
import com.dhaaga.app.ui.buyer.CartScreen
import com.dhaaga.app.ui.buyer.MyOrdersScreen
import com.dhaaga.app.ui.buyer.WishlistScreen
import com.dhaaga.app.ui.home.HomeScreen
import com.dhaaga.app.ui.onboarding.GoogleLoginScreen
import com.dhaaga.app.ui.onboarding.LanguageSelectionScreen
import com.dhaaga.app.ui.onboarding.PhoneOtpScreen
import com.dhaaga.app.ui.onboarding.ProfileSetupScreen
import com.dhaaga.app.ui.onboarding.RoleSelectionScreen
import com.dhaaga.app.ui.product.ProductDetailScreen
import com.dhaaga.app.ui.profile.ProfileScreen
import com.dhaaga.app.ui.seller.AddProductScreen
import com.dhaaga.app.ui.seller.MyListingsScreen
import com.dhaaga.app.ui.seller.SellerDashboardScreen
import com.dhaaga.app.ui.splash.SplashScreen
import com.dhaaga.app.ui.theme.DhaagaTheme

class MainActivity : FragmentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        // Pre-warm TTS engine for instant voice responses
        com.dhaaga.app.utils.AppTtsManager.init(this)

        setContent {
            DhaagaTheme {
                DhaagaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DhaagaApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val appContext = androidx.compose.ui.platform.LocalContext.current

    // Track onboarding state
    var selectedRole by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userUid by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf("") }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
            exitTransition = { fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
            popEnterTransition = { fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
            popExitTransition = { fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) }
        ) {
            // ── Splash ─────────────────────────────────────────────────────
            composable(Routes.SPLASH) {
                SplashScreen(onComplete = {
                    val isLoggedIn = viewModel.currentUser.value != null
                    val targetRoute = if (isLoggedIn) Routes.home(0) else Routes.LANGUAGE_SELECT
                    navController.navigate(targetRoute) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }

            // ── Onboarding ─────────────────────────────────────────────────
            composable(Routes.LANGUAGE_SELECT) {
                LanguageSelectionScreen(
                    viewModel = viewModel,
                    onLanguageSelected = { lang ->
                        viewModel.setLanguage(lang)
                        navController.navigate(Routes.GOOGLE_LOGIN)
                    }
                )
            }

            composable(Routes.GOOGLE_LOGIN) {
                GoogleLoginScreen(
                    viewModel = viewModel,
                    onReturningUser = { existingUser ->
                        // User account already exists & type is locked!
                        // Direct entry to app! No type selection!
                        selectedRole = existingUser.role
                        viewModel.loginAs(existingUser)
                        navController.navigate(Routes.home(0)) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                    onNewUser = { uid, email, name, photoUrl ->
                        // Brand new user: record Google info, then prompt "Who are you?" (Role Selection: Artisan vs Buyer)
                        userUid = uid
                        userEmail = email
                        userName = name
                        userPhotoUrl = photoUrl
                        navController.navigate(Routes.ROLE_SELECT)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.PHONE_OTP) {
                PhoneOtpScreen(
                    viewModel = viewModel,
                    onVerified = { phone, uid, existingUser ->
                        userPhone = phone
                        userUid = uid

                        if (existingUser != null) {
                            // User account already exists!
                            // No need to ask "Who are you?" - account type is already saved and locked!
                            selectedRole = existingUser.role

                            if (existingUser.name.isNotBlank()) {
                                // Direct re-login for returning registered user
                                viewModel.loginAs(existingUser)
                                navController.navigate(Routes.home(0)) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            } else {
                                // Incomplete profile: go directly to profile setup with existing locked role
                                navController.navigate(Routes.PROFILE_SETUP)
                            }
                        } else {
                            // Brand new user: NOW ask "Who are you?" (Role Selection: Artisan vs Buyer)
                            navController.navigate(Routes.ROLE_SELECT)
                        }
                    }
                )
            }

            composable(
                route = Routes.ROLE_SELECT,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(animationSpec = tween(350))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(animationSpec = tween(350))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                RoleSelectionScreen(
                    viewModel = viewModel,
                    onRoleSelected = { role ->
                        selectedRole = role
                        navController.navigate(Routes.PROFILE_SETUP)
                    }
                )
            }

            composable(
                route = Routes.PROFILE_SETUP,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(animationSpec = tween(350))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(animationSpec = tween(350))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                ProfileSetupScreen(
                    role = selectedRole,
                    phone = userPhone,
                    email = userEmail,
                    initialName = userName,
                    photoUrl = userPhotoUrl,
                    uid = userUid,
                    viewModel = viewModel,
                    onComplete = { user ->
                        viewModel.loginAs(user)
                        navController.navigate(Routes.home(0)) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // ── Home (Swipeable Tab Container) ───────────────────────────────
            composable(
                route = Routes.HOME,
                arguments = listOf(
                    navArgument("tab") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = spring(
                            dampingRatio = 0.82f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(animationSpec = tween(450, easing = FastOutSlowInEasing)) +
                    scaleIn(initialScale = 0.94f, animationSpec = tween(450, easing = FastOutSlowInEasing))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                    scaleOut(targetScale = 0.96f, animationSpec = tween(300, easing = FastOutSlowInEasing))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing)) +
                    scaleIn(initialScale = 0.95f, animationSpec = tween(350, easing = FastOutSlowInEasing))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing))
                }
            ) { backStack ->
                val initialTab = backStack.arguments?.getInt("tab") ?: 0
                HomeScreen(
                    viewModel = viewModel,
                    initialTab = initialTab,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onProductClick = { productId, key ->
                        navController.navigate(Routes.productDetail(productId, key))
                    },
                    onProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onCart = {
                        navController.navigate(Routes.CART)
                    },
                    onOrders = {
                        navController.navigate(Routes.MY_ORDERS)
                    },
                    onMyListings = {
                        navController.navigate(Routes.MY_LISTINGS)
                    },
                    onChatList = {
                        android.widget.Toast.makeText(appContext, "Chat with artisans — Coming Soon!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ── Product detail ─────────────────────────────────────────────
            composable(
                route = Routes.PRODUCT_DETAIL,
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("sharedKey") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                ),
                enterTransition = { fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
                exitTransition = { fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
                popEnterTransition = { fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
                popExitTransition = { fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) }
            ) { backStack ->
                val productId = backStack.arguments?.getString("productId") ?: ""
                val sharedKey = backStack.arguments?.getString("sharedKey") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    sharedKey = sharedKey,
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBack = { navController.popBackStack() },
                    onAddToCart = { product ->
                        viewModel.addToCart(product)
                        val isSeller = viewModel.currentUser.value?.isSeller == true
                        if (isSeller) {
                            navController.navigate(Routes.CART)
                        } else {
                            navController.navigate(Routes.home(2))
                        }
                    },
                    onBulkEnquiry = { product ->
                        android.widget.Toast.makeText(
                            appContext,
                            "Bulk enquiry sent to ${product.sellerName}! They'll contact you shortly.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }

        // ── Profile ────────────────────────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onMyListings = { navController.navigate(Routes.MY_LISTINGS) },
                onMyOrders = { navController.navigate(Routes.MY_ORDERS) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LANGUAGE_SELECT) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Seller screens ─────────────────────────────────────────────
        composable(Routes.SELLER_DASHBOARD) {
            SellerDashboardScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MY_LISTINGS) {
            MyListingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
                onAddProduct = { navController.navigate(Routes.ADD_PRODUCT) }
            )
        }

        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPublish = { navController.popBackStack() }
            )
        }

        // ── Buyer screens ──────────────────────────────────────────────
        composable(Routes.WISHLIST) {
            WishlistScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                }
            )
        }

        composable(Routes.CART) {
            val cart by viewModel.cart.collectAsState()
            val user by viewModel.currentUser.collectAsState()
            CartScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCheckout = {
                    viewModel.placeDemoOrder(cart, user) {
                        navController.navigate(Routes.MY_ORDERS)
                    }
                }
            )
        }

        composable(Routes.MY_ORDERS) {
            MyOrdersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOrderClick = { orderId -> }
            )
        }
    }
}
}
