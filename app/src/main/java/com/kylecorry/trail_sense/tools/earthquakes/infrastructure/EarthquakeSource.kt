package com.kylecorry.trail_sense.tools.earthquakes.infrastructure

import com.kylecorry.trail_sense.tools.earthquakes.domain.Earthquake
import java.time.Instant

interface EarthquakeSource {
    /**
     * Get the earthquakes between [start] and [end] with a magnitude of at least [minMagnitude].
     * @throws java.io.IOException if the data could not be downloaded
     */
    suspend fun getEarthquakes(start: Instant, end: Instant, minMagnitude: Float): List<Earthquake>
}
