package com.example.navigation.shared.nav.ui

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS pops the hosting UIViewController via nav-bar/swipe; intra-CMP back is
    // driven by on-screen buttons, so nothing to hook here.
}
