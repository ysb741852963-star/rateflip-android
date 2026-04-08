package com.rateflip.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rateflip.app.ui.screens.converter.ConverterScreen
import com.rateflip.app.ui.screens.settings.SettingsScreen

/**
 * RateFlip 应用导航
 */
sealed class Screen(val route: String) {
    object Converter : Screen("converter")
    object Settings : Screen("settings")
}

@Composable
fun RateFlipApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Converter.route
    ) {
        composable(Screen.Converter.route) {
            ConverterScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
