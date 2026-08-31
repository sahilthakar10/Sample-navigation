package com.example.navigation.shared.nav.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.navigation.shared.nav.EntryProviderScope
import com.example.navigation.shared.nav.NavBackStack
import com.example.navigation.shared.nav.NavKey
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.provideNavEntries

/**
 * The single, platform-agnostic CMP host. Used by BOTH platforms:
 *  - Android: via `CmpRouteScreen` (inside a Nav2 `composable`)
 *  - iOS: via `CmpMainViewController` (inside an `ApeComposeUIViewController`)
 *
 * Mirrors `sal-cmp-navigation-contract/.../ui/CmpNavigationHost.kt`.
 */
@Composable
fun CmpNavigationHost(
    startDestination: NavKey,
    onHostNavigation: (HostNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStack = remember(startDestination) { NavBackStack(startDestination) }

    // Device/system back: pop within CMP, or tell the host to exit on the root entry.
    PlatformBackHandler(enabled = true) {
        backStack.popOrExit { onHostNavigation(HostNavigation.OnExit) }
    }

    val entryProviderScope: EntryProviderScope = remember(backStack) {
        EntryProviderScope().apply {
            provideNavEntries(
                backStack = backStack,
                onHostNavigation = onHostNavigation,
                onBackPressed = {
                    backStack.popOrExit { onHostNavigation(HostNavigation.OnExit) }
                }
            )
        }
    }

    CmpNavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProviderScope = entryProviderScope
    )
}
