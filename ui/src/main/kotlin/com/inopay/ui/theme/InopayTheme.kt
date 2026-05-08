// InopayTheme — palette + typography utilisée par les 14 écrans Compose.
// Le partenaire crée une instance et la passe à InopayInvestScreen, ou laisse
// le default qui sera ré-fetchée depuis /v1/embed-theme/:partnerKey au premier
// LaunchedEffect.

package com.inopay.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class InopayTheme(
    val primary: Color,
    val primaryDark: Color,
    val accent: Color,
    val onPrimary: Color = Color.White,
    val cornerRadius: Dp = 8.dp,
    val partnerName: String,
    val partnerLogoUrl: String? = null,
) {
    companion object {
        val Default = InopayTheme(
            primary = Color(0xFF1B3A5C),
            primaryDark = Color(0xFF10243B),
            accent = Color(0xFFD4942F),
            partnerName = "Inopay",
        )
        val BanqueAtlas = InopayTheme(
            primary = Color(0xFF1B3A5C),
            primaryDark = Color(0xFF10243B),
            accent = Color(0xFFD4942F),
            partnerName = "Banque Atlas",
        )
        val WaveCash = InopayTheme(
            primary = Color(0xFF5E2D91),
            primaryDark = Color(0xFF3F1F62),
            accent = Color(0xFFF4A261),
            partnerName = "WaveCash",
        )
        val DiasporaSend = InopayTheme(
            primary = Color(0xFF1F7A5A),
            primaryDark = Color(0xFF0F4D38),
            accent = Color(0xFFE9C46A),
            partnerName = "DiasporaSend",
        )
    }
}

val LocalInopayTheme = staticCompositionLocalOf { InopayTheme.Default }

@Composable
fun ProvideInopayTheme(theme: InopayTheme, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalInopayTheme provides theme, content = content)
}

// MARK: - Couleurs partagées (non liées au theme partenaire)
object InopayColors {
    val Bg = Color.White
    val BgSoft = Color(0xFFF9F9FB)
    val BgSoft2 = Color(0xFFF5F5F7)
    val Border = Color(0xFFE5E5E5)
    val BorderSoft = Color(0xFFF0F0F0)
    val TextSoft = Color(0xFF555555)
    val TextMuted = Color(0xFF777777)
    val Success = Color(0xFF16A34A)
    val SuccessBg = Color(0xFFDCFCE7)
    val Warn = Color(0xFFB45309)
    val WarnBg = Color(0xFFFEF3C7)
    val Error = Color(0xFFB91C1C)
    val ErrorBg = Color(0xFFFEE2E2)
    val Info = Color(0xFF1D4ED8)
    val InfoBg = Color(0xFFDBEAFE)
}
