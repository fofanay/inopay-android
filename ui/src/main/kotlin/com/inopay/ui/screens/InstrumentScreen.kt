// Screen 06 — Détail instrument
package com.inopay.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.InopayCta
import com.inopay.ui.components.InopayLogoCircle
import com.inopay.ui.components.InopayOutlineCta
import com.inopay.ui.models.InstrumentDetailViewModel
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

@Composable
fun InstrumentScreen(viewModel: InstrumentDetailViewModel, onBuy: () -> Unit, onSell: () -> Unit = {}) {
    val theme = LocalInopayTheme.current
    var activeRange by remember { mutableStateOf("1S") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            InopayLogoCircle(viewModel.symbol.take(1), viewModel.logoColor, big = true)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(viewModel.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${viewModel.symbol} · BRVM · ${viewModel.sector}", fontSize = 12.sp, color = InopayColors.TextMuted)
            }
        }

        // Big price
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(viewModel.lastPrice.fcfa(), fontSize = 32.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(4.dp))
                Text("FCFA", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = InopayColors.TextMuted)
            }
            val sign = if (viewModel.changePct >= 0) "+" else ""
            val color = if (viewModel.changePct >= 0) InopayColors.Success else Color(0xFFDC2626)
            Text(
                "$sign${viewModel.changeAbs.fcfa()} ($sign${"%.1f".format(viewModel.changePct)} %) · Aujourd'hui",
                fontSize = 13.sp, color = color,
            )
        }

        // Time tabs
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("1J", "1S", "1M", "6M", "1A", "Tout").forEach { r ->
                val active = activeRange == r
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(if (active) Color(0xFFF0F0F0) else Color.Transparent, RoundedCornerShape(4.dp))
                        .clickable { activeRange = r }
                        .padding(vertical = 4.dp),
                ) {
                    Text(r, fontSize = 11.sp, color = if (active) Color.Black else InopayColors.TextMuted,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }

        // Sparkline
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val pts = listOf(80f, 75f, 72f, 68f, 55f, 60f, 45f, 52f, 40f, 32f, 28f, 18f)
            val stepX = size.width / (pts.size - 1)
            val path = Path().apply {
                pts.forEachIndexed { i, v ->
                    val x = i * stepX
                    val y = v * size.height / 100f
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(path, color = theme.primary, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
            // Fill underneath
            val fillPath = Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(fillPath, color = theme.primary.copy(alpha = 0.15f))
        }

        Text("À propos", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(viewModel.about, fontSize = 13.sp, color = InopayColors.TextMuted)

        // Metrics grid (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCell(viewModel.metricCapi, "Capi. FCFA", Modifier.weight(1f))
                MetricCell(viewModel.metricPER, "PER", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCell(viewModel.metricDividend, "Dividende", Modifier.weight(1f))
                MetricCell(viewModel.metricVolume, "Vol. jour", Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InopayOutlineCta("Vendre", onClick = onSell, modifier = Modifier.weight(1f))
            Box(Modifier.weight(1f)) {
                InopayCta("Acheter", onClick = onBuy)
            }
        }
    }
}

@Composable
private fun MetricCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(InopayColors.BgSoft, RoundedCornerShape(8.dp))
            .padding(10.dp),
    ) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 11.sp, color = InopayColors.TextMuted)
    }
}
