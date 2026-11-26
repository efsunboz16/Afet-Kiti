package com.kylecorry.trail_sense.tools.earthquakes.domain

import java.time.Instant

/**
 * A single earthquake event.
 *
 * @param depthKm depth below the surface in kilometers
 * @param location human readable place name, e.g. "Milas (Muğla)"
 */
data class Earthquake(
    val id: String,
    val time: Instant,
    val magnitude: Float,
    val depthKm: Float,
    val latitude: Double,
    val longitude: Double,
    val location: String
) {
    val level: MagnitudeLevel
        get() = MagnitudeLevel.from(magnitude)
}
