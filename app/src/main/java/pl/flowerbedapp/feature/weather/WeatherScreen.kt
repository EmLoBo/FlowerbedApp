package pl.flowerbedapp.feature.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.flowerbedapp.core.domain.model.AlertSeverity
import pl.flowerbedapp.core.domain.model.WeatherAlert
import pl.flowerbedapp.ui.components.ErrorState
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.components.LoadingState
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { FlowerbedTopBar(title = "Weather", onBack = onBack) },
        containerColor = FlowerbedColors.BackgroundDark,
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(modifier = Modifier.fillMaxSize().padding(innerPadding))
            state.error != null -> ErrorState(
                message  = state.error!!,
                onRetry  = viewModel::load,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            state.weather != null -> {
                val weather = state.weather!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    item {
                        Card(
                            shape  = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = FlowerbedColors.AlertInfo.copy(0.85f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(Spacing.lg)) {
                                Text("${weather.temperatureInt}°C", style = FlowerbedType.displayLarge, color = Color.White)
                                Text(weather.description, style = FlowerbedType.bodyMedium, color = Color.White.copy(0.8f))
                                Text(weather.locationName, style = FlowerbedType.bodyMedium, color = Color.White.copy(0.7f))
                                Spacer(Modifier.height(Spacing.sm))
                                Text("Feels like: ${weather.feelsLike.toInt()}°C", style = FlowerbedType.bodyMedium, color = Color.White.copy(0.8f))
                                Text("Humidity: ${weather.humidity}%", style = FlowerbedType.bodyMedium, color = Color.White.copy(0.8f))
                            }
                        }
                    }
                    if (weather.alerts.isNotEmpty()) {
                        item { Text("⚠️ Active Alerts", style = FlowerbedType.titleMedium, color = FlowerbedColors.TextPrimary) }
                        items(weather.alerts) { alert -> AlertCard(alert) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: WeatherAlert) {
    val color = when (alert.severity) {
        AlertSeverity.DANGER  -> FlowerbedColors.AlertDanger
        AlertSeverity.WARNING -> FlowerbedColors.AlertWarning
        AlertSeverity.INFO    -> FlowerbedColors.AlertInfo
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = color.copy(0.85f)),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text("${alert.type.emoji} ${alert.type.label}", style = FlowerbedType.titleMedium, color = Color.White)
            Spacer(Modifier.height(Spacing.xs))
            Text(alert.message, style = FlowerbedType.bodyMedium, color = Color.White.copy(0.9f))
        }
    }
}
