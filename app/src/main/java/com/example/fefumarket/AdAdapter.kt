package com.example.fefumarket

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdAdapter(private var ads: List<Ad>) : RecyclerView.Adapter<AdAdapter.AdViewHolder>() {

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.adImage)
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val seller: TextView = itemView.findViewById(R.id.tvSeller)
        val dorm: TextView = itemView.findViewById(R.id.tvDorm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
        return AdViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = ads[position]
        holder.image.setImageResource(ad.imageResId)
        holder.title.text = ad.title
        holder.price.text = ad.price
        holder.seller.text = ad.seller
        holder.dorm.text = ad.dorm

        holder.itemView.setOnClickListener {
            openAdDetail(it.context, ad)
        }
    }

    override fun getItemCount(): Int = ads.size

    fun updateAds(newAds: List<Ad>) {
        ads = newAds
        notifyDataSetChanged()
    }

    private fun openAdDetail(context: Context, ad: Ad) {
        val intent = Intent(context, AdDetailActivity::class.java).apply {
            putExtra("title", ad.title)
            putExtra("price", ad.price)
            putExtra("dorm", ad.dorm)
            putExtra("seller", ad.seller)
            putExtra("description", ad.description)
            putExtra("imageResId", ad.imageResId)
        }
        context.startActivity(intent)
    }
}