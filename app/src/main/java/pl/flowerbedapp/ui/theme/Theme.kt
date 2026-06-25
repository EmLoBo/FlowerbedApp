package pl.flowerbedapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

private val LightColorScheme = lightColorScheme(
    primary          = FlowerbedColors.GardenGreen,
    onPrimary        = Color.White,
    primaryContainer = FlowerbedColors.GardenGreenLight,
    secondary        = FlowerbedColors.EarthBrown,
    onSecondary      = Color.White,
    background       = Color(0xFFF7F9F4),
    surface          = Color(0xFFFFFFFF),
    onSurface        = Color(0xFF1A1A1A),
    onBackground     = Color(0xFF1A1A1A),
    error            = Color(0xFFB00020),
)

// Theme-dependent semantic colors. Brand colors (GardenGreen…) and alerts stay constant in
// `FlowerbedColors`; only these few swap with light/dark. Screens should migrate from
// `FlowerbedColors.BackgroundDark` etc. to `FlowerbedTheme.colors.background` etc. over time.
data class FlowerbedColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

val DarkColors = FlowerbedColorScheme(
    background      = Color(0xFF0F0F0F),
    surface         = Color(0xFF1A1A1A),
    surfaceElevated = Color(0xFF2C2C2C),
    textPrimary     = Color(0xFFFFFFFF),
    textSecondary   = Color(0xB3FFFFFF),
)

val LightColors = FlowerbedColorScheme(
    background      = Color(0xFFF7F9F4),
    surface         = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFEDEFEA),
    textPrimary     = Color(0xFF1A1A1A),
    textSecondary   = Color(0x99000000),
)

val LocalFlowerbedColors = staticCompositionLocalOf { DarkColors }

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
fun FlowerbedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val flowerbedColors = if (darkTheme) DarkColors else LightColors
    val materialScheme  = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(LocalFlowerbedColors provides flowerbedColors) {
        MaterialTheme(
            colorScheme = materialScheme,
            content     = content,
        )
    }
}

// Accessor for theme-aware colors: `FlowerbedTheme.colors.background`, etc.
object FlowerbedTheme {
    val colors: FlowerbedColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalFlowerbedColors.current
}
