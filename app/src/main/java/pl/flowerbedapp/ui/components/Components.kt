package pl.flowerbedapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = FlowerbedColors.SurfaceElevated),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(modifier = Modifier.height(100.dp)) {
            // Plant image
            Box(modifier = Modifier.size(100.dp)) {
                AsyncImage(
                    model             = plant.imageUrl,
                    contentDescription = plant.displayName,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.matchParentSize(),
                )
                // Side gradient for text legibility
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, FlowerbedColors.SurfaceElevated),
                                startX = 60f,
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text     = plant.displayName,
                    style    = FlowerbedType.titleMedium,
                    color    = FlowerbedColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text     = plant.scientificName,
                    style    = FlowerbedType.bodyMedium,
                    color    = FlowerbedColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                plant.familyCommonName?.let { family ->
                    Spacer(Modifier.height(Spacing.xs))
                    GardenChip(label = family)
                }
            }
        }
    }
}

@Composable
fun GardenChip(
    label: String,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (onClick != null) {
        FilterChip(
            selected  = selected,
            onClick   = onClick,
            label     = { Text(label, style = FlowerbedType.labelSmall) },
            modifier  = modifier,
            colors    = FilterChipDefaults.filterChipColors(
                containerColor         = FlowerbedColors.SurfaceElevated,
                selectedContainerColor = FlowerbedColors.GardenGreen.copy(alpha = 0.3f),
                labelColor             = FlowerbedColors.TextSecondary,
                selectedLabelColor     = FlowerbedColors.GardenGreen,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled              = true,
                selected             = selected,
                borderColor          = FlowerbedColors.SurfaceElevated,
                selectedBorderColor  = FlowerbedColors.GardenGreen,
            ),
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(FlowerbedColors.SurfaceElevated)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        ) {
            Text(label, style = FlowerbedType.labelSmall, color = FlowerbedColors.TextSecondary)
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = FlowerbedColors.GardenGreen)
    }
}

@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text  = "⚠️ $message",
            color = MaterialTheme.colorScheme.error,
            style = FlowerbedType.bodyMedium,
        )
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌱", style = FlowerbedType.displayLarge)
            Spacer(Modifier.height(Spacing.sm))
            Text(message, color = FlowerbedColors.TextSecondary, style = FlowerbedType.bodyMedium)
        }
    }
}

@Composable
fun IconTextRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    tint: Color = FlowerbedColors.TextSecondary,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text(text = text, style = FlowerbedType.bodyMedium, color = tint)
    }
}
