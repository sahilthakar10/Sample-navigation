package com.example.navigation.shared.feature.orders

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraph

/** Domain model. */
data class Order(val id: String, val symbol: String, val qty: Int, val status: String)

/** Repository contract (interface) — implementations are bound into the graph. */
interface OrderRepository {
    fun all(): List<Order>
    fun byId(id: String): Order?
}

/**
 * Constructor-injected, app-scoped singleton implementation.
 * `@ContributesBinding(AppScope::class)` auto-binds it as [OrderRepository] in any
 * `AppScope` graph — no manual `@Provides` needed (Anvil-style aggregation).
 */
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
class InMemoryOrderRepository : OrderRepository {
    private val orders = listOf(
        Order("ORD-1001", "TATA-TECH", 10, "EXECUTED"),
        Order("ORD-1002", "SWIGGY", 5, "PENDING"),
        Order("ORD-1003", "NYKAA", 25, "EXECUTED")
    )

    override fun all(): List<Order> = orders
    override fun byId(id: String): Order? = orders.firstOrNull { it.id == id }
}

/**
 * List presenter — pure constructor injection. The graph supplies [repo].
 */
@Inject
class OrderListPresenter(private val repo: OrderRepository) {
    fun orders(): List<Order> = repo.all()
}

/**
 * Detail presenter — **assisted injection**. [orderId] is supplied at call time
 * (from the NavKey), [repo] comes from the graph. This is the compile-time-checked
 * equivalent of Koin's `parametersOf(route.orderId)`.
 */
@Inject
class OrderDetailPresenter(
    @Assisted val orderId: String,
    private val repo: OrderRepository
) {
    val order: Order? get() = repo.byId(orderId)

    @AssistedFactory
    fun interface Factory {
        fun create(orderId: String): OrderDetailPresenter
    }
}

/**
 * The app dependency graph. Validated at compile time by the Metro compiler plugin.
 * Exposes the presenters (and the assisted factory) as accessors.
 */
@DependencyGraph(AppScope::class)
interface AppGraph {
    val orderListPresenter: OrderListPresenter
    val orderDetailPresenterFactory: OrderDetailPresenter.Factory
}

/**
 * App-scoped graph instance. Kept as a lazy singleton for sample simplicity;
 * in production you'd provide this via a CompositionLocal or platform DI entry point.
 */
object OrdersDi {
    val appGraph: AppGraph by lazy { createGraph<AppGraph>() }
}
