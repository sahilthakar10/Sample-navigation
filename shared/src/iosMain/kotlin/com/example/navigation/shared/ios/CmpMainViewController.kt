package com.example.navigation.shared.ios

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import com.example.navigation.shared.nav.NavKey
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.ui.CmpNavigationHost
import platform.UIKit.UIViewController

/**
 * iOS entry adapter: hosts the shared [CmpNavigationHost] inside a UIKit
 * [UIViewController] that Swift can push. Mirrors
 * `KNUmbrellaLib/.../CmpMainViewController.kt`.
 *
 * `onHostNavigation` is a hole filled by a Swift closure in `CmpDeeplinkNavigator`.
 */
@Suppress("FunctionName", "unused")
fun CmpMainViewController(
    startNavKey: NavKey,
    onHostNavigation: (HostNavigation) -> Unit
): UIViewController = ComposeUIViewController {
    MaterialTheme {
        CmpNavigationHost(
            startDestination = startNavKey,
            onHostNavigation = onHostNavigation
        )
    }
}
