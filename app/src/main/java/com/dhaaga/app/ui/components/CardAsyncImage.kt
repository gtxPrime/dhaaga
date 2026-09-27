package com.dhaaga.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.dhaaga.app.R
import com.dhaaga.app.ui.theme.PaletteTerracotta
import kotlin.math.abs

/**
 * Resolves an authentic, high-resolution bundled local Indian cultural craft drawable
 * as an intelligent fallback so zero cards or banners ever render as blank or broken placeholders.
 */
fun getCraftFallbackDrawable(identifier: String?): Int {
    val key = identifier?.lowercase() ?: ""
    return when {
        key.contains("warli") || key.contains("palghar") || key.contains("tarpa") || key.contains("harvest") ->
            R.drawable.banner_warli_art
        key.contains("madhubani") || key.contains("mithila") || key.contains("peacock") || key.contains("bihar") ->
            R.drawable.banner_madhubani_art
        key.contains("pottery") || key.contains("blue") || key.contains("ceramic") || key.contains("jaipur") || key.contains("tea") ->
            R.drawable.banner_blue_pottery
        key.contains("dhokra") || key.contains("metal") || key.contains("bronze") || key.contains("bastar") || key.contains("figurine") ->
            R.drawable.banner_dhokra_metal
        key.contains("pashmina") || key.contains("kashmir") || key.contains("shawl") || key.contains("silk") || key.contains("bandhani") || key.contains("chikankari") || key.contains("kurta") ->
            R.drawable.banner_pashmina_loom
        else -> when (abs(key.hashCode()) % 5) {
            0 -> R.drawable.banner_warli_art
            1 -> R.drawable.banner_madhubani_art
            2 -> R.drawable.banner_blue_pottery
            3 -> R.drawable.banner_pashmina_loom
            else -> R.drawable.banner_dhokra_metal
        }
    }
}

/**
 * Standardized Card Image Loader for Dhaaga.
 * Displays a smooth, neutral platinum shimmer effect during loading,
 * and if network or external image servers fail, immediately displays an
 * authentic, high-resolution cultural heritage artwork instead of a blank box.
 */
@Composable
fun CardAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    indicatorSize: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(0.dp)
) {
    val context = LocalContext.current
    val fallbackDrawableRes = remember(contentDescription, model) {
        val identifier = (contentDescription ?: "") + " " + (model?.toString() ?: "")
        getCraftFallbackDrawable(identifier)
    }

    val cleanModel = remember(model, fallbackDrawableRes) {
        val s = model?.toString() ?: ""
        if (s.contains("1582560475093") || s.contains("1579783902614")) {
            fallbackDrawableRes
        } else {
            model
        }
    }

    val imageRequest = remember(cleanModel, fallbackDrawableRes) {
        ImageRequest.Builder(context)
            .data(cleanModel)
            .error(fallbackDrawableRes)
            .crossfade(true)
            .crossfade(300)
            .build()
    }

    // Dynamic shimmering gradient animation with pristine slate platinum tones (Zero green wash)
    val infiniteTransition = rememberInfiniteTransition(label = "card_img_shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF8FAFC), // Slate 50
            Color(0xFFE2E8F0), // Slate 200
            Color(0xFFF8FAFC)  // Slate 50
        ),
        start = Offset(shimmerTranslate, 0f),
        end = Offset(shimmerTranslate + 320f, 320f)
    )

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.clip(shape),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(shimmerBrush),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(indicatorSize),
                    color = PaletteTerracotta,
                    strokeWidth = 2.2.dp
                )
            }
        },
        error = {
            // High-resolution authentic Indian cultural heritage artwork fallback
            Image(
                painter = painterResource(id = fallbackDrawableRes),
                contentDescription = contentDescription ?: "Heritage Craft Image",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        },
        success = {
            SubcomposeAsyncImageContent(modifier = Modifier.fillMaxSize())
        }
    )
}

