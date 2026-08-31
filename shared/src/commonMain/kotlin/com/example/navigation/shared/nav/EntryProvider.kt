package com.example.navigation.shared.nav

import com.example.navigation.shared.feature.ipo.ipoGraph
import com.example.navigation.shared.feature.oianalysis.oiAnalysisGraph
import com.example.navigation.shared.feature.orders.ordersGraph
import com.example.navigation.shared.feature.profile.profileGraph
import com.example.navigation.shared.nav.host.HostNavigation

/**
 * Aggregates every CMP feature graph into one entry table.
 * Mirrors `sal-cmp-navigation-contract/.../EntryProvider.kt`.
 *
 * A new CMP screen is NOT reachable until its graph is registered here.
 */
fun EntryProviderScope.provideNavEntries(
    backStack: NavBackStack,
    onHostNavigation: (HostNavigation) -> Unit,
    onBackPressed: () -> Unit
) {
    ipoGraph(backStack, onBackPressed, onHostNavigation)
    oiAnalysisGraph(onBackPressed, onHostNavigation)
    profileGraph(onBackPressed, onHostNavigation)
    ordersGraph(backStack, onBackPressed, onHostNavigation)
}
