package pl.flowerbedapp.feature.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedTheme
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val darkPref by viewModel.darkTheme.collectAsStateWithLifecycle()
    val isDark = darkPref ?: isSystemInDarkTheme()

    Scaffold(
        topBar = { FlowerbedTopBar(title = "Settings", onBack = onBack) },
        containerColor = FlowerbedTheme.colors.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text("Appearance", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "Dark mode",
                    style    = FlowerbedType.bodyMedium,
                    color    = FlowerbedTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked         = isDark,
                    onCheckedChange = viewModel::setDarkTheme,
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = FlowerbedColors.GardenGreen,
                    ),
                )
            }
            HorizontalDivider(color = FlowerbedTheme.colors.surfaceElevated)

            Text("Account", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
            Text("Log in to unlock premium features: unlimited saved projects, AI plant recommendations, and personalized alerts.",
                style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)
            Button(onClick = { /* navigate to login */ }, colors = ButtonDefaults.buttonColors(containerColor = FlowerbedColors.GardenGreen)) {
                Text("Log in / Sign up", color = Color.Black)
            }
            HorizontalDivider(color = FlowerbedTheme.colors.surfaceElevated)
            Text("About", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
            Text("Flowerbed v1.0.0\nPlant data: Trefle.io\nWeather: Open-Meteo (CC BY 4.0)\nWarnings: Źródłem danych jest Instytut Meteorologii i Gospodarki Wodnej – Państwowy Instytut Badawczy\nDefault background: Polina Silivanova / Unsplash",
                style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)
        }
    }
}
