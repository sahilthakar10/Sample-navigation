package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigation.cmp.DeeplinkHandlerCallback
import com.example.navigation.cmp.HostNavigationHandler
import com.example.navigation.cmp.cmpNavGraph
import com.example.navigation.native.NativeHomeScreen
import com.example.navigation.native.NativeRoute
import com.example.navigation.native.NativeScripDetailScreen
import com.example.navigation.shared.nav.generated.CmpNavigationRoute

/**
 * The single NATIVE (Nav2) root graph. The CMP (Nav3) graph is nested inside it
 * as destinations registered by [cmpNavGraph]. Mirrors `SMAppNavigation.kt`.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val hostNavigationHandler = remember { HostNavigationHandler(context) }

    // Register a lifecycle-scoped callback so the app-scoped DeeplinkHandler can drive
    // this NavHost. Deferred deep links (fired before this ran) are flushed on register.
    val deeplinkHandler = remember(context) {
        (context.applicationContext as NavApplication).deeplinkHandler
    }
    DisposableEffect(deeplinkHandler, navController) {
        val callback = object : DeeplinkHandlerCallback {
            override fun navigateDeeplink(route: String, wasDeferred: Boolean) {
                runCatching { navController.navigate(route) }
            }
        }
        deeplinkHandler.register(callback)
        onDispose { deeplinkHandler.unRegister(callback) }
    }

    NavHost(navController = navController, startDestination = NativeRoute.HOME) {

        // ---- NATIVE destinations ----
        composable(NativeRoute.HOME) {
            NativeHomeScreen(
                onOpenIpo = { navController.navigate(CmpNavigationRoute.IpoRoute.home(source = "native_home")) },
                onOpenOiAnalysis = { navController.navigate(CmpNavigationRoute.OiAnalysisRoute.home(instrumentId = null, source = "native_home")) },
                onOpenProfile = { navController.navigate(CmpNavigationRoute.ProfileRoute.home()) },
                onOpenOrders = { navController.navigate(CmpNavigationRoute.OrdersRoute.home()) }
            )
        }

        composable(
            route = NativeRoute.SCRIP_DETAIL,
            arguments = listOf(
                navArgument("instrumentId") { type = NavType.StringType; defaultValue = "" },
                navArgument("source") { type = NavType.StringType; defaultValue = "" }
            )
        ) { entry ->
            NativeScripDetailScreen(
                instrumentId = entry.arguments?.getString("instrumentId").orEmpty(),
                source = entry.arguments?.getString("source").orEmpty(),
                onBack = { navController.navigateUp() }
            )
        }

        // ---- CMP (Nav3) graph nested inside the native Nav2 graph ----
        cmpNavGraph(
            navController = navController,
            onHostNavigation = { hostNavigation ->
                hostNavigationHandler.handle(navController, hostNavigation)
            }
        )
    }
}
