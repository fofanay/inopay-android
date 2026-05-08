// InopayInvestScreen — vue racine que le partenaire intègre dans son app Compose.
//
// Usage minimal :
//
//     import com.inopay.ui.InopayInvestScreen
//
//     setContent {
//         InopayInvestScreen(
//             userToken = jwt,
//             partnerKey = "banque-atlas",
//             onOrderPlaced = { order -> Analytics.track(order) },
//         )
//     }
//
// Theme override (optionnel — sinon fetch /v1/embed-theme/:partnerKey) :
//
//     InopayInvestScreen(
//         userToken = jwt,
//         partnerKey = "banque-atlas",
//         theme = InopayTheme.BanqueAtlas,
//         ...
//     )

package com.inopay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inopay.ui.api.InopayUIClient
import com.inopay.ui.components.InopayAppBar
import com.inopay.ui.coordinator.*
import com.inopay.ui.models.*
import com.inopay.ui.screens.*
import com.inopay.ui.theme.InopayTheme
import com.inopay.ui.theme.ProvideInopayTheme

data class InopayFlowResult(val reason: String, val lastScreen: InopayScreen)

@Composable
fun InopayInvestScreen(
    userToken: String,
    partnerKey: String,
    theme: InopayTheme? = null,
    initialScreen: InopayScreen = InopayScreen.Welcome,
    firstName: String = "Aïssata",
    onComplete: ((InopayFlowResult) -> Unit)? = null,
    onOrderPlaced: ((InopayPlacedOrder) -> Unit)? = null,
    onConsentRevoked: (() -> Unit)? = null,
) {
    val coordinator: FlowCoordinator = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FlowCoordinator(FlowState(screen = initialScreen, firstName = firstName)) as T
            }
        }
    )
    val state by coordinator.state.collectAsStateWithLifecycle()

    var resolvedTheme by remember { mutableStateOf(theme ?: InopayTheme.Default) }

    // Fetch theme once if not overridden
    LaunchedEffect(partnerKey) {
        if (theme == null) {
            val client = InopayUIClient(userToken = userToken)
            resolvedTheme = client.fetchTheme(partnerKey, fallback = InopayTheme.Default)
        }
    }

    ProvideInopayTheme(resolvedTheme) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            InopayAppBar(onClose = { onComplete?.invoke(InopayFlowResult("cancelled", state.screen)) })
            Body(state, coordinator, onOrderPlaced, onConsentRevoked, onComplete)
        }
    }
}

