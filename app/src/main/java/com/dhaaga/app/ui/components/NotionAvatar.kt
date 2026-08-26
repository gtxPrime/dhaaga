package com.dhaaga.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun NotionAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderWidth: Dp = 2.dp
) {
    val cleanName = name.trim().ifBlank { "Artisan" }
    val initial = cleanName.firstOrNull()?.uppercaseChar()?.toString() ?: "A"

    val paletteIndex = kotlin.math.abs(cleanName.hashCode()) % 5
    val colors = listOf(
        Pair(Color(0xFFE55A1B), Color(0xFFF39C12)),
        Pair(Color(0xFF8E44AD), Color(0xFF3498DB)),
        Pair(Color(0xFF16A085), Color(0xFF2ECC71)),
        Pair(Color(0xFFD35400), Color(0xFFC0392B)),
        Pair(Color(0xFF2C3E50), Color(0xFF34495E))
    )
    val (c1, c2) = colors[paletteIndex]

    // High quality avatar API endpoint
    val avatarUrl = "https://ui-avatars.com/api/?name=${cleanName.replace(" ", "+")}&background=E55A1B&color=ffffff&bold=true&rounded=true&size=256"

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(c1, c2)))
            .border(borderWidth, Color.White.copy(alpha = 0.9f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = cleanName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
