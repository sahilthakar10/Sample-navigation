package com.example.navigation.shared

import com.example.navigation.shared.feature.orders.OrdersDi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Verifies the Metro dependency graph resolves presenters, incl. assisted injection. */
class OrdersDiTest {

    @Test
    fun listPresenterResolvesFromGraph() {
        val presenter = OrdersDi.appGraph.orderListPresenter
        assertTrue(presenter.orders().isNotEmpty())
    }

    @Test
    fun detailPresenterAssistedInjectionUsesNavKeyArg() {
        // orderId is the "assisted" runtime arg (the NavKey value); repo comes from the graph.
        val presenter = OrdersDi.appGraph.orderDetailPresenterFactory.create("ORD-1002")
        assertEquals("ORD-1002", presenter.orderId)
        assertNotNull(presenter.order)
        assertEquals("SWIGGY", presenter.order?.symbol)
    }

    @Test
    fun repositoryIsSingletonWithinGraph() {
        // Same graph -> same app-scoped repo instance backing both presenters.
        val a = OrdersDi.appGraph.orderDetailPresenterFactory.create("ORD-1001").order
        val b = OrdersDi.appGraph.orderDetailPresenterFactory.create("ORD-1001").order
        assertEquals(a, b)
    }
}
