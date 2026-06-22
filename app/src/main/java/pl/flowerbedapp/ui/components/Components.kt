package pl.flowerbedapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedTheme
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
    val transition = rememberInfiniteTransition(label = "loading")
    val growth by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "growth",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(72.dp)) { drawPlant(growth) }
    }
}

// Draws the flowerbed mascot: a stem with one side leaf and a 3-petal head at the tip.
// `growth` 0→1 controls how far the stem has grown and how far the leaves have unfurled;
// pass 1f for a fully-grown, static plant.
private fun DrawScope.drawPlant(growth: Float) {
    val centerX  = size.width / 2f
    val baseY    = size.height
    val maxStem  = size.height * 0.62f
    val stemTopY = baseY - maxStem * growth

    // Stem grows upward; its tip is always where the top leaf sits
    drawLine(
        color       = FlowerbedColors.GardenGreen,
        start       = Offset(centerX, baseY),
        end         = Offset(centerX, stemTopY),
        strokeWidth = 4.dp.toPx(),
        cap         = StrokeCap.Round,
    )

    // Lower leaf — right side, fixed partway up the stem
    growingLeaf(centerX, baseY - maxStem * 0.45f, growth, appearAt = 0.45f, angleDeg = -40f,  color = FlowerbedColors.GardenGreen)
    // 3-petal head riding the growing tip
    growingLeaf(centerX, stemTopY, growth, appearAt = 0.62f, angleDeg = -140f, color = FlowerbedColors.GardenGreenLight)
    growingLeaf(centerX, stemTopY, growth, appearAt = 0.62f, angleDeg = -40f,  color = FlowerbedColors.GardenGreenLight)
    growingLeaf(centerX, stemTopY, growth, appearAt = 0.62f, angleDeg = -90f,  color = FlowerbedColors.GardenGreenLight)
}

// Almond-shaped leaf pointing along +x; shared by the loading and error plants.
private fun leafPath(len: Float, wid: Float) = Path().apply {
    moveTo(0f, 0f)
    quadraticTo(len * 0.5f, -wid, len, 0f)
    quadraticTo(len * 0.5f,  wid, 0f, 0f)
    close()
}

private fun DrawScope.growingLeaf(
    centerX: Float,
    attachY: Float,
    growth: Float,
    appearAt: Float,
    angleDeg: Float,
    color: Color,
) {
    if (growth < appearAt) return
    val scale = ((growth - appearAt) / 0.25f).coerceIn(0f, 1f)
    val leaf  = leafPath(18.dp.toPx() * scale, 9.dp.toPx() * scale)

    withTransform({
        translate(centerX, attachY)
        rotate(angleDeg, pivot = Offset.Zero)
    }) {
        drawPath(leaf, color = color)
    }
}

@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier          = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WiltingPlant(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(40.dp),
            )
            Spacer(Modifier.width(Spacing.md))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text  = message,
                    color = MaterialTheme.colorScheme.error,
                    style = FlowerbedType.bodyMedium,
                )
                if (onRetry != null) {
                    Spacer(Modifier.height(Spacing.md))
                    Button(
                        onClick = onRetry,
                        colors  = ButtonDefaults.buttonColors(containerColor = FlowerbedColors.GardenGreen),
                    ) {
                        Text("Try again", color = FlowerbedColors.BackgroundDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun WiltingPlant(modifier: Modifier = Modifier) {
    val fall = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fall.animateTo(1f, animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier) {
        val stemX = size.width - 2.dp.toPx()

        // Bare stem stays as a vertical rule for the message + button
        drawLine(
            color       = FlowerbedColors.GardenGreen,
            start       = Offset(stemX, 0f),
            end         = Offset(stemX, size.height),
            strokeWidth = 3.dp.toPx(),
            cap         = StrokeCap.Round,
        )

        // Leaves on the left side drop off one after another, then vanish
        fallingLeaf(stemX, size.height * 0.20f, angleDeg = -140f, progress = stagger(fall.value, 0.00f), color = FlowerbedColors.GardenGreenLight)
        fallingLeaf(stemX, size.height * 0.20f, angleDeg = -90f, progress = stagger(fall.value, 0.12f), color = FlowerbedColors.GardenGreenLight)
        fallingLeaf(stemX, size.height * 0.20f, angleDeg = -40f, progress = stagger(fall.value, 0.24f), color = FlowerbedColors.GardenGreenLight)
        fallingLeaf(stemX, size.height * 0.60f, angleDeg = -140f, progress = stagger(fall.value, 0.40f), color = FlowerbedColors.GardenGreen)
    }
}

private fun stagger(t: Float, start: Float) = ((t - start) / 0.5f).coerceIn(0f, 1f)

private fun DrawScope.fallingLeaf(
    baseX: Float,
    attachY: Float,
    angleDeg: Float,
    progress: Float,
    color: Color,
) {
    val leaf  = leafPath(18.dp.toPx(), 9.dp.toPx())
    val dropY = attachY + progress * 24.dp.toPx()
    withTransform({
        translate(baseX, dropY)
        rotate(angleDeg + progress * 120f, pivot = Offset.Zero)
    }) {
        drawPath(leaf, color = color.copy(alpha = 1f - progress))
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(72.dp)) { drawPlant(growth = 1f) }
            Spacer(Modifier.height(Spacing.sm))
            Text(message, color = FlowerbedColors.TextSecondary, style = FlowerbedType.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowerbedTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title    = { Text(title, style = FlowerbedType.headlineMedium) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FlowerbedColors.TextPrimary,
                )
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor    = FlowerbedColors.BackgroundDark,
            titleContentColor = FlowerbedColors.TextPrimary,
        ),
    )
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

// ─── Previews ───────────────────────────────────────────────────────────────
// Wrapped in FlowerbedTheme so the dark palette renders correctly in the IDE.

private val previewPlant = Plant(
    id               = 1,
    slug             = "rosa-rugosa",
    commonName       = "Beach Rose",
    scientificName   = "Rosa rugosa",
    imageUrl         = null,
    familyCommonName = "Rose family",
    genus            = "Rosa",
    specifications   = null,
    growth           = null,
)

@Preview(name = "TopBar", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun FlowerbedTopBarPreview() {
    FlowerbedTheme { FlowerbedTopBar(title = "Find Plants", onBack = {}) }
}

@Preview(name = "PlantCard", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun PlantCardPreview() {
    FlowerbedTheme {
        PlantCard(plant = previewPlant, onClick = {}, modifier = Modifier.padding(Spacing.md))
    }
}

@Preview(name = "GardenChip", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun GardenChipPreview() {
    FlowerbedTheme {
        Row(
            modifier              = Modifier.padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            GardenChip(label = "Loamy")
            GardenChip(label = "Full sun", selected = true, onClick = {})
        }
    }
}

@Preview(name = "ErrorState (retry)", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun ErrorStatePreview() {
    FlowerbedTheme {
        ErrorState(
            message  = "Something went wrong",
            onRetry  = {},
            modifier = Modifier.fillMaxWidth().height(200.dp),
        )
    }
}

@Preview(name = "EmptyState", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun EmptyStatePreview() {
    FlowerbedTheme {
        EmptyState(message = "No plants found", modifier = Modifier.fillMaxWidth().height(200.dp))
    }
}

@Preview(name = "LoadingState", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun LoadingStatePreview() {
    FlowerbedTheme {
        LoadingState(modifier = Modifier.fillMaxWidth().height(200.dp))
    }
}
