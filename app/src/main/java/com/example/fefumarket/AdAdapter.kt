package com.example.fefumarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Класс адаптера для RecyclerView, который отображает список объявлений (ads)
class AdAdapter(private var ads: List<Ad>) : RecyclerView.Adapter<AdAdapter.AdViewHolder>() {

    // Внутренний класс ViewHolder для кэширования ссылок на View в элементе списка
    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Ссылки на TextView из разметки item_ad.xml для быстрого доступа
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvSeller: TextView = itemView.findViewById(R.id.tvSeller)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    // Метод создания нового ViewHolder: инфлейтит разметку item_ad.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
        return AdViewHolder(view)
    }

    // Метод привязки данных к ViewHolder: заполняет View данными из ads[position]
    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = ads[position]
        holder.tvTitle.text = ad.title
        holder.tvPrice.text = ad.price
        holder.tvSeller.text = ad.seller
        holder.tvDescription.text = ad.description

        // Обработчик клика по элементу: запускает AdDetailActivity с данными объявления
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, AdDetailActivity::class.java)
            intent.putExtra("title", ad.title)
            intent.putExtra("price", ad.price)
            intent.putExtra("seller", ad.seller)
            intent.putExtra("description", ad.description)
            context.startActivity(intent)
        }
    }

    // Метод возвращает количество элементов в списке
    override fun getItemCount() = ads.size

    // Публичный метод для обновления данных: заменяет список и уведомляет адаптер
    fun updateAds(newAds: List<Ad>) {
        ads = newAds
        notifyDataSetChanged()
    }
}