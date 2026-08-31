package com.example.navigation.shared

import com.example.navigation.shared.nav.generated.CmpDeepLinkResolver
import com.example.navigation.shared.nav.route.IpoRoute
import com.example.navigation.shared.nav.route.OiAnalysisRoute
import com.example.navigation.shared.nav.route.ProfileRoute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Verifies the KSP-generated resolver parses deep links into typed routes. */
class CmpDeepLinkResolverTest {

    @Test
    fun resolvesIpoDetailWithArgs() {
        val key = CmpDeepLinkResolver.resolve("ipo/detail?ipoId=SWIGGY&source=adb")
        assertTrue(key is IpoRoute.Detail, "expected IpoRoute.Detail but was $key")
        key as IpoRoute.Detail
        assertEquals("SWIGGY", key.ipoId)
        assertEquals("adb", key.source)
    }

    @Test
    fun resolvesOiAnalysisHome() {
        val key = CmpDeepLinkResolver.resolve("oiAnalysis/home?instrumentId=NIFTY&source=sim")
        assertTrue(key is OiAnalysisRoute.Home, "expected OiAnalysisRoute.Home but was $key")
        key as OiAnalysisRoute.Home
        assertEquals("NIFTY", key.instrumentId)
    }

    @Test
    fun resolvesProfileObjectRoute() {
        assertTrue(CmpDeepLinkResolver.resolve("profile/home") is ProfileRoute.Home)
    }

    @Test
    fun hasDeeplinkReflectsRegistry() {
        assertTrue(CmpDeepLinkResolver.hasDeeplink("ipo/home"))
        assertTrue(!CmpDeepLinkResolver.hasDeeplink("does/not/exist"))
    }

    @Test
    fun unknownRouteResolvesToNull() {
        assertNull(CmpDeepLinkResolver.resolve("unknown/path?x=1"))
    }
}
