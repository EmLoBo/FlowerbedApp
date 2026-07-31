package pl.flowerbedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import pl.flowerbedapp.feature.settings.SettingsViewModel
import pl.flowerbedapp.ui.navigation.FlowerbedNavHost
import pl.flowerbedapp.ui.theme.FlowerbedTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val darkPref by settingsViewModel.darkTheme.collectAsStateWithLifecycle()
            // No stored choice yet → follow the system setting
            val darkTheme = darkPref ?: isSystemInDarkTheme()

            FlowerbedTheme(darkTheme = darkTheme) {
                // Transparent status bar; icon tint follows the active theme
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color     = Color.Transparent,
                        darkIcons = !darkTheme,
                    )
                }

                val navController = rememberNavController()
                FlowerbedNavHost(navController = navController)
            }
        }
    }
}
