package com.kylecorry.trail_sense.tools.earthquakes.domain

import java.time.Duration

enum class EarthquakeTimeRange(val duration: Duration) {
    Day(Duration.ofDays(1)),
    Week(Duration.ofDays(7)),
    Month(Duration.ofDays(30))
}
