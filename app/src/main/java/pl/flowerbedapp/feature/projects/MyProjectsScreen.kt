package pl.flowerbedapp.feature.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.flowerbedapp.core.domain.model.Project
import pl.flowerbedapp.ui.components.EmptyState
import pl.flowerbedapp.ui.components.FlowerbedTopBar
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectsScreen(
    onBack: () -> Unit,
    viewModel: MyProjectsViewModel = hiltViewModel(),
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        NewProjectDialog(
            onConfirm = { name, desc ->
                viewModel.createProject(name, desc)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }

    Scaffold(
        topBar = { FlowerbedTopBar(title = "My Projects", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick          = { showDialog = true },
                containerColor   = FlowerbedColors.GardenGreen,
                contentColor     = FlowerbedColors.BackgroundDark,
            ) {
                Icon(Icons.Default.Add, "New project")
            }
        },
        containerColor = FlowerbedColors.BackgroundDark,
    ) { innerPadding ->
        if (projects.isEmpty()) {
            EmptyState(
                "No projects yet\nTap + to create your first flowerbed",
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier       = Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
                contentPadding = PaddingValues(   start  = Spacing.md,
                    top    = Spacing.md,
                    end    = Spacing.md,
                    bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(projects, key = { it.id }) { project ->
                    ProjectCard(project = project, onDelete = { viewModel.deleteProject(project.id) })
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = FlowerbedColors.SurfaceElevated),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, style = FlowerbedType.titleMedium, color = FlowerbedColors.TextPrimary)
                if (project.description.isNotBlank()) {
                    Text(project.description, style = FlowerbedType.bodyMedium, color = FlowerbedColors.TextSecondary)
                }
                Text(
                    "${project.plants.size} plants",
                    style = FlowerbedType.labelSmall,
                    color = FlowerbedColors.GardenGreen,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun NewProjectDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Flowerbed Project", color = FlowerbedColors.TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Project name") },
                    singleLine    = true,
                )
                OutlinedTextField(
                    value         = desc,
                    onValueChange = { desc = it },
                    label         = { Text("Description (optional)") },
                    maxLines      = 3,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick  = { if (name.isNotBlank()) onConfirm(name, desc) },
                enabled  = name.isNotBlank(),
            ) { Text("Create", color = FlowerbedColors.GardenGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = FlowerbedColors.SurfaceDark,
    )
}
