package com.kylecorry.trail_sense.tools.earthquakes.infrastructure

import com.kylecorry.trail_sense.tools.earthquakes.domain.Earthquake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Downloads earthquakes from the free AFAD (Disaster and Emergency Management Authority of Türkiye) API.
 * No API key is required.
 */
class AfadEarthquakeSource : EarthquakeSource {

    override suspend fun getEarthquakes(
        start: Instant,
        end: Instant,
        minMagnitude: Float
    ): List<Earthquake> = withContext(Dispatchers.IO) {
        val json = download(buildUrl(start, end, minMagnitude))
        AfadEarthquakeParser.parse(json)
    }

    private fun download(url: URL): String {
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = TIMEOUT_MILLIS
            connection.readTimeout = TIMEOUT_MILLIS
            connection.setRequestProperty("Accept", "application/json")
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                throw IOException("AFAD returned HTTP $code")
            }
            return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        // deprem.afad.gov.tr redirects here, so the final address is used directly
        private const val BASE_URL =
            "https://servisnet.afad.gov.tr/apigateway/deprem/apiv2/event/filter"
        private const val TIMEOUT_MILLIS = 10_000
        private const val MAX_RESULTS = 2000

        private val dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT).withZone(ZoneOffset.UTC)

        internal fun buildUrl(start: Instant, end: Instant, minMagnitude: Float): URL {
            val query = listOf(
                "start" to dateFormatter.format(start),
                "end" to dateFormatter.format(end),
                "minmag" to String.format(Locale.ROOT, "%.1f", minMagnitude),
                "orderby" to "timedesc",
                "limit" to MAX_RESULTS.toString(),
                "format" to "json"
            ).joinToString("&") { "${it.first}=${it.second}" }
            return URL("$BASE_URL?$query")
        }
    }
}
