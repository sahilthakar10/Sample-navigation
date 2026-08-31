package com.example.navigation.shared.feature.profile

import androidx.compose.runtime.Composable
import com.example.navigation.shared.nav.EntryProviderScope
import com.example.navigation.shared.nav.deeplink.IKNDeepLinkBridge
import com.example.navigation.shared.nav.deeplink.ServiceLocator
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.route.ProfileRoute
import com.example.navigation.shared.ui.ActionButton
import com.example.navigation.shared.ui.CmpScreenScaffold
import com.example.navigation.shared.ui.SecondaryButton

/**
 * A shared "ViewModel" that fires an app-wide deep link through [IKNDeepLinkBridge]
 * WITHOUT a navigation callback — mirrors `EducationViewModel` in the real app,
 * the one and only consumer of the bridge.
 */
class ProfileViewModel(
    private val deepLinkBridge: IKNDeepLinkBridge = ServiceLocator.deepLinkBridge()
) {
    fun onOpenSettingsDeeplink() {
        // Platform-agnostic: Android -> DeeplinkHandler, iOS -> DeeplinkOpener (Swift).
        deepLinkBridge.navigateAppDeeplink("navsample://app/scrip/detail?instrumentId=RELIANCE&source=profile_bridge")
    }
}

/** Profile feature graph — mirrors `sal-cmp-profile/.../graph/ProfileGraph.kt`. */
fun EntryProviderScope.profileGraph(
    onBackPressed: () -> Unit,
    onHostNavigation: (HostNavigation) -> Unit
) {
    entry<ProfileRoute.Home> {
        val viewModel = ProfileViewModel()
        ProfileScreen(
            onFireBridgeDeeplink = viewModel::onOpenSettingsDeeplink,
            onBack = onBackPressed
        )
    }
}

@Composable
private fun ProfileScreen(onFireBridgeDeeplink: () -> Unit, onBack: () -> Unit) {
    CmpScreenScaffold(
        badge = "CMP • Nav3 (shared)",
        title = "Profile",
        subtitle = "Demonstrates IKNDeepLinkBridge (app-wide deep link from shared code)."
    ) {
        ActionButton("Fire app deep link via IKNDeepLinkBridge") { onFireBridgeDeeplink() }
        SecondaryButton("Back (pop CMP / exit to native)") { onBack() }
    }
}
