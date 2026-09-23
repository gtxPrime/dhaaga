package com.dhaaga.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.dhaaga.app.ui.theme.PaletteForest

/**
 * Standardized Card Image Loader for Dhaaga.
 * Displays a smooth, themed shimmer effect and a forest-tinted progress indicator
 * while the image resource or process is underway, ensuring zero blank-holes in cards.
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
    val imageRequest = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .crossfade(350)
            .build()
    }

    // Dynamic shimmering gradient animation for placeholder
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
            Color(0xFFF2F5ED),
            Color(0xFFE2EAD9),
            Color(0xFFF2F5ED)
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
                    color = PaletteForest,
                    strokeWidth = 2.2.dp
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEFF3EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = contentDescription ?: "Image preview unavailable",
                    tint = Color(0xFFA0B09A),
                    modifier = Modifier.size(indicatorSize * 1.2f)
                )
            }
        },
        success = {
            SubcomposeAsyncImageContent(modifier = Modifier.fillMaxSize())
        }
    )
}
