package com.example.navigation.shared.nav.ui

import androidx.compose.runtime.Composable

/**
 * Platform back handling. Android hooks the system back button to pop the CMP
 * back stack; iOS relies on the nav-bar/swipe to pop the hosting UIViewController
 * (intra-CMP back is driven by on-screen buttons), so it is a no-op there.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
