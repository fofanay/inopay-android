# inopay-android

[![JitPack](https://img.shields.io/jitpack/v/github/fofanay/inopay-android.svg)](https://jitpack.io/#fofanay/inopay-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Kotlin SDK — client for the Inopay African capital markets infrastructure (BRVM, BVMAC, GSE) via the public sandbox.

## Status

`v0.1.0-alpha.2` — public alpha. Wraps `https://api.getinopay.com/v1/sandbox/*`. Maven Central publication coming next.

## Install

### Gradle (KTS)

In your top-level `settings.gradle.kts` or `build.gradle.kts`, add JitPack as a repository:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

Then in your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.fofanay:inopay-android:v0.1.0-alpha.2")
}
```

### Gradle (Groovy)

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.fofanay:inopay-android:v0.1.0-alpha.2'
}
```

## Quick start

```kotlin
import com.inopay.InopayClient
import com.inopay.CreateOrderInput
import com.inopay.OrderSide
import kotlinx.coroutines.runBlocking

val inopay = InopayClient(apiKey = "sk_test_demo_inopay_2026") // public demo key

runBlocking {
    // List instruments
    val list = inopay.listInstruments()
    list.instruments.take(3).forEach {
        println("${it.symbol}: ${it.last_price} ${it.currency}")
    }

    // Place a simulated order
    val order = inopay.createOrder(CreateOrderInput(
        symbol = "SNTS.BRVM",
        side = OrderSide.buy,
        qty = 10,
    ))
    println("Order ${order.order.id} status: ${order.order.status}")

    // Fetch a mock KYC attestation
    val kyc = inopay.fetchKyc("usr_demo_42")
    println("Attestation issued at ${kyc.attestation.issued_at}")
}
```

## API surface

| Method | Description |
|---|---|
| `health()` | Sandbox status |
| `listInstruments()` | List BRVM / BVMAC / GSE instruments |
| `listSGIs()` | List partner SGIs |
| `createOrder(input)` | Place a simulated order |
| `getOrder(id)` | Read back an order |
| `fetchKyc(userId)` | Mock Ed25519-signed KYC attestation |
| `resetSandbox()` | Reset the demo wallet |

## Rate limit

Public demo key `sk_test_demo_inopay_2026` is rate-limited to **60 requests per minute per IP**.
For private quotas request a sandbox key at <https://getinopay.com/fr/developers/sandbox>.

## Requirements

- Kotlin 1.9+
- JVM 17+
- OkHttp 4.12+, kotlinx-serialization-json 1.6+, kotlinx-coroutines-core 1.7+

## Why Inopay

Inopay is the [investment infrastructure for African capital markets](https://getinopay.com/fr/why-inopay) — BRVM (WAEMU), BVMAC (CEMAC), GSE (Ghana). Mobile Money operators, banks and licensed SGIs embed the regional exchanges into their apps via this SDK.

- Use case **Mobile Money operators** → see [Pour opérateurs MoMo](https://getinopay.com/fr/momo)
- Use case **Banks** → see [Pour banques](https://getinopay.com/fr/banks)
- Use case **SGI** → see [Pour SGI](https://getinopay.com/fr/sgi)
- White-label deployment → see [White-label](https://getinopay.com/fr/white-label)

## Regulatory framework

Inopay is a technical intermediation provider. Orders are executed exclusively by [AMF-UMOA-licensed SGIs](https://getinopay.com/fr/legal/regulatory-references). The KYC framework aligns with BCEAO Instruction No. 003-03-2025.

- [Compliance & doctrine (AMF-UMOA, COSUMAF, SEC Ghana)](https://getinopay.com/fr/compliance)
- [Public regulatory references](https://getinopay.com/fr/legal/regulatory-references)
- [Trust center & data residency](https://getinopay.com/fr/trust)
- [Contractual SLA](https://getinopay.com/fr/sla)
- [Public audit chain](https://getinopay.com/fr/audit)

## Other Inopay SDKs

The Inopay SDK family — same API surface, five native platforms:

- [`@inopay/web`](https://github.com/fofanay/inopay-web) — TypeScript / Web
- [`InopaySDK`](https://github.com/fofanay/inopay-ios) — Swift / iOS / macOS
- [`inopay-android`](https://github.com/fofanay/inopay-android) — Kotlin / Android / JVM
- [`inopay`](https://github.com/fofanay/inopay-python) — Python (sync, requests-based)
- [`inopay-java`](https://github.com/fofanay/inopay-java) — Java (sync, java.net.http + Jackson)

## Documentation & support

- [Developer portal](https://getinopay.com/fr/developers) — API, webhooks, sandbox
- [API reference (OpenAPI 3.1)](https://api.getinopay.com/v1/openapi.json)
- [Sandbox console](https://getinopay.com/fr/developers/sandbox) — public demo key + 7 endpoints
- [Portable KYC spec](https://getinopay.com/fr/developers/kyc) — Ed25519, offline-verifiable
- [Webhooks reference](https://getinopay.com/fr/developers/webhooks)
- [Changelog](https://getinopay.com/fr/developers/changelog)
- [Press kit](https://getinopay.com/fr/press-kit) — logo, boilerplates, fact sheet

Need integration help? Email <partner@getinopay.com>.

## License

MIT — © Inopay
