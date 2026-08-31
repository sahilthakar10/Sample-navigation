package com.example.navigation.shared.feature.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigation.shared.nav.EntryProviderScope
import com.example.navigation.shared.nav.NavBackStack
import com.example.navigation.shared.nav.host.HostNavigation
import com.example.navigation.shared.nav.host.ScripDetailsRoute
import com.example.navigation.shared.nav.route.OrdersRoute
import com.example.navigation.shared.ui.ActionButton
import com.example.navigation.shared.ui.CmpScreenScaffold
import com.example.navigation.shared.ui.SecondaryButton

/**
 * Orders feature graph — this one wires screens to **Metro-injected presenters**.
 *  - List screen: pure constructor injection (`OrderListPresenter`).
 *  - Detail screen: **assisted injection** — the NavKey's `orderId` is passed to
 *    the compile-time-checked `OrderDetailPresenter.Factory`.
 */
fun EntryProviderScope.ordersGraph(
    backStack: NavBackStack,
    onBackPressed: () -> Unit,
    onHostNavigation: (HostNavigation) -> Unit
) {
    entry<OrdersRoute.Home> {
        // Constructor-injected presenter pulled from the graph, remembered for the entry.
        val presenter = remember { OrdersDi.appGraph.orderListPresenter }
        OrderListScreen(
            orders = presenter.orders(),
            onOpenOrder = { orderId -> backStack.add(OrdersRoute.Detail(orderId)) },
            onBack = onBackPressed
        )
    }

    entry<OrdersRoute.Detail> { key ->
        // Assisted injection: orderId (runtime, from the route) + repo (from the graph).
        val presenter = remember(key.orderId) {
            OrdersDi.appGraph.orderDetailPresenterFactory.create(key.orderId)
        }
        OrderDetailScreen(
            order = presenter.order,
            onViewInNative = {
                // CMP → Native: leave the CMP graph and open the native Scrip Detail screen.
                onHostNavigation(
                    ScripDetailsRoute.detailsPage(
                        instrumentId = presenter.order?.symbol ?: key.orderId,
                        source = "orders"
                    )
                )
            },
            onBack = onBackPressed
        )
    }
}

@Composable
private fun OrderListScreen(
    orders: List<Order>,
    onOpenOrder: (String) -> Unit,
    onBack: () -> Unit
) {
    CmpScreenScaffold(
        badge = "CMP • Nav3 + Metro DI",
        title = "Orders",
        subtitle = "OrderListPresenter is constructor-injected by Metro (compile-time DI)."
    ) {
        orders.forEach { order ->
            ActionButton("${order.id} • ${order.symbol} (${order.status})") { onOpenOrder(order.id) }
        }
        SecondaryButton("Back (pop CMP / exit to native)") { onBack() }
    }
}

@Composable
private fun OrderDetailScreen(order: Order?, onViewInNative: () -> Unit, onBack: () -> Unit) {
    CmpScreenScaffold(
        badge = "CMP • Nav3 + Metro DI",
        title = "Order ${order?.id ?: "—"}",
        subtitle = "OrderDetailPresenter built via ASSISTED injection (orderId from the NavKey)."
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Symbol: ${order?.symbol ?: "unknown"}", style = MaterialTheme.typography.bodyLarge)
                Text("Quantity: ${order?.qty ?: 0}", style = MaterialTheme.typography.bodyMedium)
                Text("Status: ${order?.status ?: "—"}", style = MaterialTheme.typography.bodyMedium)
            }
        }
        SecondaryButton("View ${order?.symbol ?: "symbol"} in native Scrip Detail (CMP → Native)") { onViewInNative() }
        SecondaryButton("Back (pop CMP / exit to native)") { onBack() }
    }
}
