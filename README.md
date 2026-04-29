# inopay-android

Kotlin SDK — client for the Inopay African capital markets infrastructure (BRVM, BVMAC, GSE) via the public sandbox.

## Status

`v0.1.0-alpha.1` — alpha public, wraps `https://api.getinopay.com/v1/sandbox/*`. Maven Central publication coming next.

## Install

### Gradle (KTS)

Once published to Maven Central:

```kotlin
dependencies {
    implementation("com.inopay:inopay-android:0.1.0-alpha.1")
}
```

### Manual (during alpha)

Download the zip from <https://getinopay.com/sdk/android/> and drop the `src/main/kotlin/com/inopay/` directory into your project, or attach it as a local module.

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
- JVM toolchain 17+
- OkHttp 4.12+, kotlinx-serialization-json 1.6+, kotlinx-coroutines-core 1.7+

## License

MIT — © Inopay
