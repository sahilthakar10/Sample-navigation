package com.example.navigation.shared.nav

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Marker interface for a type-safe navigation destination key.
 *
 * In the real app this is `androidx.navigation3.runtime.NavKey`.
 * Here we ship a tiny, dependency-light equivalent so the sample builds
 * reliably on Android + iOS while demonstrating the exact same architecture.
 */
interface NavKey

/**
 * Observable Navigation-3 style back stack. It is just a list you own — the
 * whole point of Nav3. Mutating it recomposes [com.example.navigation.shared.nav.ui.CmpNavDisplay].
 *
 * Mirrors `androidx.navigation3.runtime.NavBackStack` + the custom back stack
 * helpers from `sfl-cmp-navigation/.../Util.kt`.
 */
class NavBackStack(startDestination: NavKey) {
    val entries: SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    val current: NavKey? get() = entries.lastOrNull()

    /** CMP -> CMP navigation: push a typed route onto the stack. */
    fun add(key: NavKey) {
        entries.add(key)
    }

    /** Replace the current top entry (no back entry left behind). */
    fun replace(new: NavKey) {
        if (entries.isNotEmpty()) entries.removeAt(entries.lastIndex)
        entries.add(new)
    }

    /** Pop within CMP. Keeps at least one entry. Returns false if already at root. */
    fun pop(): Boolean {
        return if (entries.size > 1) {
            entries.removeAt(entries.lastIndex)
            true
        } else {
            false
        }
    }

    /** Pop within CMP, or invoke [onExit] (e.g. tell the host to close CMP) when at root. */
    fun popOrExit(onExit: () -> Unit) {
        if (!pop()) onExit()
    }

    /** Pop entries until a route of type [T] is on top. */
    inline fun <reified T : NavKey> popUpTo(inclusive: Boolean = false) {
        while (entries.size > 1 && entries.last() !is T) {
            entries.removeAt(entries.lastIndex)
        }
        if (inclusive && entries.lastOrNull() is T && entries.size > 1) {
            entries.removeAt(entries.lastIndex)
        }
    }
}
