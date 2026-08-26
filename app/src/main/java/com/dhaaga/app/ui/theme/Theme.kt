package com.dhaaga.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DhaagaColorScheme = lightColorScheme(
    primary = DhaagaPrimary,
    onPrimary = Color.White,
    primaryContainer = DhaagaCardBg,
    onPrimaryContainer = DhaagaTextDark,
    secondary = DhaagaAccent,
    onSecondary = Color.White,
    secondaryContainer = DhaagaAccentLight.copy(alpha = 0.2f),
    onSecondaryContainer = DhaagaTextDark,
    tertiary = DhaagaPrimaryLight,
    onTertiary = Color.White,
    background = DhaagaBackground,
    onBackground = DhaagaTextDark,
    surface = DhaagaSurface,
    onSurface = DhaagaTextDark,
    surfaceVariant = DhaagaCardBg,
    onSurfaceVariant = DhaagaTextMedium,
    error = DhaagaError,
    onError = Color.White,
    outline = DhaagaDivider,
)

@Composable
fun DhaagaTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = DhaagaColorScheme,
        typography = DhaagaTypography,
        content = content
    )
}
