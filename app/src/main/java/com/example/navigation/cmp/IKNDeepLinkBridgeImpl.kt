package com.example.navigation.cmp

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.navigation.shared.nav.deeplink.IKNDeepLinkBridge

/**
 * Android implementation of the shared [IKNDeepLinkBridge].
 * Mirrors `IKNDeepLinkBridgeImpl` in the real app: internal deep links go through the
 * [DeeplinkHandler] pipeline (register/callback + deferral); external ones go to the OS.
 */
class IKNDeepLinkBridgeImpl(
    private val context: Context,
    private val deeplinkHandler: DeeplinkHandler
) : IKNDeepLinkBridge {

    override fun navigateAppDeeplink(deepLink: String) {
        val uri = deepLink.toUri()
        if (uri.scheme == INTERNAL_SCHEME) {
            // Strip "navsample://app/" -> "scrip/detail?instrumentId=..&source=.."
            val route = buildString {
                append(uri.path?.trimStart('/').orEmpty())
                uri.query?.let { append("?").append(it) }
            }
            deeplinkHandler.consumeDeeplink(route, DeeplinkSource.INTERNAL)
        } else {
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }

    private companion object {
        const val INTERNAL_SCHEME = "navsample"
    }
}
