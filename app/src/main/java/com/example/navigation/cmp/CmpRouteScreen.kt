package com.example.navigation.cmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.navigation.shared.nav.generated.CmpDeepLinkResolver
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.ui.CmpNavigationHost

/**
 * Bridge screen: resolves an incoming deep link string into a typed NavKey and,
 * on success, renders the shared [CmpNavigationHost]. Mirrors `CmpRouteScreen.kt`.
 */
@Composable
fun CmpRouteScreen(
    startDestination: String,
    onHostNavigation: (HostNavigation) -> Unit,
    onResolutionFailed: (deeplink: String?) -> Unit
) {
    val navKey = CmpDeepLinkResolver.resolve(startDestination)
    if (navKey != null) {
        CmpNavigationHost(
            startDestination = navKey,
            onHostNavigation = onHostNavigation
        )
    } else {
        LaunchedEffect(startDestination) { onResolutionFailed(startDestination) }
    }
}
