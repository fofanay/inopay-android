// Screen 01 — Welcome
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.inopay.ui.components.InopayCta
import com.inopay.ui.components.InopayDisclaimer
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val theme = LocalInopayTheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Hero
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(theme.primary, theme.primaryDark)), RoundedCornerShape(12.dp))
                .padding(24.dp),
        ) {
            Text("📈", fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Investir à la BRVM,\nsimplement",
                color = theme.onPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Achetez des actions et obligations africaines depuis votre app ${theme.partnerName}.",
                color = theme.onPrimary.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }

        // Benefits
        Benefit("💰", "Diversifiez votre épargne", "Au-delà du livret")
        Benefit("🌍", "Soutenez l'économie ouest-africaine", "BRVM · BVMAC · GSE")
        Benefit("🔍", "Suivi clair", "Reçus signés Inopay")

        InopayDisclaimer("⚠ Les marchés financiers comportent des risques. Le capital n'est pas garanti.")

        InopayCta("Commencer →", onClick = onStart)
    }
}

@Composable
private fun Benefit(icon: String, title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .background(InopayColors.BgSoft, RoundedCornerShape(8.dp))
            .padding(10.dp),
    ) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 11.sp, color = InopayColors.TextMuted)
        }
    }
}
