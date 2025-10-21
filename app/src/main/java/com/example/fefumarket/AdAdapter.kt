package com.example.fefumarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdAdapter(private val adList: List<Ad>) : RecyclerView.Adapter<AdAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageAd: ImageView = view.findViewById(R.id.imageAd)
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val textPrice: TextView = view.findViewById(R.id.textPrice)
        val textSeller: TextView = view.findViewById(R.id.textSeller)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ad = adList[position]
        holder.textTitle.text = ad.title
        holder.textPrice.text = ad.price
        holder.textSeller.text = ad.seller
        // Можно добавить загрузку изображения, если есть: holder.imageAd.setImageResource(ad.imageRes)
    }

    override fun getItemCount(): Int = adList.size
}

// Модель данных для объявления
data class Ad(val title: String, val price: String, val seller: String)