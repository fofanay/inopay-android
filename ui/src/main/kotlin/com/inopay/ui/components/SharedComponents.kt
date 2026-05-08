// Composants Compose partagés entre les 14 écrans.
// Tous lisent le theme via LocalInopayTheme.current.

package com.inopay.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inopay.ui.theme.InopayColors
import com.inopay.ui.theme.LocalInopayTheme

// ─── App bar ──────────────────────────────────────────────────────
@Composable
fun InopayAppBar(onClose: (() -> Unit)? = null) {
    val theme = LocalInopayTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.primary)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "${theme.partnerName} · Investir",
            color = theme.onPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (onClose != null) {
            IconButton(onClick = onClose) {
                Text("×", color = theme.onPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── CTA buttons ──────────────────────────────────────────────────
enum class InopayCtaStyle { Accent, Strong, Danger }

@Composable
fun InopayCta(
    label: String,
    style: InopayCtaStyle = InopayCtaStyle.Accent,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val theme = LocalInopayTheme.current
    val (bg, fg) = when (style) {
        InopayCtaStyle.Accent -> theme.accent to Color(0xFF111111)
        InopayCtaStyle.Strong -> theme.primary to theme.onPrimary
        InopayCtaStyle.Danger -> Color(0xFFB91C1C) to Color.White
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        shape = RoundedCornerShape(theme.cornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
    }
}

@Composable
fun InopayOutlineCta(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val theme = LocalInopayTheme.current
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.5.dp, theme.primary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.primary),
        shape = RoundedCornerShape(theme.cornerRadius),
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
fun InopayLink(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
            Text(label, fontSize = 12.sp, color = InopayColors.TextSoft, textDecoration = TextDecoration.Underline)
        }
    }
}

// ─── Card containers ──────────────────────────────────────────────
@Composable
fun InopayCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InopayColors.BgSoft, RoundedCornerShape(10.dp))
            .border(1.dp, InopayColors.BorderSoft, RoundedCornerShape(10.dp))
            .padding(12.dp),
        content = content,
    )
}

@Composable
fun InopayCardRow(
    label: String,
    value: String,
    valueBold: Boolean = false,
    valueColor: Color? = null,
    isTotal: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(label, fontSize = 12.sp, color = InopayColors.TextMuted, modifier = Modifier.weight(1f))
        Text(
            value,
            fontSize = if (isTotal) 14.sp else 12.sp,
            fontWeight = if (valueBold) FontWeight.SemiBold else FontWeight.Normal,
            color = valueColor ?: Color.Unspecified,
        )
    }
    Divider(color = InopayColors.BorderSoft, thickness = 0.5.dp)
}

// ─── Step badge / Status pill ─────────────────────────────────────
@Composable
fun InopayStepBadge(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = InopayColors.TextSoft,
        modifier = Modifier
            .background(Color(0xFFF0F0F0), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
fun InopayStatusPill(text: String, bg: Color = InopayColors.WarnBg, fg: Color = InopayColors.Warn) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

// ─── Big icon (success / warn / error / info) ─────────────────────
enum class IconKind { Success, Warn, Error, Info }

@Composable
fun InopayBigIcon(kind: IconKind, big: Boolean = false) {
    val (bg, fg, symbol) = when (kind) {
        IconKind.Success -> Triple(InopayColors.SuccessBg, InopayColors.Success, "✓")
        IconKind.Warn -> Triple(InopayColors.WarnBg, InopayColors.Warn, "!")
        IconKind.Error -> Triple(InopayColors.ErrorBg, InopayColors.Error, "⚠")
        IconKind.Info -> Triple(InopayColors.InfoBg, InopayColors.Info, "🕐")
    }
    val size = if (big) 72.dp else 56.dp
    Box(
        modifier = Modifier
            .size(size)
            .background(bg, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, fontSize = if (big) 38.sp else 30.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

// ─── Logo circle for instruments ──────────────────────────────────
@Composable
fun InopayLogoCircle(letter: String, color: Color, big: Boolean = false) {
    val size = if (big) 44.dp else 36.dp
    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(letter, color = Color.White, fontWeight = FontWeight.Bold, fontSize = if (big) 16.sp else 12.sp)
    }
}

// ─── Disclaimer ───────────────────────────────────────────────────
@Composable
fun InopayDisclaimer(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        color = InopayColors.TextMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFAEB), RoundedCornerShape(6.dp))
            .padding(10.dp),
    )
}

// ─── Up/down change text ──────────────────────────────────────────
@Composable
fun InopayChangeText(pct: Double, prefix: String = "") {
    val color = if (pct >= 0) InopayColors.Success else Color(0xFFDC2626)
    val sign = if (pct >= 0) "+" else ""
    Text("$prefix$sign${"%.1f".format(pct)} %", color = color, fontSize = 11.sp)
}

// ─── Helper : annotated text with bold spans (for "**inline bold**") ──
@Composable
fun annotatedBold(parts: List<Pair<String, Boolean>>) = buildAnnotatedString {
    for ((text, bold) in parts) {
        if (bold) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text) } else append(text)
    }
}
