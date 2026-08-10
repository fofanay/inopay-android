// View models for the 14 screens (data classes — no Compose dependencies).

package com.inopay.ui.models

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale

// ─── KYC accéléré (03) ─────────────────────────────────────────────
data class KycImportedItem(
    val id: String,
    val title: String,
    val detail: String,
    val source: String,
    val date: String,
)

data class KycViewModel(
    val firstName: String,
    val partnerKycContext: String,
    val importedItems: List<KycImportedItem>,
    val totalItems: Int = 8,
    val professionDefault: String = "",
) {
    companion object {
        fun demo(partnerName: String, firstName: String = "Aïssata"): KycViewModel = KycViewModel(
            firstName = firstName,
            partnerKycContext = "vous a déjà identifié pour l'usage de votre compte",
            importedItems = listOf(
                KycImportedItem("1", "Identité déclarée", "Nom, prénom, date de naissance", partnerName, ""),
                KycImportedItem("2", "Pièce d'identité", "Déposée · non vérifiée", partnerName, ""),
                KycImportedItem("3", "Photo du visage", "Déposée · vivacité non vérifiée", partnerName, ""),
                KycImportedItem("4", "Justificatif de domicile", "Déposé", partnerName, ""),
                KycImportedItem("5", "Adresse postale", "Renseignée", partnerName, ""),
                KycImportedItem("6", "Téléphone", "Renseigné · non confirmé", partnerName, ""),
            ),
            totalItems = 8,
        )
    }
}

// ─── Consent (04) ──────────────────────────────────────────────────
data class ConsentSgi(
    val id: String,
    val name: String,
    val market: String,
    val commissionPct: Double,
    val rating: String? = null,
    val activeSince: String? = null,
) {
    companion object {
        val HudsonDemo = ConsentSgi(id = "hudson", name = "Hudson & Cie", market = "BRVM",
            commissionPct = 0.30, rating = "A+", activeSince = "2008")
    }
}

// ─── Markets (05) ──────────────────────────────────────────────────
data class MarketInstrument(
    val symbol: String,
    val name: String,
    val sector: String,
    val lastPrice: Double,
    val changePct: Double,
    val logoColor: Color = Color(0xFFFF6900),
    val logoLetter: String = symbol.take(1),
)

data class MarketIndex(
    val name: String,
    val value: Double,
    val changePct: Double,
)

data class MarketsViewModel(
    val indices: List<MarketIndex>,
    val trending: List<MarketInstrument>,
    val popular: List<MarketInstrument>,
) {
    companion object {
        val Demo = MarketsViewModel(
            indices = listOf(
                MarketIndex("BRVM-Composite", 285.42, 1.2),
                MarketIndex("BRVM-30", 142.18, 0.8),
            ),
            trending = listOf(
                MarketInstrument("SNTS", "Sonatel", "Télécoms", 19500.0, 3.4),
                MarketInstrument("ORAC", "Orange CI", "Télécoms", 7250.0, 1.8, Color(0xFFFF7900)),
                MarketInstrument("SLBC", "Solibra", "Boissons", 14200.0, -0.5, Color(0xFFC8102E), logoLetter = "SO"),
            ),
            popular = listOf(
                MarketInstrument("SNTS", "Sonatel", "Télécoms", 19500.0, 3.4),
                MarketInstrument("ORAC", "Orange CI", "Télécoms", 7250.0, 1.8, Color(0xFFFF7900)),
                MarketInstrument("NSBC", "NSIA Banque CI", "Banque", 5100.0, 0.9, Color(0xFF0066B3), logoLetter = "N"),
                MarketInstrument("SLBC", "Solibra", "Boissons", 14200.0, -0.5, Color(0xFFC8102E), logoLetter = "SO"),
            ),
        )
    }
}

// ─── Instrument detail (06) ────────────────────────────────────────
data class InstrumentDetailViewModel(
    val symbol: String,
    val name: String,
    val sector: String,
    val lastPrice: Double,
    val changePct: Double,
    val changeAbs: Double,
    val about: String,
    val metricCapi: String,
    val metricPER: String,
    val metricDividend: String,
    val metricVolume: String,
    val logoColor: Color = Color(0xFFFF6900),
) {
    companion object {
        val SonatelDemo = InstrumentDetailViewModel(
            symbol = "SNTS", name = "Sonatel", sector = "Télécoms",
            lastPrice = 19500.0, changePct = 3.4, changeAbs = 650.0,
            about = "Sonatel est l'opérateur télécom historique du Sénégal, leader régional avec ~30 millions d'abonnés. Coté BRVM depuis 1998. Dividende historique stable.",
            metricCapi = "1,95 T", metricPER = "8,2", metricDividend = "5,1 %", metricVolume = "~35 K"
        )
    }
}

// ─── Portfolio (10) ────────────────────────────────────────────────
data class PortfolioHolding(
    val symbol: String,
    val name: String,
    val qty: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val changePct: Double,
    val logoColor: Color = Color(0xFFFF6900),
    val logoLetter: String = symbol.take(1),
)

data class PortfolioAllocationSlice(
    val label: String,
    val pct: Int,
    val color: Color,
)

data class PortfolioViewModel(
    val totalValue: Double,
    val totalInvested: Double,
    val gain: Double,
    val gainPct: Double,
    val allocation: List<PortfolioAllocationSlice>,
    val holdings: List<PortfolioHolding>,
    val nextDividendAmount: Double? = null,
    val nextDividendFrom: String? = null,
    val nextDividendWhen: String? = null,
    val beneficiaryLabel: String? = null,
) {
    companion object {
        val Demo = PortfolioViewModel(
            totalValue = 1_654_200.0, totalInvested = 1_540_000.0, gain = 114_200.0, gainPct = 7.4,
            allocation = listOf(
                PortfolioAllocationSlice("Télécoms", 48, Color(0xFF3B82F6)),
                PortfolioAllocationSlice("Banque", 30, Color(0xFF10B981)),
                PortfolioAllocationSlice("Boissons", 14, Color(0xFFF59E0B)),
                PortfolioAllocationSlice("Autres", 8, Color(0xFFA855F7)),
            ),
            holdings = listOf(
                PortfolioHolding("SNTS", "Sonatel", 50, 19500.0, 975_000.0, 3.4),
                PortfolioHolding("ORAC", "Orange CI", 100, 7250.0, 725_000.0, 1.8, Color(0xFFFF7900)),
            ),
            nextDividendAmount = 31500.0, nextDividendFrom = "Sonatel", nextDividendWhen = "juin",
        )
    }
}

// ─── Edge: Order rejected (12) ─────────────────────────────────────
data class OrderRejectedViewModel(
    val total: Double,
    val available: Double,
    val fundLabel: String,
)

// ─── Edge: Market closed (13) ──────────────────────────────────────
data class MarketClosedViewModel(
    val side: String,        // "buy" | "sell"
    val symbol: String,
    val qty: Int,
    val total: Double,
    val nextSession: String,
    val expectedExecution: String,
)

// ─── Edge: Revoke (14) ─────────────────────────────────────────────
data class RevokeViewModel(
    val sgiName: String,
    val consentGivenAt: String,
    val ordersExecuted: Int,
)

// ─── Helpers ───────────────────────────────────────────────────────
fun Double.fcfa(): String {
    val nf = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
    return nf.format(this).replace(' ', ' ')  // remplace nbsp par espace standard
}
