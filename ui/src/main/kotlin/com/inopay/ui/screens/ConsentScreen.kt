// Screen 04 — Consentement SGI
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.*
import com.inopay.ui.models.ConsentSgi
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

@Composable
fun ConsentScreen(sgi: ConsentSgi, onGrant: (sgiId: String, sgiName: String) -> Unit) {
    val theme = LocalInopayTheme.current
    var consent by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        InopayStepBadge("Étape 3/3")
        Text("Votre courtier en bourse", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "Pour exécuter vos ordres sur la BRVM, ${theme.partnerName} collabore avec une SGI agréée CREPMF.",
            fontSize = 13.sp, color = InopayColors.TextMuted,
        )

        // SGI card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(2.dp, InopayColors.Border, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).background(theme.primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(sgi.name.take(1), color = theme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(sgi.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("SGI agréée CREPMF · ${sgi.market}", fontSize = 11.sp, color = InopayColors.TextMuted)
                }
            }
            Divider(color = InopayColors.BorderSoft)
            Row {
                Stat(sgi.activeSince ?: "—", "Active depuis")
                Stat("%.2f %%".format(sgi.commissionPct), "Commission ordre")
                Stat(sgi.rating ?: "—", "Notation Inopay")
            }
        }

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F9FF), RoundedCornerShape(8.dp))
                .padding(12.dp),
        ) {
            Checkbox(checked = consent, onCheckedChange = { consent = it })
            Spacer(Modifier.width(8.dp))
            Text(
                annotatedBold(listOf(
                    "J'autorise " to false,
                    theme.partnerName to true,
                    " à transmettre mes ordres à " to false,
                    sgi.name to true,
                    " pour exécution sur la BRVM, dans les limites du règlement CREPMF n° 16/2017." to false,
                )),
                fontSize = 12.sp,
            )
        }

        Text(
            "🔓 Vous pourrez révoquer ce consentement à tout moment depuis Réglages → Investir → Courtier.",
            fontSize = 11.sp, color = InopayColors.TextMuted,
        )

        InopayCta("Activer Investir →", enabled = consent) {
            if (consent) onGrant(sgi.id, sgi.name)
        }
    }
}

@Composable
private fun RowScope.Stat(value: String, label: String) {
    Column(modifier = Modifier.weight(1f)) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 10.sp, color = InopayColors.TextMuted)
    }
}
