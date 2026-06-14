package com.example.baithi.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.baithi.ui.screens.about.AboutScreen
import com.example.baithi.ui.screens.edit.AddTransactionScreen
import com.example.baithi.ui.screens.home.ManHinhChinh
import com.example.baithi.ui.screens.stats.StatsScreen
import com.example.baithi.ui.viewmodel.TransactionViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Add : Screen("add")
    object Edit : Screen("edit/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit/$transactionId"
    }
    object Stats : Screen("stats")
    object About : Screen("about")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: TransactionViewModel
) {
    NavHost(
        navController = navController, 
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        }
    ) {
        composable(Screen.Home.route) {
            ManHinhChinh(
                viewModel = viewModel,
                onThemGiaoDich = { navController.navigate(Screen.Add.route) },
                onSuaGiaoDich = { transaction -> 
                    navController.navigate(Screen.Edit.createRoute(transaction.id))
                },
                onXemThongKe = { navController.navigate(Screen.Stats.route) },
                onXemGioiThieu = { navController.navigate(Screen.About.route) }
            )
        }
        composable(Screen.Add.route) {
            AddTransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId")
            AddTransactionScreen(
                viewModel = viewModel,
                transactionId = transactionId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
