package pl.flowerbedapp.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { FlowerbedTopBar(title = "Settings", onBack = onBack) },
        containerColor = FlowerbedColors.BackgroundDark,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text("Account", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
            Text("Log in to unlock premium features: unlimited saved projects, AI plant recommendations, and personalized alerts.",
                style = FlowerbedType.bodyMedium, color = FlowerbedColors.TextSecondary)
            Button(onClick = { /* navigate to login */ }, colors = ButtonDefaults.buttonColors(containerColor = FlowerbedColors.GardenGreen)) {
                Text("Log in / Sign up", color = FlowerbedColors.BackgroundDark)
            }
            HorizontalDivider(color = FlowerbedColors.SurfaceElevated)
            Text("About", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
            Text("Flowerbed v1.0.0\nPlant data: Trefle.io\nWeather: eDWIN agrometeo API",
                style = FlowerbedType.bodyMedium, color = FlowerbedColors.TextSecondary)
        }
    }
}
