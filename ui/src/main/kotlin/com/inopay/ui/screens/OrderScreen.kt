// Screen 07 — Passer un ordre
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.*
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme
import kotlin.math.roundToInt

@Composable
fun OrderScreen(
    symbol: String,
    name: String,
    price: Double,
    sgiName: String,
    sgiCommissionPct: Double,
    fundLabel: String,
    fundId: String,
    fundBalance: String,
    onDraft: (qty: Int, total: Double) -> Unit,
) {
    val theme = LocalInopayTheme.current
    var qty by remember { mutableStateOf(50) }
    var orderType by remember { mutableStateOf("market") }

    val gross = qty * price
    val commission = (gross * sgiCommissionPct / 100).roundToInt().toDouble()
    val tax = (gross * 0.0015).roundToInt().toDouble()
    val total = gross + commission + tax

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Acheter $name", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // Order type tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(4.dp),
        ) {
            listOf("market" to "Au marché", "limit" to "Cours limité").forEach { (key, label) ->
                val active = orderType == key
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(if (active) Color.White else Color.Transparent, RoundedCornerShape(6.dp))
                        .clickable { orderType = key }
                        .padding(vertical = 6.dp),
                ) {
                    Text(label, fontSize = 11.sp, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (active) Color.Black else InopayColors.TextMuted)
                }
            }
        }

        // Quantity stepper
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = { if (qty > 1) qty -= 1 }) {
                Icon(Icons.Filled.RemoveCircle, contentDescription = "moins", tint = theme.primary, modifier = Modifier.size(28.dp))
            }
            Text("$qty", fontSize = 36.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
            IconButton(onClick = { qty += 1 }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "plus", tint = theme.primary, modifier = Modifier.size(28.dp))
            }
        }
        Text("actions", fontSize = 12.sp, color = InopayColors.TextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(
            "Cours estimé : ${price.fcfa()} FCFA / action",
            fontSize = 11.sp, color = InopayColors.TextMuted,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )

        // Fee recap
        InopayCard {
            InopayCardRow("$qty × ${price.fcfa()} FCFA", "${gross.fcfa()} FCFA", valueBold = true)
            InopayCardRow("Commission $sgiName", "${commission.fcfa()} FCFA")
            InopayCardRow("Taxe BRVM", "${tax.fcfa()} FCFA")
            InopayCardRow("Total", "${total.fcfa()} FCFA", valueBold = true, isTotal = true)
        }

        // Fund card
        InopayCard {
            InopayCardRow(fundLabel, fundId, valueBold = true)
            InopayCardRow("Disponible", fundBalance)
        }

        Text(
            "⚠ Le prix peut évoluer entre votre validation et l'exécution sur le marché.",
            fontSize = 11.sp, color = InopayColors.TextMuted,
        )

        InopayCta("Vérifier l'ordre →") { onDraft(qty, total) }
    }
}
