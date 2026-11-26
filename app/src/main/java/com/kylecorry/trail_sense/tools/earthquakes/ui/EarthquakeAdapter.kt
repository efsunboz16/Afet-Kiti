package com.kylecorry.trail_sense.tools.earthquakes.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.ItemEarthquakeBinding
import com.kylecorry.trail_sense.shared.FormatService
import com.kylecorry.trail_sense.tools.earthquakes.domain.Earthquake
import com.kylecorry.trail_sense.tools.earthquakes.domain.MagnitudeLevel
import java.time.ZoneId
import java.util.Locale

class EarthquakeAdapter(
    private val onClick: (Earthquake) -> Unit
) : ListAdapter<Earthquake, EarthquakeAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEarthquakeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemEarthquakeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val formatter = FormatService.getInstance(binding.root.context)

        fun bind(earthquake: Earthquake) {
            val context = binding.root.context
            val level = earthquake.level

            binding.earthquakeMagnitude.text = formatOneDecimal(earthquake.magnitude)
            binding.earthquakeMagnitude.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, getColor(level)))
            binding.earthquakeMagnitude.setTextColor(
                if (level == MagnitudeLevel.Strong) Color.WHITE else Color.BLACK
            )

            binding.earthquakeLocation.text = earthquake.location
            binding.earthquakeTime.text = formatter.formatDateTime(
                earthquake.time.atZone(ZoneId.systemDefault()),
                relative = true
            )
            binding.earthquakeDepth.text = context.getString(
                R.string.earthquakes_depth,
                formatOneDecimal(earthquake.depthKm)
            )

            binding.root.setOnClickListener { onClick(earthquake) }
        }
    }

    private fun formatOneDecimal(value: Float): String {
        return String.format(Locale.getDefault(), "%.1f", value)
    }

    object DiffCallback : DiffUtil.ItemCallback<Earthquake>() {
        override fun areItemsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        @ColorRes
        fun getColor(level: MagnitudeLevel): Int {
            return when (level) {
                MagnitudeLevel.Minor -> R.color.earthquake_minor
                MagnitudeLevel.Light -> R.color.earthquake_light
                MagnitudeLevel.Moderate -> R.color.earthquake_moderate
                MagnitudeLevel.Strong -> R.color.earthquake_strong
            }
        }
    }
}
