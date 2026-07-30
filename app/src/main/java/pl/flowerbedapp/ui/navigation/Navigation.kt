package pl.flowerbedapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pl.flowerbedapp.feature.database.PlantDatabaseScreen
import pl.flowerbedapp.feature.detail.PlantDetailScreen
import pl.flowerbedapp.feature.main.MainScreen
import pl.flowerbedapp.feature.projectdetail.ProjectDetailScreen
import pl.flowerbedapp.feature.projects.MyProjectsScreen
import pl.flowerbedapp.feature.search.PlantSearchScreen
import pl.flowerbedapp.feature.settings.SettingsScreen
import pl.flowerbedapp.feature.splash.SplashScreen
import pl.flowerbedapp.feature.weather.WeatherScreen

sealed class Screen(val route: String) {
    data object Splash       : Screen("splash")
    data object Main         : Screen("main")
    data object PlantSearch  : Screen("plant_search")
    data object PlantDatabase: Screen("plant_database")
    data object MyProjects   : Screen("my_projects")
    data object Weather      : Screen("weather")
    data object Settings     : Screen("settings")
    data object PlantDetail  : Screen("plant_detail/{plantId}") {
        fun route(plantId: Int) = "plant_detail/$plantId"
    }
    data object ProjectDetail : Screen("project_detail/{projectId}") {
        fun route(projectId: Long) = "project_detail/$projectId"
    }
}

@Composable
fun FlowerbedNavHost(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route,
        enterTransition  = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) +
                    fadeIn(tween(300))
        },
        exitTransition   = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) },
        popExitTransition  = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) +
                    fadeOut(tween(300))
        },
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateTo = { screen -> navController.navigate(screen.route) },
            )
        }

        composable(Screen.PlantSearch.route) {
            PlantSearchScreen(
                onPlantClick = { id -> navController.navigate(Screen.PlantDetail.route(id)) },
                onBack       = { navController.navigateUp() },
            )
        }

        composable(Screen.PlantDatabase.route) {
            PlantDatabaseScreen(
                onPlantClick = { id -> navController.navigate(Screen.PlantDetail.route(id)) },
                onBack       = { navController.navigateUp() },
            )
        }

        composable(
            route     = Screen.PlantDetail.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType }),
        ) {
            PlantDetailScreen(onBack = { navController.navigateUp() })
        }

        composable(Screen.MyProjects.route) {
            MyProjectsScreen(
                onProjectClick = { id -> navController.navigate(Screen.ProjectDetail.route(id)) },
                onBack         = { navController.navigateUp() },
            )
        }

        composable(
            route     = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType }),
        ) {
            ProjectDetailScreen(
                onPlantClick = { id -> navController.navigate(Screen.PlantDetail.route(id)) },
                onBack       = { navController.navigateUp() },
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(onBack = { navController.navigateUp() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.navigateUp() })
        }
    }
}
