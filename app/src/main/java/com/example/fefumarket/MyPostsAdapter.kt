package com.example.fefumarket

import android.content.Context
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

class MyPostsAdapter(
    private var ads: MutableList<Ad>,
    private val onEditClick: (Ad) -> Unit
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

        // Загружаем первое фото через Glide, если есть
        val firstPhotoUri = ad.imageUris.firstOrNull()?.toUri()
        if (firstPhotoUri != null) {
            Glide.with(holder.itemView.context)
                .load(firstPhotoUri)
                .centerCrop()
                .into(holder.adImage)
        } else {
            holder.adImage.setImageResource(R.drawable.ic_camera) // заглушка
        }

        // Клик по карточке — открыть детали объявления
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdDetailActivity::class.java).apply {
                putExtra("AD_ID", ad.id)
            }
            context.startActivity(intent)
        }

        // Кнопка "Изменить"
        holder.btnEdit.setOnClickListener {
            onEditClick(ad)
        }
    }

    override fun getItemCount(): Int = ads.size

    fun updateList(newAds: MutableList<Ad>) {
        ads.clear()
        ads.addAll(newAds)
        notifyDataSetChanged()
    }
}