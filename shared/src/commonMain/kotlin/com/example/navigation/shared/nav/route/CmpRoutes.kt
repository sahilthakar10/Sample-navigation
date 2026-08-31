package com.example.navigation.shared.nav.route

import com.example.navigation.shared.nav.NavKey
import com.example.navigation.shared.nav.annotation.CmpDeepLink
import kotlinx.serialization.Serializable

/**
 * CMP route path constants (mirrors `CmpNavRoute` in the real app).
 * These are the single source of truth for both URL building and resolution.
 */
object CmpNavRoute {
    object Ipo {
        const val HOME = "ipo/home"
        const val DETAIL = "ipo/detail"
    }

    object OiAnalysis {
        const val HOME = "oiAnalysis/home"
    }

    object Profile {
        const val HOME = "profile/home"
    }

    object Orders {
        const val HOME = "orders/home"
        const val DETAIL = "orders/detail"
    }
}

/**
 * IPO feature routes. `@Serializable` + `NavKey` = type-safe Nav3 destinations,
 * exactly like `sfl-cmp-navigation/.../route/IpoRoute.kt`.
 */
@Serializable
sealed interface IpoRoute : NavKey {
    @Serializable
    @CmpDeepLink(CmpNavRoute.Ipo.HOME)
    data class Home(val source: String? = null) : IpoRoute

    @Serializable
    @CmpDeepLink(CmpNavRoute.Ipo.DETAIL)
    data class Detail(val ipoId: String, val source: String? = null) : IpoRoute
}

@Serializable
sealed interface OiAnalysisRoute : NavKey {
    @Serializable
    @CmpDeepLink(CmpNavRoute.OiAnalysis.HOME)
    data class Home(val instrumentId: String? = null, val source: String? = null) : OiAnalysisRoute
}

@Serializable
sealed interface ProfileRoute : NavKey {
    @Serializable
    @CmpDeepLink(CmpNavRoute.Profile.HOME)
    data object Home : ProfileRoute
}

/**
 * Orders feature routes. The Detail route carries [orderId] — this runtime arg is
 * fed into the Metro-injected presenter via **assisted injection** (see OrdersDi.kt).
 */
@Serializable
sealed interface OrdersRoute : NavKey {
    @Serializable
    @CmpDeepLink(CmpNavRoute.Orders.HOME)
    data object Home : OrdersRoute

    @Serializable
    @CmpDeepLink(CmpNavRoute.Orders.DETAIL)
    data class Detail(val orderId: String) : OrdersRoute
}
