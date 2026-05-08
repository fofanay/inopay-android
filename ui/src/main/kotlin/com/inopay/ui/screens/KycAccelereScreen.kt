// Screen 03 — KYC accéléré (différenciateur clé)
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.*
import com.inopay.ui.models.KycViewModel
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

@Composable
fun KycAccelereScreen(viewModel: KycViewModel, onConfirm: (nif: String, profession: String) -> Unit) {
    val theme = LocalInopayTheme.current
    var nif by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf(viewModel.professionDefault) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Speed pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(theme.primary, theme.primaryDark)), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text("⚡", fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("≈ 30 secondes", color = theme.onPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("au lieu de ~12 min de re-saisie", color = theme.onPrimary.copy(alpha = 0.85f), fontSize = 11.sp)
            }
        }

        Text(
            "Bonjour ${viewModel.firstName}, on reconnaît votre KYC ✓",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            annotatedBold(listOf(
                theme.partnerName to true,
                " ${viewModel.partnerKycContext}. Inopay " to false,
                "réutilise tout" to true,
                " — vous ne ressaisissez rien." to false,
            )),
            fontSize = 13.sp,
            color = InopayColors.TextMuted,
        )

        // Import counter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(2.dp, theme.primary, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${viewModel.importedItems.size}", fontSize = 30.sp, fontWeight = FontWeight.Black, color = theme.primary)
                    Text("/${viewModel.totalItems}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = InopayColors.TextMuted)
                }
                LinearProgressIndicator(
                    progress = viewModel.importedItems.size.toFloat() / viewModel.totalItems,
                    color = theme.primary,
                    trackColor = InopayColors.Border,
                    modifier = Modifier.width(100.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                annotatedBold(listOf(
                    "éléments KYC importés depuis " to false,
                    theme.partnerName to true,
                )),
                fontSize = 12.sp,
            )
        }

        // Imported list
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(InopayColors.Success, CircleShape))
            Spacer(Modifier.width(6.dp))
            Text("Déjà vérifié et signé Ed25519", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        viewModel.importedItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InopayColors.SuccessBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                Text("✓", color = InopayColors.Success, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(item.detail, fontSize = 10.sp, color = InopayColors.TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(item.source, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Text(item.date, fontSize = 9.sp, color = InopayColors.TextMuted)
                }
            }
        }

        // Missing fields
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(theme.accent, CircleShape))
            Spacer(Modifier.width(6.dp))
            Text("2 infos spécifiques investissement", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        TextField(
            value = nif, onValueChange = { nif = it },
            label = { Text("NIF (numéro fiscal) · optionnel", fontSize = 11.sp) },
            placeholder = { Text("N0000000000A") },
            singleLine = true,
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFFAFAFA), focusedContainerColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = profession, onValueChange = { profession = it },
            label = { Text("Profession actuelle", fontSize = 11.sp) },
            singleLine = true,
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFFAFAFA), focusedContainerColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
        )

        // Trust chain
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFAFBFF), RoundedCornerShape(8.dp))
                .border(1.dp, theme.primary.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("🔒 Chaîne d'audit signée Ed25519", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChainNode(theme.partnerName)
                Text(" → ", color = InopayColors.TextMuted, fontSize = 12.sp)
                ChainNode("Inopay")
                Text(" → ", color = InopayColors.TextMuted, fontSize = 12.sp)
                ChainNode("SGI Hudson")
            }
            Text(
                "Données importées scellées par ${theme.partnerName}. Inopay ne peut pas les modifier — vérifiable hors-ligne.",
                fontSize = 10.sp, color = InopayColors.TextMuted,
            )
        }

        InopayCta("Confirmer →", style = InopayCtaStyle.Strong) { onConfirm(nif, profession) }
        InopayLink("Voir le détail des données importées") {}
    }
}

@Composable
private fun ChainNode(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(4.dp))
            .border(1.dp, InopayColors.Border, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
