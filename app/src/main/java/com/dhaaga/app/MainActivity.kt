package com.dhaaga.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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

    // Track onboarding state
    var selectedRole by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userUid by remember { mutableStateOf("") }

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
                LanguageSelectionScreen(onLanguageSelected = { lang ->
                    navController.navigate(Routes.ROLE_SELECT)
                })
            }

            composable(Routes.ROLE_SELECT) {
                RoleSelectionScreen(onRoleSelected = { role ->
                    selectedRole = role
                    navController.navigate(Routes.PHONE_OTP)
                })
            }

            composable(Routes.PHONE_OTP) {
                PhoneOtpScreen(
                    role = selectedRole,
                    onVerified = { phone, uid ->
                        userPhone = phone
                        userUid = uid
                        navController.navigate(Routes.PROFILE_SETUP)
                    }
                )
            }

            composable(Routes.PROFILE_SETUP) {
                ProfileSetupScreen(
                    role = selectedRole,
                    phone = userPhone,
                    uid = userUid,
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
                enterTransition = { fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
                exitTransition = { fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
                popEnterTransition = { fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) },
                popExitTransition = { fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) }
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
                        viewModel.logout()
                        navController.navigate(Routes.LANGUAGE_SELECT) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onChatList = {}
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
                val productId = backStack.arguments?.getString("productId") ?: return@composable
                val argKey = backStack.arguments?.getString("sharedKey")?.takeIf { it.isNotEmpty() }
                val sharedKey = argKey ?: "product-image-$productId"
                ProductDetailScreen(
                    productId = productId,
                    sharedKey = sharedKey,
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBack = { navController.popBackStack() },
                    onAddToCart = { product ->
                        viewModel.addToCart(product)
                        navController.navigate(Routes.home(2))
                    },
                    onBulkEnquiry = { }
                )
            }

        // ── Profile ────────────────────────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
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
            SellerDashboardScreen(onBack = { navController.popBackStack() })
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
            CartScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCheckout = {
                    // Mock checkout: navigate to order success
                    navController.navigate(Routes.MY_ORDERS)
                }
            )
        }

        composable(Routes.MY_ORDERS) {
            MyOrdersScreen(
                onBack = { navController.popBackStack() },
                onOrderClick = { orderId -> }
            )
        }
    }
}
}
