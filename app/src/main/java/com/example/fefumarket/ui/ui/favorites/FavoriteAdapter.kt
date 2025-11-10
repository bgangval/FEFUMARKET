package com.example.fefumarket.favorites

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.ad.AdDetailActivity
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.FavoritesManager

class FavoriteAdapter(
    val items: MutableList<Ad>
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemTitle: TextView = view.findViewById(R.id.itemTitle)
        val itemDescription: TextView = view.findViewById(R.id.itemDescription)
        val btnRemoveFavorite: ImageButton = view.findViewById(R.id.btnRemoveFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemTitle.text = item.title
        holder.itemDescription.text = item.price

        // ✅ Загружаем первое фото, если оно есть
        if (item.imageUris.isNotEmpty()) {
            Glide.with(holder.itemImage.context)
                .load(item.imageUris[0])   // первое фото
                .centerCrop()
                .into(holder.itemImage)
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_camera) // заглушка
        }

        holder.btnRemoveFavorite.setImageResource(R.drawable.ic_heart_red)

        holder.btnRemoveFavorite.setOnClickListener {
            FavoritesManager.remove(item)
            removeItem(position)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdDetailActivity::class.java)
            intent.putExtra("AD_ID", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}