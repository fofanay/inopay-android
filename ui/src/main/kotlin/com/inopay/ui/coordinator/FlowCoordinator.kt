// FlowCoordinator — state machine Compose-friendly (StateFlow + ViewModel).
// Le partenaire ne touche pas directement au coordinator dans le cas nominal —
// InopayInvestScreen le crée via viewModel(). Mais c'est exposé pour les cas
// avancés (deep-link impératif depuis une notification, par exemple).

package com.inopay.ui.coordinator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class InopayScreen(val id: String) {
    Welcome("welcome"),
    RiskProfile("risk-profile"),
    KycAccelere("kyc-accelere"),
    KycIncomplete("kyc-incomplete"),
    Consent("consent"),
    Markets("markets"),
    Instrument("instrument"),
    Order("order"),
    Confirmation("confirmation"),
    Receipt("receipt"),
    Portfolio("portfolio"),
    OrderRejected("order-rejected"),
    MarketClosed("market-closed"),
    Revoke("revoke");

    companion object {
        fun fromId(id: String): InopayScreen = values().firstOrNull { it.id == id } ?: Welcome
    }
}

data class InopaySelectedInstrument(
    val symbol: String,
    val name: String,
    val price: Double,
)

data class InopayDraftOrder(
    val qty: Int,
    val total: Double,
    val orderType: String = "market",
)

data class InopayPlacedOrder(
    val id: String,
    val total: Double,
    val signatureB64: String? = null,
    val hashSHA256: String? = null,
)

data class FlowState(
    val screen: InopayScreen = InopayScreen.Welcome,
    val firstName: String = "Aïssata",
    val selectedInstrument: InopaySelectedInstrument? = null,
    val draftOrder: InopayDraftOrder? = null,
    val lastOrder: InopayPlacedOrder? = null,
    val consentSgiId: String? = null,
    val consentSgiName: String? = null,
)

class FlowCoordinator(initial: FlowState = FlowState()) : ViewModel() {

    private val _state = MutableStateFlow(initial)
    val state: StateFlow<FlowState> = _state.asStateFlow()

    private val history = mutableListOf<InopayScreen>()

    // ─── Public navigation API ─────────────────────────────────────

    fun openTo(target: InopayScreen) {
        _state.update { ctx ->
            var next = ctx
            // Auto-populate context defaults so deep-links don't show empty states
            if (target in setOf(InopayScreen.Instrument, InopayScreen.Order, InopayScreen.Confirmation, InopayScreen.Receipt, InopayScreen.MarketClosed)
                && next.selectedInstrument == null) {
                next = next.copy(selectedInstrument = InopaySelectedInstrument("SNTS", "Sonatel", 19500.0))
            }
            if (target in setOf(InopayScreen.Confirmation, InopayScreen.Receipt, InopayScreen.OrderRejected, InopayScreen.MarketClosed)
                && next.draftOrder == null) {
                next = next.copy(draftOrder = InopayDraftOrder(qty = 50, total = 979_388.0))
            }
            if (target == InopayScreen.Receipt && next.lastOrder == null) {
                next = next.copy(lastOrder = InopayPlacedOrder(id = "INV-DEMO-A47Z9", total = 979_388.0, signatureB64 = "demo-sig", hashSHA256 = "a47z9b3c8f2d-demo-e91a"))
            }
            if (target in setOf(InopayScreen.Consent, InopayScreen.Order, InopayScreen.Confirmation, InopayScreen.Receipt, InopayScreen.Revoke)
                && next.consentSgiName == null) {
                next = next.copy(consentSgiName = "Hudson & Cie", consentSgiId = "hudson")
            }
            history.add(next.screen)
            next.copy(screen = target)
        }
    }

    fun goBack() {
        val prev = history.removeLastOrNull() ?: return
        _state.update { it.copy(screen = prev) }
    }

    fun setFirstName(name: String) {
        _state.update { it.copy(firstName = name) }
    }

    // ─── Flow events ────────────────────────────────────────────────

    fun next() {
        val cur = _state.value.screen
        when (cur) {
            InopayScreen.Welcome -> openTo(InopayScreen.RiskProfile)
            InopayScreen.RiskProfile -> openTo(InopayScreen.KycAccelere)
            InopayScreen.KycAccelere -> openTo(InopayScreen.Consent)
            InopayScreen.KycIncomplete -> openTo(InopayScreen.Consent)
            InopayScreen.Receipt -> openTo(InopayScreen.Portfolio)
            InopayScreen.OrderRejected -> openTo(InopayScreen.Order)
            InopayScreen.MarketClosed -> openTo(InopayScreen.Order)
            InopayScreen.Revoke -> openTo(InopayScreen.Welcome)
            InopayScreen.Instrument -> openTo(InopayScreen.Order)
            // Markets, Order, Confirmation, Portfolio, Consent : require explicit action
            else -> Unit
        }
    }

    fun selectInstrument(symbol: String, name: String, price: Double) {
        _state.update { it.copy(selectedInstrument = InopaySelectedInstrument(symbol, name, price)) }
        openTo(InopayScreen.Instrument)
    }

    fun draftOrder(qty: Int, total: Double, orderType: String = "market") {
        _state.update { it.copy(draftOrder = InopayDraftOrder(qty, total, orderType)) }
        openTo(InopayScreen.Confirmation)
    }

    fun orderPlaced(order: InopayPlacedOrder) {
        _state.update { it.copy(lastOrder = order) }
        openTo(InopayScreen.Receipt)
    }

    fun grantConsent(sgiId: String, sgiName: String) {
        _state.update { it.copy(consentSgiId = sgiId, consentSgiName = sgiName) }
        openTo(InopayScreen.Markets)
    }

    fun revokeConsent() {
        _state.update { it.copy(consentSgiId = null, consentSgiName = null) }
        openTo(InopayScreen.Welcome)
    }
}
