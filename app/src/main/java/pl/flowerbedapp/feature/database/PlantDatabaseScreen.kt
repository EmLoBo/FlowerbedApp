package pl.flowerbedapp.feature.database

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.flowerbedapp.ui.components.EmptyState
import pl.flowerbedapp.ui.components.ErrorState
import pl.flowerbedapp.ui.components.LoadingState
import pl.flowerbedapp.ui.components.PlantCard
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDatabaseScreen(
    onPlantClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: PlantDatabaseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plant Database", style = FlowerbedType.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = FlowerbedColors.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = FlowerbedColors.BackgroundDark,
                    titleContentColor = FlowerbedColors.TextPrimary,
                ),
            )
        },
        containerColor = FlowerbedColors.BackgroundDark,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = Spacing.xl),
        ) {
            item {
                OutlinedTextField(
                    value         = "",
                    onValueChange = viewModel::onQueryChanged,
                    modifier      = Modifier.fillMaxWidth().padding(Spacing.md),
                    placeholder   = { Text("Search all plants…", color = FlowerbedColors.TextSecondary) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = FlowerbedColors.GardenGreen) },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = FlowerbedColors.GardenGreen,
                        unfocusedBorderColor = FlowerbedColors.SurfaceElevated,
                        focusedTextColor     = FlowerbedColors.TextPrimary,
                        unfocusedTextColor   = FlowerbedColors.TextPrimary,
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
                    ErrorState(state.error!!, modifier = Modifier.fillMaxWidth().height(200.dp))
                }
                state.plants.isEmpty() -> item {
                    EmptyState("No plants found", modifier = Modifier.fillMaxWidth().height(200.dp))
                }
                else -> items(state.plants, key = { it.id }) { plant ->
                    PlantCard(
                        plant    = plant,
                        onClick  = { onPlantClick(plant.id) },
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                    )
                }
            }
        }
    }
}
