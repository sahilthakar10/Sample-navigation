package com.example.navigation.native

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** NATIVE (Nav2) home screen. Buttons deep link into the shared CMP (Nav3) graph. */
@Composable
fun NativeHomeScreen(
    onOpenIpo: () -> Unit,
    onOpenOiAnalysis: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenOrders: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(
                badge = "NATIVE • Nav2 (androidApp)",
                title = "Native Home",
                subtitle = "This is the native graph. Buttons below cross into the CMP (Nav3) graph via a deep link."
            )
            Button(onClick = onOpenIpo, modifier = Modifier.fillMaxWidth()) { Text("Open IPO Home (Native → CMP)") }
            Button(onClick = onOpenOiAnalysis, modifier = Modifier.fillMaxWidth()) { Text("Open OI Analysis (Native → CMP)") }
            Button(onClick = onOpenProfile, modifier = Modifier.fillMaxWidth()) { Text("Open Profile (Native → CMP)") }
            Button(onClick = onOpenOrders, modifier = Modifier.fillMaxWidth()) { Text("Open Orders (Native → CMP, Metro DI)") }
        }
    }
}

/** NATIVE (Nav2) scrip detail screen. This is where CMP screens navigate back to (CMP → Native). */
@Composable
fun NativeScripDetailScreen(
    instrumentId: String,
    source: String,
    onBack: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(
                badge = "NATIVE • Nav2 (androidApp)",
                title = "Scrip Detail: $instrumentId",
                subtitle = "Reached from a CMP screen via HostNavigation.DeeplinkRoute. source = $source"
            )
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        }
    }
}

@Composable
private fun Header(badge: String, title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(badge, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
