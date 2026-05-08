// Screen 09 — Reçu signé Ed25519
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.*
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors

@Composable
fun ReceiptScreen(
    orderId: String,
    side: String = "buy",
    qty: Int,
    symbol: String,
    total: Double,
    sgiName: String,
    signatureB64: String? = null,
    hashSHA256: String? = null,
    executionEta: String = "Demain · BRVM · 10:30 GMT",
    onPortfolio: () -> Unit,
    onDownloadPDF: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        InopayBigIcon(IconKind.Success, big = true)
        Text("Ordre passé !", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Référence #$orderId", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = InopayColors.TextMuted)
        InopayStatusPill("⏳ En attente d'exécution marché")
        Text(
            annotatedBold(listOf(
                "Exécution prévue : " to false,
                executionEta to true,
            )),
            fontSize = 13.sp, color = InopayColors.TextMuted, textAlign = TextAlign.Center,
        )

        InopayCard {
            InopayCardRow("Action", "${if (side == "buy") "ACHETER" else "VENDRE"} $qty $symbol", valueBold = true)
            InopayCardRow("Montant immobilisé", "${total.fcfa()} FCFA", valueBold = true)
            InopayCardRow("SGI exécutante", sgiName, valueBold = true)
        }

        // Trust card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F9FF), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("🔒 Reçu signé Ed25519", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "Vérifiable hors-ligne via api.getinopay.com/.well-known/inopay-kyc-pubkey.pem",
                fontSize = 10.sp, color = InopayColors.TextMuted,
            )
            if (hashSHA256 != null) {
                Text(
                    "SHA-256: $hashSHA256",
                    fontSize = 10.sp, fontFamily = FontFamily.Monospace,
                    color = InopayColors.TextMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(6.dp),
                )
            }
        }

        InopayCta("Voir mon portefeuille", onClick = onPortfolio)
        InopayLink("⬇ Télécharger l'attestation (PDF)", onClick = onDownloadPDF)
    }
}
