package com.kylecorry.trail_sense.tools.earthquakes.infrastructure

import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.trail_sense.shared.ProguardIgnore
import com.kylecorry.trail_sense.tools.earthquakes.domain.Earthquake
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

/**
 * Converts the JSON returned by the AFAD event API into [Earthquake]s.
 * AFAD sends numbers as strings and dates in UTC without a zone, so both are converted here.
 * Records that are missing required fields are skipped.
 */
object AfadEarthquakeParser {

    fun parse(json: String): List<Earthquake> {
        val events = JsonConvert.fromJson<Array<AfadEvent>>(json) ?: return emptyList()
        return events.mapNotNull { it.toEarthquake() }
    }

    private fun AfadEvent.toEarthquake(): Earthquake? {
        val id = eventID ?: return null
        val time = parseTime(date) ?: return null
        val mag = magnitude?.toFloatOrNull() ?: return null
        val lat = latitude?.toDoubleOrNull()
        val lon = longitude?.toDoubleOrNull()
        if (lat == null || lon == null) {
            return null
        }
        return Earthquake(
            id = id,
            time = time,
            magnitude = mag,
            depthKm = depth?.toFloatOrNull() ?: 0f,
            latitude = lat,
            longitude = lon,
            location = location?.takeIf { it.isNotBlank() } ?: province ?: ""
        )
    }

    private fun parseTime(date: String?): Instant? {
        date ?: return null
        return try {
            LocalDateTime.parse(date).toInstant(ZoneOffset.UTC)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    // Field names match the AFAD response, do not rename
    private class AfadEvent(
        val eventID: String?,
        val location: String?,
        val province: String?,
        val latitude: String?,
        val longitude: String?,
        val depth: String?,
        val magnitude: String?,
        val date: String?
    ) : ProguardIgnore
}
