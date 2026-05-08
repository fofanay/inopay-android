// InopayUIClient — fetch wrapper utilisé par les écrans pour charger leurs view models.
// Léger, retourne des fallbacks démo si l'endpoint /v1/embed-theme renvoie 404.

package com.inopay.ui.api

import com.inopay.ui.theme.InopayTheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Serializable
data class InopayThemeResponse(
    val partner: Partner,
    val theme: ThemePayload,
    val locales_supported: List<String> = listOf("fr"),
    val sdk_min_version: String = "0.2.0",
) {
    @Serializable data class Partner(val name: String, val logo_url: String? = null)
    @Serializable data class ThemePayload(
        val primary: String,
        val primary_dark: String,
        val accent: String,
        val on_primary: String = "#FFFFFF",
        val font: String = "Inter, system-ui, sans-serif",
    )
}

class InopayUIClient(
    private val baseUrl: String = "https://api.getinopay.com",
    private val userToken: String? = null,
    private val httpClient: OkHttpClient = defaultClient(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchTheme(partnerKey: String, fallback: InopayTheme = InopayTheme.Default): InopayTheme = withContext(Dispatchers.IO) {
        val url = "$baseUrl/v1/embed-theme/$partnerKey"
        val req = Request.Builder().url(url).get().build()
        try {
            httpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext fallback
                val body = resp.body?.string() ?: return@withContext fallback
                val r = json.decodeFromString(InopayThemeResponse.serializer(), body)
                fallback.copy(
                    primary = parseHex(r.theme.primary) ?: fallback.primary,
                    primaryDark = parseHex(r.theme.primary_dark) ?: fallback.primaryDark,
                    accent = parseHex(r.theme.accent) ?: fallback.accent,
                    onPrimary = parseHex(r.theme.on_primary) ?: fallback.onPrimary,
                    partnerName = r.partner.name,
                    partnerLogoUrl = r.partner.logo_url,
                )
            }
        } catch (e: Exception) {
            fallback
        }
    }

    companion object {
        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        private fun parseHex(s: String): Color? {
            val clean = s.removePrefix("#")
            return try {
                val v = clean.toLong(16)
                Color(0xFF000000 or v)
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
}
