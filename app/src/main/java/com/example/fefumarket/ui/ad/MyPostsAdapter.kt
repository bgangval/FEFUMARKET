package com.example.fefumarket.ui.ad

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.core.net.toUri
import com.example.fefumarket.R
import com.example.fefumarket.data.models.Ad

// Адаптер для RecyclerView в MyPostsActivity
// Отображает список объявлений пользователя с кнопкой редактирования и кликом для открытия деталей
class MyPostsAdapter(
    private var ads: MutableList<Ad>,
    private val onEditClick: (Ad) -> Unit // 🔹 Логика передачи выбранного объявления для редактирования
) : RecyclerView.Adapter<MyPostsAdapter.MyPostViewHolder>() {

    inner class MyPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adImage: ImageView = itemView.findViewById(R.id.adImage)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_post, parent, false)
        return MyPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val ad = ads[position]

        holder.tvTitle.text = ad.title
        holder.tvPrice.text = ad.price

        // 🔹 Загружаем первое фото через Glide или показываем заглушку
        val firstPhotoUri = ad.imageUris.firstOrNull()?.toUri()
        if (firstPhotoUri != null) {
            Glide.with(holder.itemView.context)
                .load(firstPhotoUri)
                .centerCrop()
                .into(holder.adImage)
        } else {
            holder.adImage.setImageResource(R.drawable.ic_camera)
        }

        // 🔹 Клик по карточке — открытие деталей объявления через AdDetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdDetailActivity::class.java).apply {
                putExtra("AD_ID", ad.id)
            }
            context.startActivity(intent)
        }

        // 🔹 Кнопка "Изменить" вызывает callback для редактирования
        holder.btnEdit.setOnClickListener {
            onEditClick(ad)
        }
    }

    override fun getItemCount(): Int = ads.size

    // 🔹 Обновление списка объявлений
    fun updateList(newAds: MutableList<Ad>) {
        ads.clear()
        ads.addAll(newAds)
        notifyDataSetChanged()
    }
}