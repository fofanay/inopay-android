# inopay-android-ui — Module Android Compose pour @inopay/android v0.2

Ce module fournit les **14 écrans canoniques** du parcours investisseur Inopay (BRVM/BVMAC/GSE) en Jetpack Compose, themables par le partenaire intégrateur.

## Installation (JitPack)

`settings.gradle.kts` du partenaire :

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

`build.gradle.kts` (app module du partenaire) :

```kotlin
dependencies {
    implementation("com.github.inopay:inopay-android:0.2.0-alpha.1")
    // ⬆ inclut transitivement :ui via api(project(":")) — ou explicitement :
    implementation("com.github.inopay.inopay-android:ui:0.2.0-alpha.1")
}
```

## Usage minimal

```kotlin
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.inopay.ui.InopayInvestScreen

class InvestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userJwt = getUserJwt()  // votre JWT app
        setContent {
            InopayInvestScreen(
                userToken = userJwt,
                partnerKey = "banque-atlas",
                onOrderPlaced = { order ->
                    Analytics.track("inopay_order_placed", mapOf("id" to order.id))
                },
            )
        }
    }
}
```

C'est tout. La vue fetch automatiquement la palette depuis `/v1/embed-theme/:partnerKey`, monte le state machine (StateFlow + ViewModel), et orchestre les 14 écrans.

## Theme override

```kotlin
InopayInvestScreen(
    userToken = jwt,
    partnerKey = "banque-atlas",
    theme = InopayTheme(
        primary = Color(0xFF1B3A5C),
        primaryDark = Color(0xFF10243B),
        accent = Color(0xFFD4942F),
        partnerName = "Banque Atlas",
        partnerLogoUrl = "https://cdn.banque-atlas.com/logo.svg",
    ),
)
```

3 presets démo livrés : `InopayTheme.BanqueAtlas`, `InopayTheme.WaveCash`, `InopayTheme.DiasporaSend`.

## Deep-link (initialScreen)

Pour user revenant qui a déjà fait l'onboarding :

```kotlin
InopayInvestScreen(
    userToken = jwt,
    partnerKey = "banque-atlas",
    initialScreen = InopayScreen.Markets,
)
```

## Architecture

- **`InopayInvestScreen`** — vue racine (entry point partenaire). Orchestre app bar + écran courant + theme.
- **`FlowCoordinator: ViewModel`** — state machine `StateFlow`-based, survit aux config changes (rotations).
- **`InopayTheme` data class** — palette + `LocalInopayTheme` CompositionLocal pour propagation sans prop drilling.
- **`InopayUIClient`** — fetch wrapper OkHttp pour `/v1/embed-theme`. Fallback gracieux vers `InopayTheme.Default` si réseau ou 404.
- **14 écrans** dans `Sources/com/inopay/ui/screens/` : 1 fichier par écran (welcome, risk-profile, kyc-accelere, consent, markets, instrument, order, confirmation, receipt, portfolio + edge cases regroupés dans `EdgeCaseScreens.kt`).
- **Composants partagés** dans `components/SharedComponents.kt` : `InopayAppBar`, `InopayCta`, `InopayCard`, `InopayBigIcon`, etc.

## Dépendances tirées

- AndroidX Compose BOM `2024.02.02` (Material 3, ui-tooling-preview, etc.)
- Lifecycle ViewModel/Runtime Compose `2.7.0`
- Kotlin Coroutines Android `1.7.3`
- Kotlin Serialization JSON `1.6.3`
- OkHttp `4.12.0` (transitive depuis `:` root module)

## Compatibilité

- Android : `minSdk 24` (Android 7.0 Nougat — couvre 99 % des devices Afrique 2026)
- Kotlin : `1.9.22`
- Compose Compiler : `1.5.10` (aligné Kotlin 1.9.22)
- JVM : 17

## Compose Previews

Aucun PreviewProvider n'est ship par défaut (pour réduire la surface). Pour valider visuellement avant build :

```kotlin
@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun WelcomePreview() {
    ProvideInopayTheme(InopayTheme.BanqueAtlas) {
        Column { InopayAppBar(); WelcomeScreen(onStart = {}) }
    }
}
```

## Limitations connues v0.2

- **Localisation** : FR uniquement. EN/PT en v0.3.
- **Endpoints API server-side incomplets** : `/v1/kyc/me`, `/v1/sgi-directory` sont en backlog côté Inopay backend. Les VMs des écrans 03/04 utilisent des fallbacks démo en attendant.
- **Aucun test instrumenté** dans `androidTest/`. À ajouter en v0.2.x.

## Build & test

```sh
# Depuis le repo inopay-android
./gradlew :ui:assembleRelease   # build the UI library AAR
./gradlew :ui:test               # unit tests (none yet for v0.2)
./gradlew :ui:lint               # AGP lint
```

Ou ouvre `inopay-android/` dans Android Studio Hedgehog (2023.1.1+) — le sync Gradle reconnaît automatiquement le multi-module `:` + `:ui`. Lance les Compose previews depuis n'importe quel `@Composable` annoté `@Preview`.

## Licence

MIT — voir LICENSE en racine du package.
