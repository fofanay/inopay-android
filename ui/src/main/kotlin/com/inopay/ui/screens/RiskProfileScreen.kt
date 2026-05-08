// Screen 02 — Profil de risque
package com.inopay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.components.InopayCta
import com.inopay.ui.components.InopayStepBadge
import com.inopay.ui.theme.InopayColors

@Composable
fun RiskProfileScreen(onNext: () -> Unit) {
    var q1 by remember { mutableStateOf("b") }
    var q2 by remember { mutableStateOf("b") }
    var q3 by remember { mutableStateOf("b") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InopayStepBadge("Étape 1/3")
        Text("Votre profil d'investisseur", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("3 questions rapides pour adapter notre offre.", fontSize = 13.sp, color = InopayColors.TextMuted)

        Question("Combien de temps pouvez-vous laisser cet argent investi ?",
            options = listOf("a" to "Moins de 2 ans", "b" to "2 à 5 ans", "c" to "Plus de 5 ans"),
            selected = q1, onSelect = { q1 = it })
        Question("Si votre placement perd 20 % en 1 mois, vous...",
            options = listOf("a" to "Vendez tout", "b" to "Attendez la remontée", "c" to "Achetez plus"),
            selected = q2, onSelect = { q2 = it })
        Question("Quel est votre objectif ?",
            options = listOf("a" to "Préserver mon épargne", "b" to "Faire fructifier modérément", "c" to "Croissance long-terme"),
            selected = q3, onSelect = { q3 = it })

        InopayCta("Suivant →", onClick = onNext)
    }
}

@Composable
private fun Question(prompt: String, options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InopayColors.BgSoft, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(prompt, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        options.forEach { (key, label) ->
            val isSelected = selected == key
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) Color(0xFFF0FDF4) else Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, if (isSelected) InopayColors.Success else InopayColors.Border, RoundedCornerShape(8.dp))
                    .selectable(selected = isSelected, onClick = { onSelect(key) })
                    .padding(10.dp),
            ) {
                RadioButton(selected = isSelected, onClick = null)
                Spacer(Modifier.width(8.dp))
                Text(label, fontSize = 12.sp)
            }
        }
    }
}
