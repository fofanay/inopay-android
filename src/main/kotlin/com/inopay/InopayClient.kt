// Inopay Android SDK — Kotlin client for the African capital markets infrastructure.
// Aligns with the public sandbox at https://api.getinopay.com/v1/sandbox

package com.inopay

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Serializable enum class Market { BRVM, BVMAC, GSE }
@Serializable enum class Currency { XOF, XAF, GHS }
@Serializable enum class OrderSide { buy, sell }
@Serializable enum class OrderStatus { pending, filled, rejected, cancelled }
@Serializable enum class KycLevel { KYC1, KYC2, KYC3 }

@Serializable
data class Instrument(
    val symbol: String,
    val name: String,
    val market: Market,
    val currency: Currency,
    val last_price: Double,
    val change_pct: Double,
)

@Serializable
data class InstrumentList(
    val sandbox: Boolean? = null,
    val as_of: String,
    val instruments: List<Instrument>,
)

@Serializable
data class SGI(
    val id: String,
    val name: String,
    val market: Market,
    val fill_rate: Double,
)

@Serializable
data class SGIList(val sandbox: Boolean, val sgis: List<SGI>)

@Serializable
data class CreateOrderInput(
    val symbol: String,
    val side: OrderSide,
    val qty: Int,
    val sgi_id: String? = null,
)

@Serializable
data class Order(
    val id: String,
    val symbol: String,
    val side: OrderSide,
    val qty: Int,
    val sgi_id: String,
    val status: OrderStatus,
    val avg_price: Double,
    val filled_qty: Int,
    val filled_at: String? = null,
    val settlement_date: String? = null,
    val settlement_currency: Currency? = null,
)

@Serializable
data class OrderResponse(val sandbox: Boolean, val order: Order, val note: String? = null)

@Serializable
data class KycAttestation(
    val schema: String,
    val user_id: String,
    val issuer: String,
    val level: KycLevel,
    val issued_at: String,
    val expires_at: String,
    val key_id: String,
    val ed25519_signature: String,
)

@Serializable
data class KycResponse(val sandbox: Boolean, val attestation: KycAttestation, val note: String? = null)

@Serializable
data class SandboxResetResult(
    val sandbox: Boolean,
    val reset_at: String,
    val wallet_credit_cents: Long,
    val message: String,
)

@Serializable
data class HealthResult(
    val sandbox: Boolean,
    val status: String,
    val demo_key: String,
    val rate_limit: String,
)

class InopayException(
    val statusCode: Int,
    val errorCode: String,
    val detail: String?,
) : RuntimeException("InopayException($statusCode $errorCode): ${detail ?: ""}")

@Serializable
private data class ErrorPayload(val error: String? = null, val detail: String? = null)

/**
 * Inopay client — coroutines-based.
 *
 * @param apiKey API key. Use `sk_test_demo_inopay_2026` for the public sandbox (60 req/min/IP).
 * @param baseUrl base URL — defaults to the public sandbox.
 * @param httpClient optional shared OkHttpClient.
 */
class InopayClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.getinopay.com/v1/sandbox",
    private val httpClient: OkHttpClient = OkHttpClient(),
) {
    init {
        require(apiKey.isNotEmpty()) { "InopayClient: apiKey is required" }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    private val jsonMedia = "application/json".toMediaType()

    private suspend inline fun <reified T> request(
        path: String,
        method: String = "GET",
        body: String? = null,
    ): T = withContext(Dispatchers.IO) {
        val builder = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/${path.trimStart('/')}")
            .header("Authorization", "Bearer $apiKey")
            .header("Accept", "application/json")

        when (method) {
            "GET" -> builder.get()
            "POST" -> builder.post((body ?: "").toRequestBody(jsonMedia))
            "DELETE" -> builder.delete(body?.toRequestBody(jsonMedia))
            else -> builder.method(method, body?.toRequestBody(jsonMedia))
        }

        if (body != null) builder.header("Content-Type", "application/json")

        val response = await(builder.build())
        val text = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            val payload = runCatching { json.decodeFromString(ErrorPayload.serializer(), text) }.getOrNull()
            throw InopayException(
                statusCode = response.code,
                errorCode = payload?.error ?: "http_${response.code}",
                detail = payload?.detail ?: response.message,
            )
        }
        json.decodeFromString(text)
    }

    private suspend fun await(request: Request): Response = suspendCancellableCoroutine { cont ->
        val call = httpClient.newCall(request)
        cont.invokeOnCancellation { call.cancel() }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
            override fun onResponse(call: Call, response: Response) {
                cont.resume(response)
            }
        })
    }

    // ── Endpoints ─────────────────────────────────────────────

    suspend fun health(): HealthResult = request("health")
    suspend fun listInstruments(): InstrumentList = request("instruments")
    suspend fun listSGIs(): SGIList = request("sgis")
    suspend fun createOrder(input: CreateOrderInput): OrderResponse =
        request("orders", method = "POST", body = json.encodeToString(CreateOrderInput.serializer(), input))
    suspend fun getOrder(id: String): OrderResponse = request("orders/$id")
    suspend fun fetchKyc(userId: String): KycResponse = request("kyc/$userId")
    suspend fun resetSandbox(): SandboxResetResult = request("sandbox/reset", method = "POST")
}
