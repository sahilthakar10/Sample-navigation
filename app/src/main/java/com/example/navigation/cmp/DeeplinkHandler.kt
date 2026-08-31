package com.example.navigation.cmp

/** Where a deep link originated. Mirrors the real app's `DeeplinkSource`. */
enum class DeeplinkSource { INTERNAL, EXTERNAL }

/**
 * Implemented by the UI/host (which owns the live NavHostController) and registered
 * with the [DeeplinkHandler]. Mirrors `sfl-deeplink/.../DeeplinkHandlerCallback.kt`.
 */
interface DeeplinkHandlerCallback {
    fun navigateDeeplink(route: String, wasDeferred: Boolean)
}

/**
 * Observer-based deep link pipeline. The app-scoped [IKNDeepLinkBridgeImpl] feeds
 * deep links in via [consumeDeeplink]; lifecycle-scoped UI registers a
 * [DeeplinkHandlerCallback] to actually navigate. If no callback is registered yet
 * (e.g. app still starting), the deep link is **deferred** and flushed on register.
 *
 * Mirrors `sfl-deeplink/.../DeeplinkHandler.kt` + `DeeplinkHandlerImpl.kt` (minus the
 * session/validation logic), replacing the previous mutable-static callback.
 */
interface DeeplinkHandler {
    fun register(callback: DeeplinkHandlerCallback)
    fun unRegister(callback: DeeplinkHandlerCallback)
    fun consumeDeeplink(route: String, source: DeeplinkSource)
}

class DeeplinkHandlerImpl : DeeplinkHandler {
    private val callbacks = mutableSetOf<DeeplinkHandlerCallback>()
    private val deferred = mutableListOf<String>()

    override fun register(callback: DeeplinkHandlerCallback) {
        callbacks.add(callback)
        // Flush anything that arrived before a callback was available.
        if (deferred.isNotEmpty()) {
            val pending = deferred.toList()
            deferred.clear()
            pending.forEach { route -> callback.navigateDeeplink(route, wasDeferred = true) }
        }
    }

    override fun unRegister(callback: DeeplinkHandlerCallback) {
        callbacks.remove(callback)
    }

    override fun consumeDeeplink(route: String, source: DeeplinkSource) {
        if (callbacks.isEmpty()) {
            deferred.add(route) // defer until a callback registers
        } else {
            callbacks.forEach { it.navigateDeeplink(route, wasDeferred = false) }
        }
    }
}
