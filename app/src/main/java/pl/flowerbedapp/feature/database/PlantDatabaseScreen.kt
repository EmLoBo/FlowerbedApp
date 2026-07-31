package pl.flowerbedapp.feature.database

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
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
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDatabaseScreen(
    onPlantClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: PlantDatabaseViewModel = hiltViewModel(),
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
        topBar = { FlowerbedTopBar(title = "Plant Database", onBack = onBack) },
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
            item {
                OutlinedTextField(
                    value         = query,
                    onValueChange = viewModel::onQueryChanged,
                    modifier      = Modifier.fillMaxWidth().padding(Spacing.md),
                    placeholder   = { Text("Search all plants…", color = FlowerbedTheme.colors.textSecondary) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = FlowerbedColors.GardenGreen) },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = FlowerbedColors.GardenGreen,
                        unfocusedBorderColor = FlowerbedTheme.colors.surfaceElevated,
                        focusedTextColor     = FlowerbedTheme.colors.textPrimary,
                        unfocusedTextColor   = FlowerbedTheme.colors.textPrimary,
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    singleLine = true,
                )
            }

            when {
                state.isLoading -> item {
                    LoadingState(modifier = Modifier.fillMaxWidth().height(200.dp))
                }
                state.error != null -> item {
                    ErrorState(
                        message  = state.error!!,
                        onRetry  = viewModel::retry,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                }
                state.plants.isEmpty() -> item {
                    EmptyState("No plants found", modifier = Modifier.fillMaxWidth().height(200.dp))
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
