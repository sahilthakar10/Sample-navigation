package com.example.navigation.shared.nav.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.navigation.shared.nav.EntryProviderScope
import com.example.navigation.shared.nav.NavBackStack

/**
 * Renders the top of the [NavBackStack] using the registered `entry<>` mappings.
 *
 * Mirrors `sal-cmp-navigation-contract/.../ui/CmpNavDisplay.kt`, which wraps
 * `androidx.navigation3.ui.NavDisplay` with scene strategies + transitions.
 */
@Composable
internal fun CmpNavDisplay(
    modifier: Modifier,
    backStack: NavBackStack,
    entryProviderScope: EntryProviderScope
) {
    Box(modifier = modifier.fillMaxSize()) {
        val current = backStack.current
        AnimatedContent(
            targetState = current,
            transitionSpec = {
                (slideInHorizontally { it / 2 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it / 3 } + fadeOut())
            },
            label = "cmp-nav"
        ) { entry ->
            when (entry) {
                null -> Text("Empty back stack", style = MaterialTheme.typography.bodyLarge)
                else -> {
                    val content = entryProviderScope.entries[entry::class]
                    if (content != null) {
                        content(entry)
                    } else {
                        Text(
                            "No entry registered for ${entry::class.simpleName}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
