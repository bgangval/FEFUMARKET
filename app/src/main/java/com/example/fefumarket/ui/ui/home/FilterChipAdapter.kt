package com.example.fefumarket.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R

class FilterChipAdapter(
    private val filters: MutableList<String>,
    private val onRemove: (String) -> Unit,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<FilterChipAdapter.FilterViewHolder>() {

    class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.filterText)
        val remove: ImageView = view.findViewById(R.id.removeFilter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter_chip, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filters[position]

        holder.text.text = filter

        holder.remove.setOnClickListener {
            val removed = filters[position]
            filters.removeAt(position)
            notifyItemRemoved(position)
            onRemove(removed)
            onListChanged()  // уведомляем HomeActivity, что список изменился
        }
    }

    override fun getItemCount(): Int = filters.size

    fun updateFilters(newFilters: List<String>) {
        filters.clear()
        filters.addAll(newFilters)
        notifyDataSetChanged()
        onListChanged() // уведомляем HomeActivity после обновления
    }
}