package pl.flowerbedapp.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import pl.flowerbedapp.ui.components.EmptyState
import pl.flowerbedapp.ui.components.ErrorState
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.components.LoadingState
import pl.flowerbedapp.ui.components.PlantCard
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedTheme
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSearchScreen(
    onPlantClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: PlantSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layout    = listState.layoutInfo
            val lastIndex = layout.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastIndex to layout.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (total > 0 && lastVisible >= total - 3) viewModel.loadMore()
            }
    }

    Scaffold(
        topBar = { FlowerbedTopBar(title = "Find Plants", onBack = onBack) },
        containerColor = FlowerbedTheme.colors.background,
    ) { innerPadding ->
        LazyColumn(
            state    = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = Spacing.xl),
        ) {

            // ── Filter panel ───────────────────────────────────────────────
            item {
                FilterPanel(
                    state       = state,
                    onQueryChanged   = viewModel::onQueryChanged,
                    onPhChanged      = viewModel::onPhChanged,
                    onGpsClick       = viewModel::useDeviceLocation,
                    onSearchClick    = viewModel::search,
                )
            }

            // ── Results ────────────────────────────────────────────────────
            when {
                state.isLoading -> item {
                    LoadingState(modifier = Modifier.fillMaxWidth().height(200.dp))
                }
                state.error != null -> item {
                    ErrorState(
                        message  = state.error!!,
                        onRetry  = viewModel::search,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                }
                state.plants.isEmpty() -> item {
                    EmptyState(
                        message  = "No plants found\nTry adjusting your filters",
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                }
                else -> {
                    items(state.plants, key = { it.id }) { plant ->
                        PlantCard(
                            plant      = plant,
                            onClick    = { onPlantClick(plant.id) },
                            isFavorite = plant.id in favoriteIds,
                            modifier   = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                        )
                    }
                    if (state.isLoadingMore) {
                        item { LoadingState(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterPanel(
    state: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onPhChanged: (Float, Float) -> Unit,
    onGpsClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FlowerbedTheme.colors.surface, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {

        // Search field
        OutlinedTextField(
            value         = state.query,
            onValueChange = onQueryChanged,
            modifier      = Modifier.fillMaxWidth(),
            placeholder   = { Text("Search plants…", color = FlowerbedTheme.colors.textSecondary) },
            leadingIcon   = { Icon(Icons.Default.Search, null, tint = FlowerbedColors.GardenGreen) },
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = FlowerbedColors.GardenGreen,
                unfocusedBorderColor = FlowerbedTheme.colors.surfaceElevated,
                focusedTextColor     = FlowerbedTheme.colors.textPrimary,
                unfocusedTextColor   = FlowerbedTheme.colors.textPrimary,
                cursorColor          = FlowerbedColors.GardenGreen,
            ),
            shape         = RoundedCornerShape(12.dp),
            singleLine    = true,
        )

        // pH range
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Soil pH", style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)
                Text(
                    "${"%.1f".format(state.phMin)} – ${"%.1f".format(state.phMax)}",
                    style = FlowerbedType.bodyMedium,
                    color = FlowerbedColors.GardenGreen,
                )
            }
            RangeSlider(
                value         = state.phMin..state.phMax,
                onValueChange = { range -> onPhChanged(range.start, range.endInclusive) },
                valueRange    = 0f..14f,
                steps         = 27,
                colors        = SliderDefaults.colors(
                    thumbColor        = FlowerbedColors.GardenGreen,
                    activeTrackColor  = FlowerbedColors.GardenGreen,
                    inactiveTrackColor = FlowerbedTheme.colors.surfaceElevated,
                ),
            )
        }

        // Location row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = when {
                    state.usingDeviceLocation -> "📍 Device GPS"
                    state.latitude != null    -> "📍 Manual: ${"%.4f".format(state.latitude)}, ${"%.4f".format(state.longitude)}"
                    else                      -> "📍 No location"
                },
                style = FlowerbedType.bodyMedium,
                color = if (state.latitude != null) FlowerbedColors.GardenGreen else FlowerbedTheme.colors.textSecondary,
            )
            IconButton(onClick = onGpsClick) {
                Icon(Icons.Default.GpsFixed, "Use GPS", tint = FlowerbedColors.GardenGreen)
            }
        }

        // Search button
        Button(
            onClick  = onSearchClick,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(containerColor = FlowerbedColors.GardenGreen),
            shape    = RoundedCornerShape(12.dp),
        ) {
            Text("🌱 Search Plants", color = Color.Black, style = FlowerbedType.titleMedium)
        }
    }
}
