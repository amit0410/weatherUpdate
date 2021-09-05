package com.example.weatherupdate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherupdate.R
import com.example.weatherupdate.models.CityModel
import com.example.weatherupdate.roundOffDecimal
import kotlinx.android.synthetic.main.city_item_view.view.*

class WeatherAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CityModel>() {

        override fun areItemsTheSame(oldItem: CityModel, newItem: CityModel): Boolean {
            return oldItem.city == newItem.city
        }

        override fun areContentsTheSame(oldItem: CityModel, newItem: CityModel): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this,DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WeatherViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.city_item_view,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WeatherViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<CityModel>) {
        differ.submitList(list)
    }

    class WeatherViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: CityModel) = with(itemView) {
            val color = when(item.aqi.toInt()){
                in 0..50 -> R.color.Good
                in 51..100 -> R.color.satisfactory
                in 101..200 -> R.color.moderate
                in 201..300 -> R.color.poor
                in 301..400 -> R.color.verypoor
                in 401..500 -> R.color.severe
                else -> R.color.severe
            }
            card_view.setBackgroundResource(color)
            itemView.setOnClickListener {
                interaction?.onItemClick(adapterPosition, item)
            }
            val roundAqi = item.aqi.roundOffDecimal()
            city.setText(item.city)
            aqi.setText(roundAqi.toString())

        }
    }

    interface Interaction {
        fun onItemClick(position: Int, item: CityModel)
    }

}