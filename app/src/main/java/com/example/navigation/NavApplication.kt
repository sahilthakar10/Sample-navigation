package com.example.navigation

import android.app.Application
import com.example.navigation.cmp.DeeplinkHandler
import com.example.navigation.cmp.DeeplinkHandlerImpl
import com.example.navigation.cmp.IKNDeepLinkBridgeImpl
import com.example.navigation.shared.nav.deeplink.ServiceLocator

/**
 * Owns the app-scoped [DeeplinkHandler] singleton and registers the Android
 * [IKNDeepLinkBridgeImpl] into the shared [ServiceLocator] at startup.
 * Mirrors the real app's DI: a single `DeeplinkHandler` that UI registers callbacks with.
 */
class NavApplication : Application() {

    /** App-scoped singleton; the composed NavHost registers a callback on it. */
    val deeplinkHandler: DeeplinkHandler = DeeplinkHandlerImpl()

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.registerDeepLinkBridge(
            IKNDeepLinkBridgeImpl(applicationContext, deeplinkHandler)
        )
    }
}
