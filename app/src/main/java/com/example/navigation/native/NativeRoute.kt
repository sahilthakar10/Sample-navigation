package com.example.navigation.native

/** Native (Nav2) route strings — the "legacy" graph, separate from the CMP graph. */
object NativeRoute {
    const val HOME = "native/home"
    const val SCRIP_DETAIL = "scrip/detail?instrumentId={instrumentId}&source={source}"
    const val SCRIP_DETAIL_PATH = "scrip/detail"

    fun scripDetail(instrumentId: String, source: String): String =
        "$SCRIP_DETAIL_PATH?instrumentId=$instrumentId&source=$source"
}
