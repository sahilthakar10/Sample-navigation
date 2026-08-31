package com.example.navigation.shared.nav

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

/**
 * DSL receiver used to register `entry<RouteType> { screen }` mappings.
 *
 * Mirrors `androidx.navigation3.runtime.EntryProviderScope<NavKey>` from the real app.
 * Each CMP feature module contributes its entries as an extension on this scope
 * (see `IpoGraph.kt`, `OiAnalysisGraph.kt`, `ProfileGraph.kt`).
 */
class EntryProviderScope {
    val entries: LinkedHashMap<KClass<out NavKey>, @Composable (NavKey) -> Unit> = LinkedHashMap()

    inline fun <reified T : NavKey> entry(crossinline content: @Composable (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        entries[T::class] = { key -> content(key as T) }
    }
}
