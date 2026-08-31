package com.example.navigation.cmp

import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.navigation.shared.nav.generated.CmpNavigationRoute
import com.example.navigation.shared.nav.host.HostNavigation

/**
 * Registers every CMP deep link as a destination inside the NATIVE Nav2 graph.
 * Mirrors `CmpGraph.cmpNavGraph()` in the real app.
 *
 * Each CMP route becomes a Nav2 `composable` whose body is [CmpRouteScreen],
 * so `navController.navigate("ipo/home?...")` (or a system deep link) lands in CMP.
 */
fun NavGraphBuilder.cmpNavGraph(
    navController: NavHostController,
    onHostNavigation: (HostNavigation) -> Unit
) {
    CmpNavigationRoute.deeplinkUris.forEach { deeplinkUri ->
        composable(
            route = deeplinkUri,
            deepLinks = listOf(navDeepLink { uriPattern = "navsample://app/$deeplinkUri" })
        ) { entry ->
            val startDestination = remember(deeplinkUri) {
                parseDeeplinkWithArguments(entry, deeplinkUri)
            }
            CmpRouteScreen(
                startDestination = startDestination,
                onHostNavigation = onHostNavigation,
                onResolutionFailed = { navController.navigateUp() }
            )
        }
    }
}

/**
 * Rebuilds the concrete deep link string (template + runtime arg values) so the
 * CMP resolver can parse it into a typed NavKey. Mirrors `parseDeeplinkWithArguments`.
 */
private fun parseDeeplinkWithArguments(entry: NavBackStackEntry, template: String): String {
    val path = template.substringBefore('?')
    val queryPart = template.substringAfter('?', "")
    if (queryPart.isEmpty()) return path

    val queryKeys = queryPart.split('&')
        .map { it.substringBefore('=') }
        .filter { it.isNotEmpty() }

    val query = queryKeys.mapNotNull { key ->
        val value = entry.arguments?.getString(key)
        if (value.isNullOrEmpty()) null else "$key=$value"
    }.joinToString("&")

    return if (query.isEmpty()) path else "$path?$query"
}
