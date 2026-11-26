package com.kylecorry.trail_sense.tools.earthquakes.domain

/**
 * Severity buckets used to color earthquakes in the list.
 */
enum class MagnitudeLevel(val minMagnitude: Float) {
    /** Below 3.0, shown in a neutral color */
    Minor(Float.NEGATIVE_INFINITY),

    /** 3.0 - 3.9, yellow */
    Light(3f),

    /** 4.0 - 4.9, orange */
    Moderate(4f),

    /** 5.0 and above, red */
    Strong(5f);

    companion object {
        fun from(magnitude: Float): MagnitudeLevel {
            return entries.last { magnitude >= it.minMagnitude }
        }
    }
}
