package com.example.fefumarket.ui.chat

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class StickerAdapter(
    private val stickers: List<Int>,
    private val onStickerClick: (Int) -> Unit // 🔹 callback при выборе стикера
) : RecyclerView.Adapter<StickerAdapter.StickerViewHolder>() {

    class StickerViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        // 🔹 Создание ImageView для стикера
        val iv = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(120, 120) // размер стикера
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(8, 8, 8, 8)
        }
        return StickerViewHolder(iv)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        // 🔹 Загружаем стикер и передаём выбор через onStickerClick
        holder.imageView.setImageResource(stickers[position])
        holder.imageView.setOnClickListener {
            onStickerClick(stickers[position])
        }
    }

    override fun getItemCount(): Int = stickers.size
}