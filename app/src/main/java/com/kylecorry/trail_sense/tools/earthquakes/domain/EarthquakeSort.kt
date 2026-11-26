package com.kylecorry.trail_sense.tools.earthquakes.domain

import java.text.Collator
import java.util.Locale

enum class EarthquakeSort {
    /** Newest first */
    Time,

    /** Strongest first */
    Magnitude,

    /** Shallowest first */
    Depth,

    /** Alphabetical by place name */
    Location;

    fun sort(earthquakes: List<Earthquake>, locale: Locale = Locale.getDefault()): List<Earthquake> {
        return when (this) {
            Time -> earthquakes.sortedByDescending { it.time }
            Magnitude -> earthquakes.sortedWith(
                compareByDescending<Earthquake> { it.magnitude }.thenByDescending { it.time }
            )

            Depth -> earthquakes.sortedWith(
                compareBy<Earthquake> { it.depthKm }.thenByDescending { it.time }
            )

            Location -> {
                val collator = Collator.getInstance(locale)
                earthquakes.sortedWith(
                    compareBy<Earthquake, String>(collator) { it.location }
                        .thenByDescending { it.time }
                )
            }
        }
    }
}
