package com.kylecorry.trail_sense.tools.earthquakes.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Instant
import java.util.Locale

internal class EarthquakeDomainTest {

    @ParameterizedTest
    @CsvSource(
        "0.0, Minor",
        "2.9, Minor",
        "3.0, Light",
        "3.9, Light",
        "4.0, Moderate",
        "4.9, Moderate",
        "5.0, Strong",
        "7.8, Strong"
    )
    fun magnitudeLevels(magnitude: Float, expected: MagnitudeLevel) {
        assertEquals(expected, MagnitudeLevel.from(magnitude))
    }

    @Test
    fun sortsByTimeNewestFirst() {
        assertEquals(listOf("b", "c", "a"), ids(EarthquakeSort.Time.sort(sample)))
    }

    @Test
    fun sortsByMagnitudeStrongestFirst() {
        assertEquals(listOf("c", "a", "b"), ids(EarthquakeSort.Magnitude.sort(sample)))
    }

    @Test
    fun sortsByDepthShallowestFirst() {
        assertEquals(listOf("b", "a", "c"), ids(EarthquakeSort.Depth.sort(sample)))
    }

    @Test
    fun sortsByLocationAlphabetically() {
        val sorted = EarthquakeSort.Location.sort(sample, Locale.forLanguageTag("tr"))
        assertEquals(listOf("Çanakkale", "İzmir", "Malatya"), sorted.map { it.location })
    }

    private fun ids(list: List<Earthquake>) = list.map { it.id }

    private val sample = listOf(
        quake("a", "2026-11-24T10:00:00Z", 3.5f, 10f, "Malatya"),
        quake("b", "2026-11-26T10:00:00Z", 2.1f, 3f, "İzmir"),
        quake("c", "2026-11-25T10:00:00Z", 4.6f, 25f, "Çanakkale")
    )

    private fun quake(id: String, time: String, magnitude: Float, depth: Float, location: String) =
        Earthquake(id, Instant.parse(time), magnitude, depth, 0.0, 0.0, location)
}
