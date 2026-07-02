package pl.flowerbedapp.feature.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.ui.components.ErrorState
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.components.GardenChip
import pl.flowerbedapp.ui.components.LoadingState
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedTheme
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    onBack: () -> Unit,
    viewModel: PlantDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FlowerbedTopBar(
                title  = state.plant?.displayName ?: "Plant Detail",
                onBack = onBack,
                actions = {
                    if (state.plant != null) {
                        IconButton(onClick = { viewModel.saveToProject(1L) }) {
                            Icon(
                                Icons.Default.Favorite,
                                "Save",
                                tint = if (state.savedToProject) FlowerbedColors.GardenGreen else FlowerbedTheme.colors.textSecondary,
                            )
                        }
                    }
                },
            )
        },
        containerColor = FlowerbedTheme.colors.background,
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            )
            state.error != null -> ErrorState(
                message  = state.error!!,
                onRetry  = viewModel::load,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            state.plant != null -> PlantDetailContent(
                plant    = state.plant!!,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun PlantDetailContent(plant: Plant, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        // Hero image
        AsyncImage(
            model            = plant.imageUrl,
            contentDescription = plant.displayName,
            contentScale     = ContentScale.Crop,
            modifier         = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        )

        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(plant.displayName, style = FlowerbedType.displayLarge, color = FlowerbedTheme.colors.textPrimary)
            Text(plant.scientificName, style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)

            plant.familyCommonName?.let {
                Spacer(Modifier.height(Spacing.sm))
                GardenChip(label = "Family: $it")
            }

            plant.genus?.let {
                Spacer(Modifier.height(Spacing.xs))
                GardenChip(label = "Genus: $it")
            }

            // Growth conditions
            plant.growth?.let { growth ->
                Spacer(Modifier.height(Spacing.lg))
                Text("Growing conditions", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
                Spacer(Modifier.height(Spacing.sm))

                growth.phMinimum?.let { min ->
                    growth.phMaximum?.let { max ->
                        InfoRow("🧪", "Soil pH", "$min – $max")
                    }
                }
                growth.light?.let { InfoRow("☀️", "Light (0–10)", it.toString()) }
                growth.atmosphericHumidity?.let { InfoRow("💧", "Humidity", "$it%") }
                growth.soilTexture?.let { InfoRow("🌍", "Soil texture (0–10)", it.toString()) }
                growth.rowSpacingCm?.let { InfoRow("📏", "Row spacing", "${it.toInt()} cm") }
                growth.spreadCm?.let { InfoRow("↔️", "Spread", "${it.toInt()} cm") }

                if (growth.bloomMonths.isNotEmpty()) {
                    Spacer(Modifier.height(Spacing.sm))
                    Text("🌸 Blooms:", style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)
                    Text(growth.bloomMonths.joinToString(", "), color = FlowerbedTheme.colors.textPrimary, style = FlowerbedType.bodyMedium)
                }
                if (growth.growthMonths.isNotEmpty()) {
                    Spacer(Modifier.height(Spacing.xs))
                    Text("🌿 Growing:", style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)
                    Text(growth.growthMonths.joinToString(", "), color = FlowerbedTheme.colors.textPrimary, style = FlowerbedType.bodyMedium)
                }
            }

            // Specifications
            plant.specifications?.let { spec ->
                Spacer(Modifier.height(Spacing.lg))
                Text("Specifications", style = FlowerbedType.titleMedium, color = FlowerbedColors.GardenGreen)
                Spacer(Modifier.height(Spacing.sm))
                spec.averageHeightCm?.let { InfoRow("📐", "Average height", "${it.toInt()} cm") }
                spec.maximumHeightCm?.let { InfoRow("⬆️", "Maximum height", "${it.toInt()} cm") }
                spec.lifespan?.let { InfoRow("♻️", "Lifespan", it) }
                spec.toxicity?.let { if (it.isNotBlank()) InfoRow("⚠️", "Toxicity", it) }
            }
        }
    }
}

@Composable
private fun InfoRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, style = FlowerbedType.bodyMedium)
        Spacer(Modifier.width(Spacing.sm))
        Text(label, style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary, modifier = Modifier.weight(1f))
        Text(value, style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textPrimary)
    }
}
