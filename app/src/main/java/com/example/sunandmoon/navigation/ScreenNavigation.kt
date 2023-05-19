package com.example.sunandmoon.navigation

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sunandmoon.ar.SunAR
import com.example.sunandmoon.ui.screens.*

// controls the navigation and communication of the different screens
@Composable
fun MultipleScreenNavigator(modifier: Modifier, packageManager: PackageManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "productionSelectionScreen") {
        val routeProductionSelectionScreen = "productionSelectionScreen"
        composable(routeProductionSelectionScreen) {
            ProductionSelectionScreen(
                modifier = modifier,
                navigateToShootInfoScreen = { shootId: Int -> navController.navigate("shootInfoScreen/$shootId")},
                navigateToNextBottomBar = { index: Int ->
                    bottomBarNavigation(index, navController)
                },
                navigateToCreateShootScreen = { parentProductionId: Int?, shootToEditId: Int? -> navController.navigate("createShootScreen/$parentProductionId/$shootToEditId") },
                navController = navController,
                currentScreenRoute = routeProductionSelectionScreen,
                goToAboutScreen = { navController.navigate("aboutScreen") },
            )
        }
        val routeShootInfoScreen = "shootInfoScreen/{shootId}"
        composable(routeShootInfoScreen) { backStackEntry ->
            val shootId: Int? = backStackEntry.arguments?.getString("shootId")?.toIntOrNull()
            // we can assume that shootId never will be null, so this if-check will always be true (which we want)
            if(shootId != null) {
                ShootInfoScreen(
                    modifier = modifier,
                    navigateBack = { navController.popBackStack("productionSelectionScreen", false) },
                    shootId = shootId,
                    navigateToCreateShootScreen = { parentProductionId: Int?, shootToEditId: Int? -> navController.navigate("createShootScreen/$parentProductionId/$shootToEditId") },
                    navController = navController,
                    currentScreenRoute = routeShootInfoScreen
                )
            }
        }
        composable("createShootScreen/{parentProductionId}/{shootToEditId}") { backStackEntry ->
            val parentProductionId: Int? = backStackEntry.arguments?.getString("parentProductionId")?.toIntOrNull()
            val shootToEditId: Int? = backStackEntry.arguments?.getString("shootToEditId")?.toIntOrNull()
            val routeToGoBackTo: String = if(shootToEditId != null) routeShootInfoScreen else routeProductionSelectionScreen

            CreateShootScreen(
                modifier = modifier,
                navigateBack = { navController.popBackStack(routeToGoBackTo, false) },
                parentProductionId = parentProductionId,
                shootToEditId = shootToEditId
            )
        }
        composable("tableScreen"){ backStackEntry ->
            TableScreen(
                modifier = modifier,
                navigateToNextBottomBar = { index: Int ->
                    bottomBarNavigation(index, navController)
                }
            )
        }
        composable("ARScreen"){ backStackEntry ->
            SunAR(
                modifier = modifier,
                navigateToNextBottomBar = { index: Int ->
                    bottomBarNavigation(index, navController)
                },
                packageManager = packageManager
            )
        }
        composable("aboutScreen"){
            AboutScreen(
                modifier = modifier,
                navigateBack = { navController.popBackStack("productionSelectionScreen", false) }
            )
        }
    }
}

fun bottomBarNavigation(index: Int, navController: NavController) {
    when (index) {
        0 -> navController.popBackStack("productionSelectionScreen", false)
        1 -> navController.navigate("ARScreen")
        2 -> navController.navigate("tableScreen")
    }
}