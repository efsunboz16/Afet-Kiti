package com.kylecorry.trail_sense.tools.earthquakes

import android.content.Context
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.tools.tools.infrastructure.Tool
import com.kylecorry.trail_sense.tools.tools.infrastructure.ToolCategory
import com.kylecorry.trail_sense.tools.tools.infrastructure.ToolRegistration
import com.kylecorry.trail_sense.tools.tools.infrastructure.Tools

object RecentEarthquakesToolRegistration : ToolRegistration {
    override fun getTool(context: Context): Tool {
        return Tool(
            Tools.RECENT_EARTHQUAKES,
            context.getString(R.string.tool_recent_earthquakes_title),
            R.drawable.ic_tool_earthquake,
            R.id.recentEarthquakesFragment,
            ToolCategory.Other,
            context.getString(R.string.tool_recent_earthquakes_summary)
        )
    }
}
