package pl.flowerbedapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.flowerbedapp.R

// ─── Colors ───────────────────────────────────────────────────────────────────

object FlowerbedColors {
    val GardenGreen     = Color(0xFF4CAF50)
    val GardenGreenDark = Color(0xFF2E7D32)
    val GardenGreenLight = Color(0xFFA5D6A7)
    val EarthBrown      = Color(0xFF8D6E63)
    val EarthBrownLight = Color(0xFFD7CCC8)
    val AlertDanger     = Color(0xFFB71C1C)
    val AlertWarning    = Color(0xFFE65100)
    val AlertInfo       = Color(0xFF1B5E20)
    val SurfaceDark     = Color(0xFF1A1A1A)
    val SurfaceElevated = Color(0xFF2C2C2C)
    val BackgroundDark  = Color(0xFF0F0F0F)
    val TextPrimary     = Color(0xFFFFFFFF)
    val TextSecondary   = Color(0xB3FFFFFF)
    val Translucent     = Color(0xCC000000)
    val TranslucentLight = Color(0x80000000)
}

private val DarkColorScheme = darkColorScheme(
    primary          = FlowerbedColors.GardenGreen,
    onPrimary        = Color.Black,
    primaryContainer = FlowerbedColors.GardenGreenDark,
    secondary        = FlowerbedColors.EarthBrown,
    onSecondary      = Color.White,
    background       = FlowerbedColors.BackgroundDark,
    surface          = FlowerbedColors.SurfaceDark,
    onSurface        = FlowerbedColors.TextPrimary,
    onBackground     = FlowerbedColors.TextPrimary,
    error            = Color(0xFFCF6679),
)

// ─── Typography ───────────────────────────────────────────────────────────────

// Add Playfair Display font files to res/font/ in Android Studio
// For now falls back to system serif
val PlayfairDisplay = FontFamily.Serif

object FlowerbedType {
    val displayLarge  = TextStyle(fontFamily = PlayfairDisplay, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    val headlineMedium = TextStyle(fontFamily = PlayfairDisplay, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    val titleMedium   = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium)
    val bodyMedium    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
    val labelSmall    = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
}

// ─── Spacing ──────────────────────────────────────────────────────────────────

object Spacing {
    val xs: Dp  = 4.dp
    val sm: Dp  = 8.dp
    val md: Dp  = 16.dp
    val lg: Dp  = 24.dp
    val xl: Dp  = 32.dp
    val xxl: Dp = 48.dp
}

// ─── Theme ────────────────────────────────────────────────────────────────────

@Composable
fun FlowerbedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content     = content,
    )
}
