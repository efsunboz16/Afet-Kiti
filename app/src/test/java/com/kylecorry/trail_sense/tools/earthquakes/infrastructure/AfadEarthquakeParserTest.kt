package com.kylecorry.trail_sense.tools.earthquakes.infrastructure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

internal class AfadEarthquakeParserTest {

    @Test
    fun parsesAfadResponse() {
        val json = """
            [
              {"rms":"0.44","eventID":"735337","location":"Milas (Muğla)",
               "latitude":"37.13217","longitude":"27.864","depth":"6.98","type":"ML",
               "magnitude":"2.5","country":"Türkiye","province":"Muğla","district":"Milas",
               "neighborhood":"Bağdamları","date":"2026-11-26T05:10:42",
               "isEventUpdate":false,"lastUpdateDate":null}
            ]
        """.trimIndent()

        val earthquakes = AfadEarthquakeParser.parse(json)

        assertEquals(1, earthquakes.size)
        val earthquake = earthquakes.first()
        assertEquals("735337", earthquake.id)
        assertEquals("Milas (Muğla)", earthquake.location)
        assertEquals(2.5f, earthquake.magnitude)
        assertEquals(6.98f, earthquake.depthKm)
        assertEquals(37.13217, earthquake.latitude)
        assertEquals(27.864, earthquake.longitude)
        // AFAD dates are in UTC
        assertEquals(Instant.parse("2026-11-26T05:10:42Z"), earthquake.time)
    }

    @Test
    fun skipsRecordsWithMissingRequiredFields() {
        val json = """
            [
              {"eventID":"1","location":"A","latitude":"37","longitude":"27","depth":"5","magnitude":null,"date":"2026-11-26T05:10:42"},
              {"eventID":"2","location":"B","latitude":"37","longitude":"27","depth":"5","magnitude":"abc","date":"2026-11-26T05:10:42"},
              {"eventID":"3","location":"C","latitude":"37","longitude":"27","depth":"5","magnitude":"3.1","date":"not a date"},
              {"location":"D","latitude":"37","longitude":"27","depth":"5","magnitude":"3.1","date":"2026-11-26T05:10:42"},
              {"eventID":"5","location":"E","latitude":"37","longitude":"27","depth":"5","magnitude":"3.1","date":"2026-11-26T05:10:42"}
            ]
        """.trimIndent()

        val earthquakes = AfadEarthquakeParser.parse(json)

        assertEquals(listOf("5"), earthquakes.map { it.id })
    }

    @Test
    fun fallsBackToProvinceAndZeroDepth() {
        val json = """
            [{"eventID":"1","location":"","province":"Malatya","latitude":"38.3","longitude":"38.3",
              "depth":null,"magnitude":"4.0","date":"2026-11-26T05:10:42"}]
        """.trimIndent()

        val earthquake = AfadEarthquakeParser.parse(json).first()

        assertEquals("Malatya", earthquake.location)
        assertEquals(0f, earthquake.depthKm)
    }

    @Test
    fun emptyArrayGivesEmptyList() {
        assertTrue(AfadEarthquakeParser.parse("[]").isEmpty())
    }

    @Test
    fun buildsUrlInUtc() {
        val url = AfadEarthquakeSource.buildUrl(
            Instant.parse("2026-11-25T06:00:00Z"),
            Instant.parse("2026-11-26T06:00:00Z"),
            3f
        ).toString()

        assertEquals(
            "https://servisnet.afad.gov.tr/apigateway/deprem/apiv2/event/filter" +
                    "?start=2026-11-25T06:00:00&end=2026-11-26T06:00:00&minmag=3.0" +
                    "&orderby=timedesc&limit=2000&format=json",
            url
        )
    }
}
