// Screen 05 — Marchés BRVM
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.InopayChangeText
import com.inopay.ui.components.InopayLogoCircle
import com.inopay.ui.models.MarketInstrument
import com.inopay.ui.models.MarketsViewModel
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors

@Composable
fun MarketsScreen(viewModel: MarketsViewModel, onSelect: (MarketInstrument) -> Unit) {
    var activeTab by remember { mutableStateOf("Tous") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(4.dp),
        ) {
            listOf("Tous", "Actions", "Obligations", "Fonds").forEach { tab ->
                val active = activeTab == tab
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(if (active) Color.White else Color.Transparent, RoundedCornerShape(6.dp))
                        .clickable { activeTab = tab }
                        .padding(vertical = 6.dp),
                ) {
                    Text(tab, fontSize = 11.sp, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (active) Color.Black else InopayColors.TextMuted)
                }
            }
        }

        // Search bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(InopayColors.BgSoft2, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = InopayColors.TextMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(8.dp))
            Text("Rechercher SNTS, ORANGE...", fontSize = 12.sp, color = InopayColors.TextMuted)
        }

        // Index strip
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.indices.forEach { idx ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(InopayColors.BgSoft, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                ) {
                    Text(idx.name, fontSize = 11.sp, color = InopayColors.TextMuted)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("%.2f".format(idx.value), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        InopayChangeText(idx.changePct)
                    }
                }
            }
        }

        Text("Tendances aujourd'hui", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            viewModel.trending.forEach { instr ->
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, InopayColors.Border, RoundedCornerShape(8.dp))
                        .clickable { onSelect(instr) }
                        .padding(10.dp),
                ) {
                    Text(instr.symbol, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(instr.name, fontSize = 11.sp, maxLines = 1)
                    InopayChangeText(instr.changePct)
                }
            }
        }

        Text("Populaires", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

        viewModel.popular.forEach { i ->
            Column(modifier = Modifier.fillMaxWidth().clickable { onSelect(i) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                ) {
                    InopayLogoCircle(i.logoLetter, i.logoColor)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(i.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${i.symbol} · ${i.sector}", fontSize = 11.sp, color = InopayColors.TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(i.lastPrice.fcfa(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        InopayChangeText(i.changePct)
                    }
                }
                Divider(color = InopayColors.BorderSoft, thickness = 0.5.dp)
            }
        }
    }
}
