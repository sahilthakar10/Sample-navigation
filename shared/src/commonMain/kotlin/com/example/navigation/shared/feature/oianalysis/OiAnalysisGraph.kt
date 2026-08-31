package com.example.navigation.shared.feature.oianalysis

import androidx.compose.runtime.Composable
import com.example.navigation.shared.nav.EntryProviderScope
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.route.OiAnalysisRoute
import com.example.navigation.shared.ui.CmpScreenScaffold
import com.example.navigation.shared.ui.SecondaryButton

/** OI Analysis feature graph — mirrors `sal-cmp-oi-analysis/.../OiAnalysisGraph.kt`. */
fun EntryProviderScope.oiAnalysisGraph(
    onBackPressed: () -> Unit,
    onHostNavigation: (HostNavigation) -> Unit
) {
    entry<OiAnalysisRoute.Home> { route ->
        OiAnalysisScreen(
            instrumentId = route.instrumentId,
            source = route.source,
            onBack = onBackPressed
        )
    }
}

@Composable
private fun OiAnalysisScreen(instrumentId: String?, source: String?, onBack: () -> Unit) {
    CmpScreenScaffold(
        badge = "CMP • Nav3 (shared)",
        title = "OI Analysis",
        subtitle = "instrumentId = ${instrumentId ?: "—"} • source = ${source ?: "—"}"
    ) {
        SecondaryButton("Back (pop CMP / exit to native)") { onBack() }
    }
}
