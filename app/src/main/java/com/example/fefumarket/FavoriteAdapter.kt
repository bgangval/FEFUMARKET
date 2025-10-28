package com.example.fefumarket

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Адаптер для RecyclerView, показывающего избранные товары
class FavoriteAdapter(
    val items: MutableList<Ad>
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    // ViewHolder — хранит ссылки на элементы макета item_favorite.xml
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
        holder.itemImage.setImageResource(item.imageResId)

        // Устанавливаем красное сердечко, так как товар в избранном
        holder.btnRemoveFavorite.setImageResource(R.drawable.ic_heart_red)

        // Удаление элемента по кнопке
        holder.btnRemoveFavorite.setOnClickListener {
            val ad = items[position]
            FavoritesManager.remove(ad)
            removeItem(position)
        }

        // Открытие деталей по клику на элемент
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdDetailActivity::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("price", item.price)
            intent.putExtra("seller", item.seller)
            intent.putExtra("description", item.description)
            intent.putExtra("imageResId", item.imageResId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    // Удаление элемента из списка
    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}