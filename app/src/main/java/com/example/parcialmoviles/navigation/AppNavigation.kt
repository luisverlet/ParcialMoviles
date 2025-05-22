package com.example.parcialmoviles.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.parcialmoviles.HomeScreen
import com.example.parcialmoviles.screens.LoginScreen
import com.example.parcialmoviles.screens.TaskCreationScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object TaskCreation : Screen("task_creation")
    object TaskEdit : Screen("task_creation/{taskId}") {
        fun createRoute(taskId: Int) = "task_creation/$taskId"
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.TaskCreation.route) {
            TaskCreationScreen(navController)
        }

        composable(
            route = Screen.TaskEdit.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            TaskCreationScreen(navController = navController, taskId = taskId)
        }
    }
}