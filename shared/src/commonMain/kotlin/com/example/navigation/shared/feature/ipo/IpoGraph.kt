package com.example.navigation.shared.feature.ipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigation.shared.nav.EntryProviderScope
import com.example.navigation.shared.nav.NavBackStack
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.host.ScripDetailsRoute
import com.example.navigation.shared.nav.route.IpoRoute
import com.example.navigation.shared.ui.ActionButton
import com.example.navigation.shared.ui.CmpScreenScaffold
import com.example.navigation.shared.ui.SecondaryButton

/**
 * IPO feature graph. Extension on [EntryProviderScope] — mirrors
 * `sal-cmp-ipo/.../graph/IpoGraph.kt`. Receives the shared [backStack] so it can
 * push new CMP routes, and [onHostNavigation] to leave CMP.
 */
fun EntryProviderScope.ipoGraph(
    backStack: NavBackStack,
    onBackPressed: () -> Unit,
    onHostNavigation: (HostNavigation) -> Unit
) {
    entry<IpoRoute.Home> { route ->
        IpoHomeScreen(
            source = route.source,
            onOpenDetail = { ipoId ->
                // CMP -> CMP: just push a typed route onto the Nav3 back stack.
                backStack.add(IpoRoute.Detail(ipoId = ipoId, source = "ipo_home"))
            },
            onBack = onBackPressed
        )
    }

    entry<IpoRoute.Detail> { route ->
        IpoDetailScreen(
            ipoId = route.ipoId,
            source = route.source,
            onOpenAnotherIpo = { ipoId ->
                // CMP -> CMP -> CMP: push again (native never sees these hops).
                backStack.add(IpoRoute.Detail(ipoId = ipoId, source = "ipo_detail"))
            },
            onViewScripDetails = {
                // CMP -> Native: hand a native deep link back to the host.
                onHostNavigation(ScripDetailsRoute.detailsPage(instrumentId = route.ipoId, source = "ipo"))
            },
            onOpenWebsite = {
                // CMP -> External URL.
                onHostNavigation(HostNavigation.ExternalUri("https://www.nseindia.com", isValidationNeeded = false))
            },
            onBack = onBackPressed
        )
    }
}

@Composable
private fun IpoHomeScreen(source: String?, onOpenDetail: (String) -> Unit, onBack: () -> Unit) {
    CmpScreenScaffold(
        badge = "CMP • Nav3 (shared)",
        title = "IPO Home",
        subtitle = "source = ${source ?: "—"}"
    ) {
        InfoCard("Tap an IPO to push a Detail route onto the CMP back stack (CMP → CMP).")
        ActionButton("Open IPO: TATA-TECH") { onOpenDetail("TATA-TECH") }
        ActionButton("Open IPO: SWIGGY") { onOpenDetail("SWIGGY") }
        SecondaryButton("Back (pop CMP / exit to native)") { onBack() }
    }
}

@Composable
private fun IpoDetailScreen(
    ipoId: String,
    source: String?,
    onOpenAnotherIpo: (String) -> Unit,
    onViewScripDetails: () -> Unit,
    onOpenWebsite: () -> Unit,
    onBack: () -> Unit
) {
    CmpScreenScaffold(
        badge = "CMP • Nav3 (shared)",
        title = "IPO Detail: $ipoId",
        subtitle = "source = ${source ?: "—"}"
    ) {
        InfoCard("This is a CMP screen. Every button below is a different navigation scenario.")
        ActionButton("Open another IPO (CMP → CMP → CMP)") { onOpenAnotherIpo("NYKAA") }
        SecondaryButton("View Scrip Details (CMP → Native)") { onViewScripDetails() }
        SecondaryButton("Open NSE website (CMP → External URL)") { onOpenWebsite() }
        SecondaryButton("Back (pop CMP / exit to native)") { onBack() }
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
