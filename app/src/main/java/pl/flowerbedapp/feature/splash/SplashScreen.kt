package pl.flowerbedapp.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        // Animate in
        alpha.animateTo(1f, animationSpec = tween(700))
        scale.animateTo(1f, animationSpec = tween(700))
        delay(1_000)
        onFinished()
    }

    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(FlowerbedColors.BackgroundDark),
        contentAlignment  = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier
                .alpha(alpha.value)
                .scale(scale.value),
        ) {
            Icon(
                imageVector      = Icons.Default.Grass,
                contentDescription = "Flowerbed logo",
                tint             = FlowerbedColors.GardenGreen,
                modifier         = Modifier.size(96.dp),
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text  = "Flowerbed",
                style = FlowerbedType.displayLarge,
                color = FlowerbedColors.TextPrimary,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text  = "Your garden starts here",
                style = FlowerbedType.bodyMedium,
                color = FlowerbedColors.TextSecondary,
            )
        }
    }
}
