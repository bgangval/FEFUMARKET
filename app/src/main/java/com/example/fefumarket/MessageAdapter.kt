package com.example.fefumarket

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        holder.messageText.text = msg.text

        if (msg.isUser) {
            // мои сообщения
            holder.container.foregroundGravity = Gravity.END
        } else {
            // сообщения собеседника
            holder.container.foregroundGravity = Gravity.START
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (messages[position].isUser) 1 else 0

    override fun getItemCount() = messages.size
}