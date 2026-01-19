package com.example.fefumarket.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.data.models.MessageItem
import android.widget.ImageView

class MessageAdapter(private val messages: List<MessageItem>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: FrameLayout = view.findViewById(R.id.messageContainer)
        val messageText: TextView = view.findViewById(R.id.messageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == 1)
            R.layout.item_message_user else R.layout.item_message_other

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messages[position]

        holder.container.removeAllViews()

        if (msg.isSticker && msg.stickerRes != null) {
            val iv = ImageView(holder.container.context).apply {
                setImageResource(msg.stickerRes)
                layoutParams = FrameLayout.LayoutParams(200, 200)
            }
            holder.container.addView(iv)
        } else {
            holder.messageText.visibility = View.VISIBLE
            holder.messageText.text = msg.text
            holder.container.addView(holder.messageText)
        }

        holder.container.foregroundGravity = if (msg.isUser) Gravity.END else Gravity.START
    }

    override fun getItemViewType(position: Int): Int =
        if (messages[position].isUser) 1 else 0

    override fun getItemCount() = messages.size
}