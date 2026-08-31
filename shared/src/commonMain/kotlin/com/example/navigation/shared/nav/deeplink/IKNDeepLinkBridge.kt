package com.example.navigation.shared.nav.deeplink

/**
 * Platform-agnostic interface for firing an app-wide deep link from *inside*
 * shared code (a ViewModel that has no navigation callback in scope).
 *
 * Mirrors `sfl-cmp-navigation/.../deeplink/IKNDeepLinkBridge.kt`.
 *
 * Implemented per platform and injected via DI:
 *  - Android: `IKNDeepLinkBridgeImpl` -> native deep link handler
 *  - iOS: `CMPDeepLinkBridge` (Swift, conforms to this Kotlin interface) -> DeeplinkOpener
 */
fun interface IKNDeepLinkBridge {
    fun navigateAppDeeplink(deepLink: String)
}

/**
 * Minimal DI container standing in for Koin. The platform registers its
 * [IKNDeepLinkBridge] implementation at startup; shared code resolves it.
 */
object ServiceLocator {
    private var deepLinkBridge: IKNDeepLinkBridge? = null

    fun registerDeepLinkBridge(bridge: IKNDeepLinkBridge) {
        deepLinkBridge = bridge
    }

    fun deepLinkBridge(): IKNDeepLinkBridge =
        deepLinkBridge ?: error("IKNDeepLinkBridge not registered. Call ServiceLocator.registerDeepLinkBridge() at startup.")
}
