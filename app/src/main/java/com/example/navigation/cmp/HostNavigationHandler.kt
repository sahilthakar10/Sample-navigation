package com.example.navigation.cmp

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.navigation.shared.nav.host.HostNavigation

/**
 * Handles CMP → host navigation: translates a CMP-emitted [HostNavigation] into
 * native `NavHostController` operations / external intents.
 *
 * NOTE: this is a plain handler class, not an Android `ViewModel`. In the real app the
 * equivalent happens to be a Hilt `@HiltViewModel` (`CmpNavViewModel`), but here it
 * holds no UI state and has no ViewModel lifecycle, so it's named for what it does.
 */
class HostNavigationHandler(private val context: Context) {

    fun handle(navHostController: NavHostController, hostNavigation: HostNavigation) {
        when (hostNavigation) {
            is HostNavigation.DeeplinkRoute -> {
                // CMP asked for a native destination — navigate the native graph.
                runCatching { navHostController.navigate(hostNavigation.route) }
            }

            is HostNavigation.OnExit -> {
                // CMP back stack is empty — pop the CMP host out of the native stack.
                navHostController.navigateUp()
            }

            is HostNavigation.ExternalUri -> {
                runCatching {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, hostNavigation.uri.toUri())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }
    }
}
