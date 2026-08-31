package com.example.navigation.shared.nav.host

/**
 * Bridge type: how a CMP screen asks the *native host* to do something outside
 * the CMP graph. Mirrors `sfl-cmp-navigation/.../route/host/HostNavigation.kt`.
 *
 * The shared CMP host only ever *emits* these — each platform supplies the handler:
 *  - Android: `CmpNavViewModel.onHostNavigation(...)` -> NavHostController
 *  - iOS: the `onHostNavigation` closure in `CmpDeeplinkNavigator`
 */
sealed interface HostNavigation {
    /** Navigate to a native/legacy screen by its deep link route string. */
    data class DeeplinkRoute(val route: String) : HostNavigation

    /** Pop/close the whole CMP host back to native. */
    data object OnExit : HostNavigation

    /** Open an external URL (browser / other app). */
    data class ExternalUri(val uri: String, val isValidationNeeded: Boolean = true) : HostNavigation
}

/** helper extension to convert a route string into a DeeplinkRoute. Mirrors `String.toDeeplinkRoute()`. */
fun String.toDeeplinkRoute(): HostNavigation = HostNavigation.DeeplinkRoute(this)

/**
 * Typed builders for native host destinations, so CMP screens don't hand-write
 * native deep links. Mirrors `sfl-cmp-navigation/.../route/host/HostRoute.kt`
 * (ScripDetailsRoute, WebviewHostRoute, etc.).
 */
object ScripDetailsRoute {
    fun detailsPage(instrumentId: String, source: String): HostNavigation =
        "scrip/detail?instrumentId=$instrumentId&source=$source".toDeeplinkRoute()
}

object WebviewHostRoute {
    fun webview(url: String): HostNavigation =
        HostNavigation.ExternalUri(uri = url, isValidationNeeded = false)
}
