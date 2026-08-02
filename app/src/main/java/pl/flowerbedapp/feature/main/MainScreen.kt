package pl.flowerbedapp.feature.main

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import pl.flowerbedapp.R
import pl.flowerbedapp.core.domain.model.AlertSeverity
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.ui.components.FlowerbedWordmark
import pl.flowerbedapp.ui.navigation.Screen
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedType
import pl.flowerbedapp.ui.theme.Spacing

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    onNavigateTo: (Screen) -> Unit,
    onOpenProject: (Long) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    ) { results ->
        if (results.values.any { it }) viewModel.loadWeather()
        else viewModel.onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        // Only ask when we don't have it. Weather itself is loaded in MainViewModel.init,
        // and this effect re-runs whenever the screen re-enters composition (e.g. on back
        // navigation) — so it must not touch the already-loaded weather state.
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }


    val context = LocalContext.current
    // OpenDocument (not GetContent) so we can persist read access across app restarts
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
        }
        viewModel.setBackground(uri?.toString())
    }

    Box(modifier = Modifier.fillMaxSize()) {


        if (uiState.backgroundUri != null) {
            AsyncImage(
                model            = uiState.backgroundUri,
                contentDescription = null,
                contentScale     = ContentScale.Crop,
                // Stale/unreadable URI (e.g. picked before persistable access, or photo deleted)
                // falls back to the default instead of rendering nothing
                error            = painterResource(R.drawable.bg_lavender_default),
                modifier         = Modifier.fillMaxSize(),
            )
        } else {
            // Default background: lavender field (Polina Silivanova / Unsplash)
            Image(
                painter            = painterResource(R.drawable.bg_lavender_default),
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(FlowerbedColors.Translucent, Color.Transparent, FlowerbedColors.Translucent),
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FlowerbedWordmark(
                    style = FlowerbedType.headlineMedium,
                    color = Color.White,
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onNavigateTo(Screen.Settings) }) {
                    Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                }
                OutlinedButton(onClick = { /* login flow */ }) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text  = if (uiState.isLoggedIn) "My Account" else "Log in",
                        color = Color.White,
                    )
                }
            }

            LazyRow(
                modifier            = Modifier.fillMaxWidth(),
                contentPadding      = androidx.compose.foundation.layout.PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                uiState.favoritesProjectId?.let { favoritesId ->
                    item {
                        MainTabChip(
                            emoji   = "❤",
                            label   = "Favorites",
                            onClick = { onOpenProject(favoritesId) },
                        )
                    }
                }
                items(mainTabs) { tab ->
                    MainTabChip(
                        emoji   = tab.emoji,
                        label   = tab.label,
                        onClick = { onNavigateTo(tab.screen) },
                    )
                }
            }

            Spacer(Modifier.weight(1f))


            WeatherCard(
                weather  = uiState.weather,
                isLoading = uiState.isWeatherLoading,
                error    = uiState.weatherError,
                onClick  = { onNavigateTo(Screen.Weather) },
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(FlowerbedColors.Translucent)
                        .clickable { imagePicker.launch(arrayOf("image/*")) }
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Change background",
                            tint     = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(Spacing.xs))
                        Text("Change background", color = Color.White, style = FlowerbedType.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(80.dp)) // bottom nav clearance
        }
    }
}


@Composable
private fun WeatherCard(
    weather: Weather?,
    isLoading: Boolean,
    error: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alert = weather?.worstAlert
    val cardColor = when (alert?.severity) {
        AlertSeverity.DANGER  -> FlowerbedColors.AlertDanger.copy(alpha = 0.85f)
        AlertSeverity.WARNING -> FlowerbedColors.AlertWarning.copy(alpha = 0.85f)
        else                  -> FlowerbedColors.AlertInfo.copy(alpha = 0.85f)
    }

    Card(
        modifier  = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        when {
            isLoading -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            weather != null -> {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (alert != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(alert.type.emoji, fontSize = 22.sp)
                                Spacer(Modifier.width(Spacing.xs))
                                Text(
                                    alert.type.label,
                                    style    = FlowerbedType.titleMedium,
                                    color    = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Spacer(Modifier.height(Spacing.xs))
                            Text(alert.message, style = FlowerbedType.bodyMedium, color = Color.White)
                        } else {
                            Text(weather.description, style = FlowerbedType.titleMedium, color = Color.White)
                            Text(weather.locationName, style = FlowerbedType.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                        Spacer(Modifier.height(Spacing.xs))
                        Text("Humidity: ${weather.humidity}%", style = FlowerbedType.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text  = "${weather.temperatureInt}°C",
                            style = FlowerbedType.displayLarge,
                            color = Color.White,
                        )
                        Text(
                            text  = "Feels ${weather.feelsLike.toInt()}°C",
                            style = FlowerbedType.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                    }
                }
            }
            error != null -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.White)
                        Spacer(Modifier.width(Spacing.sm))
                        Text("Weather unavailable", color = Color.White, style = FlowerbedType.bodyMedium)
                    }
                }
            }
            else -> {
                Box(Modifier.fillMaxWidth().padding(Spacing.lg)){
                    Text("Tap to grant location access", color = Color.White)
                }
            }
        }
    }
}

private data class MainTab(val emoji: String, val label: String, val screen: Screen)

private val mainTabs = listOf(
    MainTab("🌱", "Find Plants",    Screen.PlantSearch),
    MainTab("📁", "My Projects",   Screen.MyProjects),
    MainTab("🌿", "Plant Database", Screen.PlantDatabase),
    MainTab("🌤", "Weather",       Screen.Weather),
)

@Composable
private fun MainTabChip(emoji: String, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(FlowerbedColors.Translucent)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 16.sp)
            Spacer(Modifier.width(Spacing.xs))
            Text(label, color = Color.White, style = FlowerbedType.bodyMedium)
        }
    }
}
