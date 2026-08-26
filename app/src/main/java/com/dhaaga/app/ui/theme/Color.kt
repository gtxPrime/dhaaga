package com.dhaaga.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Pure Earthy Sage & White Palette (ZERO BROWN) ─────────────────────────
// Off-White Canvas
val PaletteCanvas = Color(0xFFFCFCFC)

// Soft Mint Cream (Cards)
val PaletteMintCard = Color(0xFFEFF4EB)
val PaletteGreenTint = Color(0xFFE2EAD9)     // Richer Green Tint for Bottom Nav

// Sage Green (#ACB291)
val PaletteSage = Color(0xFFACB291)
val PaletteSageLight = Color(0xFFD5DCC8)

// Forest Sage Green (#60734E - Primary CTAs & Active States)
val PaletteForest = Color(0xFF60734E)
val PaletteForestBright = Color(0xFF738861)

// Deep Forest Green (Dark Text - Pure Dark Green, NO BROWN)
val PaletteDarkGreen = Color(0xFF1E2C17)

// Standard mappings for app Theme
val DhaagaPrimary = PaletteForest           // #60734E
val DhaagaPrimaryLight = PaletteSage       // #ACB291
val DhaagaAccent = PaletteSage             // #ACB291
val DhaagaAccentLight = PaletteSageLight
val DhaagaBackground = PaletteCanvas       // #FCFCFC
val DhaagaCardBg = PaletteMintCard         // #EFF4EB Soft Mint Card
val DhaagaSurface = Color(0xFFFFFFFF)

// Text (Pure Greens & Dark Forest)
val DhaagaTextDark = PaletteDarkGreen      // #1E2C17 Deep Forest
val DhaagaTextMedium = Color(0xFF3F5435)
val DhaagaTextLight = Color(0xFF677E5C)

// Status
val DhaagaSuccess = PaletteForest
val DhaagaError = Color(0xFFB33A3A)
val DhaagaWarning = Color(0xFF86A03C)

// Dividers & Badges
val DhaagaDivider = PaletteSage
val DhaagaGIBadge = PaletteForest

// Gradients
val GradientStart = PaletteForest          // #60734E
val GradientEnd = PaletteForestBright      // #738861
