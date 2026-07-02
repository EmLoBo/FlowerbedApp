package pl.flowerbedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import pl.flowerbedapp.ui.navigation.FlowerbedNavHost
import pl.flowerbedapp.ui.theme.FlowerbedColors
import pl.flowerbedapp.ui.theme.FlowerbedTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FlowerbedTheme {
                // Transparent status bar; icon tint follows the theme (dark icons in light mode)
                val darkTheme = isSystemInDarkTheme()
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color         = FlowerbedColors.BackgroundDark.copy(alpha = 0f),
                        darkIcons     = !darkTheme,
                    )
                }

                val navController = rememberNavController()
                FlowerbedNavHost(navController = navController)
            }
        }
    }
}
