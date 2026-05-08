// Screen 08 — Récap avant validation
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.*
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

@Composable
fun ConfirmationScreen(
    side: String = "buy",
    symbol: String,
    name: String,
    qty: Int,
    price: Double,
    total: Double,
    sgiName: String,
    fundId: String,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    val theme = LocalInopayTheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Vérifiez votre ordre", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // Confirm card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(2.dp, theme.primary, RoundedCornerShape(12.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(theme.primary)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(if (side == "buy") "ACHETER" else "VENDRE",
                    color = theme.onPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                Text(annotatedBold(listOf(name to true, " · $symbol" to false)), fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text("$qty actions × ${price.fcfa()} FCFA", fontSize = 12.sp, color = InopayColors.TextMuted)
            }
            Divider(color = InopayColors.BorderSoft)
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            ) {
                Text(total.fcfa(), fontSize = 28.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(4.dp))
                Text("FCFA", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = InopayColors.TextMuted)
            }
        }

        InopayCard {
            InopayCardRow("Type d'ordre", "Au marché", valueBold = true)
            InopayCardRow("SGI exécutante", sgiName, valueBold = true)
            InopayCardRow("Compte source", fundId, valueBold = true)
            InopayCardRow("Exécution prévue", "Demain · BRVM 10:30 GMT")
        }

        Text(
            annotatedBold(listOf(
                "Une fois validé, l'ordre est transmis à $sgiName. La somme sera " to false,
                "immobilisée" to true,
                " sur votre compte jusqu'à exécution." to false,
            )),
            fontSize = 11.sp, color = InopayColors.TextMuted,
        )

        InopayCta("Valider mon ordre", style = InopayCtaStyle.Strong, onClick = onSubmit)
        InopayLink("Modifier", onClick = onCancel)
    }
}
