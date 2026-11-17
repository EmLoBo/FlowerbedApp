package pl.flowerbedapp.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import pl.flowerbedapp.core.domain.model.Moisture
import pl.flowerbedapp.core.domain.model.SoilType
import pl.flowerbedapp.core.domain.model.SunExposure
import pl.flowerbedapp.ui.components.EmptyState
import pl.flowerbedapp.ui.components.ErrorState
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.components.GardenChip
import pl.flowerbedapp.ui.components.LoadingState
import pl.flowerbedapp.ui.components.PlantCard
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedTheme
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSearchScreen(
    onPlantClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: PlantSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
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
                    state             = state,
                    query             = query,
                    onQueryChanged    = viewModel::onQueryChanged,
                    onPhChanged       = viewModel::onPhChanged,
                    onSunHoursChanged = viewModel::onSunHoursChanged,
                    onSliderReleased  = viewModel::onSliderReleased,
                    onSoilTypeChanged = viewModel::onSoilTypeChanged,
                    onMoistureChanged = viewModel::onMoistureChanged,
                    onClearFilters    = viewModel::clearFilters,
                    onGpsClick        = viewModel::useDeviceLocation,
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
    query: String,
    onQueryChanged: (String) -> Unit,
    onPhChanged: (Float, Float) -> Unit,
    onSunHoursChanged: (Int?) -> Unit,
    onSliderReleased: () -> Unit,
    onSoilTypeChanged: (SoilType?) -> Unit,
    onMoistureChanged: (Moisture?) -> Unit,
    onClearFilters: () -> Unit,
    onGpsClick: () -> Unit,
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
            value         = query,
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

        // Soil type
        Column {
            FilterHeader("Soil")
            ChoiceChips(
                options  = SoilType.entries,
                selected = state.soilType,
                label    = { "${it.emoji} ${it.label}" },
                onSelect = onSoilTypeChanged,
            )
        }

        // Soil moisture
        Column {
            FilterHeader("Soil moisture")
            ChoiceChips(
                options  = Moisture.entries,
                selected = state.moisture,
                label    = { "${it.emoji} ${it.label}" },
                onSelect = onMoistureChanged,
            )
        }

        // Sun hours — a single value: how many hours of direct sun the bed gets
        Column {
            val hours = state.sunHours
            FilterHeader(
                title = "Sun",
                value = if (hours == null) "Any"
                        else "$hours h · ${SunExposure.forHours(hours).let { "${it.emoji} ${it.label}" }}",
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                GardenChip(
                    label    = "Any",
                    selected = hours == null,
                    // Same as releasing the slider: commit the change and search
                    onClick  = { if (hours != null) { onSunHoursChanged(null); onSliderReleased() } },
                )
                Spacer(Modifier.width(Spacing.sm))
                Slider(
                    value                 = (hours ?: 6).toFloat(),
                    onValueChange         = { onSunHoursChanged(it.roundToInt()) },
                    onValueChangeFinished = onSliderReleased,
                    valueRange            = 0f..SUN_SLIDER_MAX.toFloat(),
                    steps                 = SUN_SLIDER_MAX - 1,
                    modifier              = Modifier.weight(1f),
                    colors                = gardenSliderColors(active = hours != null),
                )
            }
        }

        // Soil pH
        Column {
            FilterHeader(
                title = "Soil pH",
                value = if (state.phFilterOn) "${"%.1f".format(state.phMin)} – ${"%.1f".format(state.phMax)}" else "Any",
            )
            RangeSlider(
                value                 = state.phMin..state.phMax,
                onValueChange         = { range -> onPhChanged(range.start, range.endInclusive) },
                onValueChangeFinished = onSliderReleased,
                valueRange            = PH_SLIDER_RANGE,
                steps                 = 10,   // 0.5 pH per step
                colors                = gardenSliderColors(active = state.phFilterOn),
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

        if (state.hasActiveFilters) {
            TextButton(onClick = onClearFilters, modifier = Modifier.align(Alignment.End)) {
                Text("Clear filters", color = FlowerbedColors.GardenGreen, style = FlowerbedType.bodyMedium)
            }
        }
    }
}

@Composable
private fun FilterHeader(title: String, value: String? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = FlowerbedType.bodyMedium, color = FlowerbedTheme.colors.textSecondary)
        if (value != null) Text(value, style = FlowerbedType.bodyMedium, color = FlowerbedColors.GardenGreen)
    }
}

/** Single-choice chip row; tapping the selected chip again clears the filter. */
@Composable
private fun <T> ChoiceChips(
    options: List<T>,
    selected: T?,
    label: (T) -> String,
    onSelect: (T?) -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        options.forEach { option ->
            GardenChip(
                label    = label(option),
                selected = option == selected,
                onClick  = { onSelect(if (option == selected) null else option) },
            )
        }
    }
}

/** Green when the filter is on, faded when the slider sits at "any". */
@Composable
private fun gardenSliderColors(active: Boolean) = SliderDefaults.colors(
    thumbColor         = FlowerbedColors.GardenGreen.copy(alpha = if (active) 1f else 0.4f),
    activeTrackColor   = FlowerbedColors.GardenGreen.copy(alpha = if (active) 1f else 0.4f),
    inactiveTrackColor = FlowerbedTheme.colors.surfaceElevated,
)
