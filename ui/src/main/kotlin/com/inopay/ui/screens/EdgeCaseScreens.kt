// Edge cases — Screens 11 / 12 / 13 / 14
// Regroupés dans un seul fichier (chacun reste compact).

package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.*
import com.inopay.ui.models.MarketClosedViewModel
import com.inopay.ui.models.OrderRejectedViewModel
import com.inopay.ui.models.RevokeViewModel
import com.inopay.ui.models.fcfa
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

// ─── 11 KYC à compléter ────────────────────────────────────────────
@Composable
fun KycIncompleteScreen(onUpload: () -> Unit, onSkip: () -> Unit) {
    val theme = LocalInopayTheme.current
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            InopayBigIcon(IconKind.Warn)
        }
        Text("Une pièce manque pour KYC2+", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            annotatedBold(listOf(
                theme.partnerName to true,
                " a vérifié votre identité au niveau KYC1. Pour acheter au-dessus de " to false,
                "500 000 FCFA / mois" to true,
                ", complétez :" to false,
            )),
            fontSize = 13.sp, color = InopayColors.TextMuted,
        )
        InopayCard {
            DocRow("✓ Pièce d'identité (recto/verso)", done = true)
            DocRow("✓ Selfie de vivacité", done = true)
            DocRow("⌛ Justificatif de domicile (facture < 3 mois)", done = false)
        }
        Text(
            "📎 Acceptés : facture SENELEC/SODECI, relevé bancaire, attestation employeur. Le document sera analysé par IA et un humain (SGI) en moins de 24h.",
            fontSize = 11.sp, color = InopayColors.TextMuted,
        )
        InopayCta("📷 Téléverser maintenant", onClick = onUpload)
        InopayLink("Continuer en KYC1 (limite 500 000 FCFA)", onClick = onSkip)
    }
}

@Composable
private fun DocRow(text: String, done: Boolean) {
    Text(
        text,
        fontSize = 12.sp,
        color = if (done) Color(0xFF166534) else Color(0xFF92400E),
        modifier = Modifier
            .fillMaxWidth()
            .background(if (done) Color(0xFFF0FDF4) else Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
            .padding(8.dp),
    )
}

// ─── 12 Ordre rejeté ───────────────────────────────────────────────
@Composable
fun OrderRejectedScreen(viewModel: OrderRejectedViewModel, onRecharge: () -> Unit, onReduce: () -> Unit) {
    val missing = (viewModel.total - viewModel.available).coerceAtLeast(0.0)
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { InopayBigIcon(IconKind.Error) }
        Text("Ordre non transmis", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = InopayColors.Error)
        Text("Solde insuffisant sur votre ${viewModel.fundLabel.lowercase()}.", fontSize = 13.sp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                .padding(12.dp),
        ) {
            InopayCardRow("Total ordre", "${viewModel.total.fcfa()} FCFA", valueBold = true)
            InopayCardRow("${viewModel.fundLabel} disponible", "${viewModel.available.fcfa()} FCFA", valueBold = true)
            InopayCardRow("Manque", "${missing.fcfa()} FCFA", valueBold = true, valueColor = InopayColors.Error, isTotal = true)
        }
        Text(
            annotatedBold(listOf(
                "L'ordre n'a " to false,
                "pas été transmis" to true,
                " à la SGI. Aucun frais débité. Aucun ordre en attente." to false,
            )),
            fontSize = 11.sp, color = InopayColors.TextMuted,
        )
        InopayCta("Approvisionner mon compte", onClick = onRecharge)
        InopayLink("Réduire la quantité", onClick = onReduce)
    }
}

// ─── 13 Marché fermé ───────────────────────────────────────────────
@Composable
fun MarketClosedScreen(viewModel: MarketClosedViewModel, onQueue: () -> Unit, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { InopayBigIcon(IconKind.Info) }
        Text("BRVM fermée actuellement", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            annotatedBold(listOf(
                "La séance BRVM se tient du " to false,
                "lundi au vendredi, 09:00–15:00 GMT" to true,
                ". Hors séance, votre ordre est mis en file pour la prochaine ouverture." to false,
            )),
            fontSize = 13.sp, color = InopayColors.TextMuted,
        )
        InopayCard {
            InopayCardRow("Action", "${if (viewModel.side == "buy") "ACHETER" else "VENDRE"} ${viewModel.qty} ${viewModel.symbol}", valueBold = true)
            InopayCardRow("Montant immobilisé", "${viewModel.total.fcfa()} FCFA", valueBold = true)
            InopayCardRow("Prochaine séance", viewModel.nextSession, valueBold = true)
            InopayCardRow("Exécution prévue", viewModel.expectedExecution, valueBold = true)
        }
        Text(
            annotatedBold(listOf(
                "⏳ Vous pouvez " to false,
                "annuler sans frais" to true,
                " tant que l'ordre n'est pas exécuté." to false,
            )),
            fontSize = 11.sp, color = InopayColors.TextMuted,
        )
        InopayCta("Mettre en file", onClick = onQueue)
        InopayLink("Annuler", onClick = onCancel)
    }
}

// ─── 14 Révoquer consentement ──────────────────────────────────────
@Composable
fun RevokeScreen(viewModel: RevokeViewModel, onConfirm: (reason: String) -> Unit, onCancel: () -> Unit) {
    var reason by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Révoquer mon consentement SGI", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Vous êtes sur le point de révoquer le consentement donné à ${viewModel.sgiName}.",
            fontSize = 13.sp, color = InopayColors.TextMuted)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF7ED), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFFDBA74), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("⚠ Conséquences immédiates", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Bullet("Vous ne pourrez plus passer de nouveaux ordres tant qu'un consentement actif n'est pas en place.")
            Bullet("Vos positions existantes sont conservées.")
            Bullet("Les ordres en attente seront annulés.")
            Bullet("Le journal d'audit conserve la trace (FATF Rec. 11).")
        }
        InopayCard {
            InopayCardRow("Consentement actuel", viewModel.sgiName, valueBold = true)
            InopayCardRow("Donné le", viewModel.consentGivenAt)
            InopayCardRow("Ordres exécutés", "${viewModel.ordersExecuted}", valueBold = true)
        }
        Column {
            Text("Raison (optionnel)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = InopayColors.TextSoft)
            TextField(
                value = reason, onValueChange = { reason = it },
                placeholder = { Text("Ex : changement de SGI préférée...", fontSize = 12.sp) },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(80.dp),
            )
        }
        InopayCta("Révoquer maintenant", style = InopayCtaStyle.Danger) { onConfirm(reason) }
        InopayLink("Annuler", onClick = onCancel)
    }
}

@Composable
private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•", color = Color(0xFFEA580C))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp)
    }
}
