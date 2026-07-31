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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
        colors    = CardDefaults.cardColors(containerColor = FlowerbedTheme.colors.surfaceElevated),
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
                                listOf(Color.Transparent, FlowerbedTheme.colors.surfaceElevated),
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
                    color    = FlowerbedTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text     = plant.scientificName,
                    style    = FlowerbedType.bodyMedium,
                    color    = FlowerbedTheme.colors.textSecondary,
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
                containerColor         = FlowerbedTheme.colors.surfaceElevated,
                selectedContainerColor = FlowerbedColors.GardenGreen.copy(alpha = 0.3f),
                labelColor             = FlowerbedTheme.colors.textSecondary,
                selectedLabelColor     = FlowerbedColors.GardenGreen,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled              = true,
                selected             = selected,
                borderColor          = FlowerbedTheme.colors.surfaceElevated,
                selectedBorderColor  = FlowerbedColors.GardenGreen,
            ),
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(FlowerbedTheme.colors.surfaceElevated)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        ) {
            Text(label, style = FlowerbedType.labelSmall, color = FlowerbedTheme.colors.textSecondary)
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

    // Proportions are fractions of the canvas height (they match the original 4/18/9 dp at
    // 72.dp) so the same plant renders correctly at any size — logo, loader, empty state.
    val stroke  = size.height * 0.055f
    val leafLen = size.height * 0.25f
    val leafWid = size.height * 0.125f

    // Stem grows upward; its tip is always where the top leaf sits
    drawLine(
        color       = FlowerbedColors.GardenGreen,
        start       = Offset(centerX, baseY),
        end         = Offset(centerX, stemTopY),
        strokeWidth = stroke,
        cap         = StrokeCap.Round,
    )

    // Lower leaf — right side, fixed partway up the stem
    growingLeaf(centerX, baseY - maxStem * 0.45f, growth, 0.45f, -40f,  FlowerbedColors.GardenGreen, leafLen, leafWid)
    // 3-petal head riding the growing tip
    growingLeaf(centerX, stemTopY, growth, 0.62f, -140f, FlowerbedColors.GardenGreenLight, leafLen, leafWid)
    growingLeaf(centerX, stemTopY, growth, 0.62f, -40f,  FlowerbedColors.GardenGreenLight, leafLen, leafWid)
    growingLeaf(centerX, stemTopY, growth, 0.62f, -90f,  FlowerbedColors.GardenGreenLight, leafLen, leafWid)
}

/**
 * "Flowerbed" wordmark where the flower stands in for the letter "l".
 * The flower is sized from the text style, so it scales with the typography.
 */
@Composable
fun FlowerbedWordmark(
    modifier: Modifier = Modifier,
    style: TextStyle = FlowerbedType.headlineMedium,
    color: Color = FlowerbedColors.TextPrimary,
) {
    val letterHeight = with(LocalDensity.current) { style.fontSize.toDp() }

    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text("F", style = style, color = color)
        Canvas(
            modifier = Modifier
                // lifted slightly so the stem sits on the text baseline, not the descender line
                .padding(bottom = letterHeight * 0.12f)
                .size(width = letterHeight * 0.55f, height = letterHeight),
        ) { drawPlant(growth = 1f) }
        Text("owerbed", style = style, color = color)
    }
}

@Composable
fun FlowerbedLogo(modifier: Modifier = Modifier, size: Dp = 36.dp) {
    Row(
        modifier              = modifier,
        verticalAlignment     = Alignment.Bottom,   // all three grow from the same ground line
        horizontalArrangement = Arrangement.spacedBy(size * 0.08f),
    ) {
        Canvas(Modifier.size(size * 0.62f)) { drawPlant(growth = 1f) }
        Canvas(Modifier.size(size))         { drawPlant(growth = 1f) }
        Canvas(Modifier.size(size * 0.78f)) { drawPlant(growth = 1f) }
    }
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
    leafLen: Float,
    leafWid: Float,
) {
    if (growth < appearAt) return
    val scale = ((growth - appearAt) / 0.25f).coerceIn(0f, 1f)
    val leaf  = leafPath(leafLen * scale, leafWid * scale)

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
                        Text("Try again", color = Color.Black)
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
            Text(message, color = FlowerbedTheme.colors.textSecondary, style = FlowerbedType.bodyMedium)
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
                    tint = FlowerbedTheme.colors.textPrimary,
                )
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor    = FlowerbedTheme.colors.background,
            titleContentColor = FlowerbedTheme.colors.textPrimary,
        ),
    )
}

@Composable
fun IconTextRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    tint: Color = FlowerbedTheme.colors.textSecondary,
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

@Preview(name = "Logo", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun FlowerbedLogoPreview() {
    FlowerbedTheme {
        Row(
            modifier              = Modifier.padding(Spacing.md),
            verticalAlignment     = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            FlowerbedLogo(size = 36.dp)   // header size
            FlowerbedLogo(size = 72.dp)   // splash size
        }
    }
}

@Preview(name = "Wordmark", showBackground = true, backgroundColor = 0xFF0F0F0F)
@Composable
private fun FlowerbedWordmarkPreview() {
    FlowerbedTheme {
        Column(
            modifier          = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FlowerbedWordmark(style = FlowerbedType.titleMedium)
            FlowerbedWordmark(style = FlowerbedType.headlineMedium)
            FlowerbedWordmark(style = FlowerbedType.displayLarge)
        }
    }
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