@Composable
private fun Body(
    state: FlowState,
    coordinator: FlowCoordinator,
    onOrderPlaced: ((InopayPlacedOrder) -> Unit)?,
    onConsentRevoked: (() -> Unit)?,
    onComplete: ((InopayFlowResult) -> Unit)?,
) {
    val theme = com.inopay.ui.theme.LocalInopayTheme.current
    when (state.screen) {
        InopayScreen.Welcome -> WelcomeScreen(onStart = coordinator::next)

        InopayScreen.RiskProfile -> RiskProfileScreen(onNext = coordinator::next)

        InopayScreen.KycAccelere -> KycAccelereScreen(
            viewModel = KycViewModel.demo(theme.partnerName, state.firstName),
            onConfirm = { _, _ -> coordinator.next() },
        )

        InopayScreen.KycIncomplete -> KycIncompleteScreen(
            onUpload = coordinator::next,
            onSkip = coordinator::goBack,
        )

        InopayScreen.Consent -> ConsentScreen(sgi = ConsentSgi.HudsonDemo) { id, name ->
            coordinator.grantConsent(id, name)
        }

        InopayScreen.Markets -> MarketsScreen(viewModel = MarketsViewModel.Demo) { instr ->
            coordinator.selectInstrument(instr.symbol, instr.name, instr.lastPrice)
        }

        InopayScreen.Instrument -> {
            val sel = state.selectedInstrument ?: InopaySelectedInstrument("SNTS", "Sonatel", 19500.0)
            val vm = if (sel.symbol == "SNTS") InstrumentDetailViewModel.SonatelDemo
                     else InstrumentDetailViewModel(
                         symbol = sel.symbol, name = sel.name, sector = "BRVM",
                         lastPrice = sel.price, changePct = 0.0, changeAbs = 0.0,
                         about = "", metricCapi = "—", metricPER = "—", metricDividend = "—", metricVolume = "—",
                     )
            InstrumentScreen(viewModel = vm, onBuy = coordinator::next)
        }

        InopayScreen.Order -> {
            val sel = state.selectedInstrument ?: InopaySelectedInstrument("SNTS", "Sonatel", 19500.0)
            OrderScreen(
                symbol = sel.symbol, name = sel.name, price = sel.price,
                sgiName = state.consentSgiName ?: "Hudson & Cie",
                sgiCommissionPct = 0.30,
                fundLabel = "Compte source", fundId = "●●●● 1234", fundBalance = "1 547 800 FCFA",
                onDraft = { qty, total -> coordinator.draftOrder(qty, total) },
            )
        }

        InopayScreen.Confirmation -> {
            val sel = state.selectedInstrument ?: InopaySelectedInstrument("SNTS", "Sonatel", 19500.0)
            val draft = state.draftOrder ?: InopayDraftOrder(50, 979_388.0)
            ConfirmationScreen(
                side = "buy", symbol = sel.symbol, name = sel.name,
                qty = draft.qty, price = sel.price, total = draft.total,
                sgiName = state.consentSgiName ?: "Hudson & Cie",
                fundId = "●●●● 1234",
                onSubmit = {
                    val order = InopayPlacedOrder(
                        id = "INV-" + java.lang.Long.toHexString(System.currentTimeMillis()).uppercase(),
                        total = draft.total,
                        signatureB64 = "demo-sig-base64-Ed25519",
                        hashSHA256 = "a47z9b3c8f2d-demo-hash-e91a",
                    )
                    coordinator.orderPlaced(order)
                    onOrderPlaced?.invoke(order)
                },
                onCancel = coordinator::goBack,
            )
        }

        InopayScreen.Receipt -> {
            val sel = state.selectedInstrument ?: InopaySelectedInstrument("SNTS", "Sonatel", 19500.0)
            val draft = state.draftOrder ?: InopayDraftOrder(50, 979_388.0)
            val last = state.lastOrder ?: InopayPlacedOrder("INV-DEMO", draft.total)
            ReceiptScreen(
                orderId = last.id, side = "buy", qty = draft.qty, symbol = sel.symbol, total = last.total,
                sgiName = state.consentSgiName ?: "Hudson & Cie",
                signatureB64 = last.signatureB64, hashSHA256 = last.hashSHA256,
                onPortfolio = coordinator::next,
            )
        }

        InopayScreen.Portfolio -> PortfolioScreen(viewModel = PortfolioViewModel.Demo)

        InopayScreen.OrderRejected -> {
            val draft = state.draftOrder ?: InopayDraftOrder(50, 979_388.0)
            OrderRejectedScreen(
                viewModel = OrderRejectedViewModel(total = draft.total, available = 1_547_800.0, fundLabel = "Compte chèque"),
                onRecharge = { onComplete?.invoke(InopayFlowResult("cancelled", state.screen)) },
                onReduce = coordinator::goBack,
            )
        }

        InopayScreen.MarketClosed -> {
            val sel = state.selectedInstrument ?: InopaySelectedInstrument("SNTS", "Sonatel", 19500.0)
            val draft = state.draftOrder ?: InopayDraftOrder(50, 979_388.0)
            MarketClosedScreen(
                viewModel = MarketClosedViewModel(
                    side = "buy", symbol = sel.symbol, qty = draft.qty, total = draft.total,
                    nextSession = "Lun. 11 mai · 09:00 GMT",
                    expectedExecution = "Lun. 11 mai · 10:30 GMT",
                ),
                onQueue = coordinator::next,
                onCancel = coordinator::goBack,
            )
        }

        InopayScreen.Revoke -> RevokeScreen(
            viewModel = RevokeViewModel(
                sgiName = state.consentSgiName ?: "Hudson & Cie",
                consentGivenAt = "12 mars 2026",
                ordersExecuted = 14,
            ),
            onConfirm = { _ ->
                coordinator.revokeConsent()
                onConsentRevoked?.invoke()
            },
            onCancel = coordinator::goBack,
        )
    }
}

// Helper import for collectAsStateWithLifecycle (Compose UI 1.6+)
@Composable
private fun <T> kotlinx.coroutines.flow.StateFlow<T>.collectAsStateWithLifecycle(): State<T> {
    val state = remember { mutableStateOf(this.value) }
    LaunchedEffect(this) { collect { state.value = it } }
    return state
}
