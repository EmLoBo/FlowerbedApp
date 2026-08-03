package pl.flowerbedapp.feature.projectdetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.flowerbedapp.ui.components.EmptyState
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.components.LoadingState
import pl.flowerbedapp.ui.components.PlantCard
import pl.flowerbedapp.ui.theme.FlowerbedTheme
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    onPlantClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel(),
) {
    val project by viewModel.project.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val current = project

    Scaffold(
        topBar = { FlowerbedTopBar(title = current?.name ?: "Project", onBack = onBack) },
        containerColor = FlowerbedTheme.colors.background,
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when {
            current == null -> LoadingState(modifier = contentModifier)

            current.plants.isEmpty() -> EmptyState(
                message  = "No plants in this project yet\nOpen a plant and tap ♥ to add it",
                modifier = contentModifier,
            )

            else -> LazyColumn(
                modifier       = contentModifier.navigationBarsPadding(),
                contentPadding = PaddingValues(bottom = Spacing.xl),
            ) {
                if (current.description.isNotBlank()) {
                    item {
                        Text(
                            text     = current.description,
                            style    = FlowerbedType.bodyMedium,
                            color    = FlowerbedTheme.colors.textSecondary,
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        )
                    }
                }
                items(current.plants, key = { it.id }) { plant ->
                    PlantCard(
                        plant      = plant,
                        onClick    = { onPlantClick(plant.id) },
                        isFavorite = plant.id in favoriteIds,
                        modifier   = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                    )
                }
            }
        }
    }
}
