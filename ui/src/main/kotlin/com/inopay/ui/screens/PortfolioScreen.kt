// Screen 10 — Portefeuille
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.InopayChangeText
import com.inopay.ui.components.InopayLogoCircle
import com.inopay.ui.models.PortfolioViewModel
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel) {
    val theme = LocalInopayTheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Mon portefeuille", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        if (viewModel.beneficiaryLabel != null) {
            Text(viewModel.beneficiaryLabel, fontSize = 13.sp, color = InopayColors.TextMuted)
        }

        // Big metric
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(theme.primary, theme.primaryDark)), RoundedCornerShape(12.dp))
                .padding(16.dp),
        ) {
            Text("Valeur totale", fontSize = 12.sp, color = theme.onPrimary.copy(alpha = 0.85f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(viewModel.totalValue.fcfa(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = theme.onPrimary)
                Spacer(Modifier.width(4.dp))
                Text("FCFA", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = theme.onPrimary)
            }
            val sign = if (viewModel.gain >= 0) "+" else ""
            Text(
                "Investi : ${viewModel.totalInvested.fcfa()} FCFA · $sign${viewModel.gain.fcfa()} ($sign${"%.1f".format(viewModel.gainPct)} %)",
                fontSize = 11.sp, color = theme.onPrimary.copy(alpha = 0.9f),
            )
        }

        // Allocation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp)),
        ) {
            viewModel.allocation.forEach { slice ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(slice.pct.toFloat())
                        .fillMaxHeight()
                        .background(slice.color),
                ) {
                    Text("${slice.label} ${slice.pct}%",
                        fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }

        Text("Mes positions", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

        viewModel.holdings.forEach { h ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            ) {
                InopayLogoCircle(h.logoLetter, h.logoColor)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(h.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("${h.qty} actions × ${h.unitPrice.fcfa()}", fontSize = 11.sp, color = InopayColors.TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(h.totalValue.fcfa(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    InopayChangeText(h.changePct)
                }
            }
            Divider(color = InopayColors.BorderSoft, thickness = 0.5.dp)
        }

        if (viewModel.nextDividendAmount != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(8.dp))
                    .padding(10.dp),
            ) {
                Text("💸 Dividendes prévus", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "+ ${viewModel.nextDividendAmount.fcfa()} FCFA en ${viewModel.nextDividendWhen} (${viewModel.nextDividendFrom})",
                    fontSize = 11.sp, color = InopayColors.TextMuted,
                )
            }
        }
    }
}
