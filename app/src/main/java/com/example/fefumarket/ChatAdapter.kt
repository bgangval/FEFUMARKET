package com.example.fefumarket

import android.animation.ValueAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatAdapter(val chats: MutableList<ChatItem>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.chatAvatar)
        val name: TextView = itemView.findViewById(R.id.chatName)
        val product: TextView = itemView.findViewById(R.id.chatProduct)
        val lastMessage: TextView = itemView.findViewById(R.id.chatLastMessage)
        val muteIcon: ImageView = itemView.findViewById(R.id.chatMuteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        // ✅ Загружаем avatarUri в ImageView
        Glide.with(holder.itemView.context)
            .load(chat.avatarUri.toUri())
            .placeholder(R.drawable.ic_camera)
            .centerCrop()
            .into(holder.avatar)

        holder.name.text = chat.sellerName
        holder.product.text = chat.productName
        holder.lastMessage.text = chat.lastMessage

        holder.muteIcon.setImageResource(
            if (chat.isMuted) R.drawable.ic_mute else R.drawable.ic_unmute
        )
        holder.muteIcon.alpha = if (chat.isMuted) 1f else 0f
        holder.muteIcon.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, MessageActivity::class.java).apply {
                putExtra("CHAT_ID", "${chat.sellerName}_${chat.productName}")
                putExtra("SELLER_NAME", chat.sellerName)
                putExtra("PRODUCT_NAME", chat.productName)
                putExtra("AVATAR_URI", chat.avatarUri)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = chats.size

    fun removeAt(position: Int) {
        chats.removeAt(position)
        notifyItemRemoved(position)
    }

    fun toggleMute(position: Int) {
        val chat = chats[position]
        chat.isMuted = !chat.isMuted

        val holder = recyclerView?.findViewHolderForAdapterPosition(position) as? ChatViewHolder
        holder?.let {
            val startAlpha = if (chat.isMuted) 0f else 1f
            val endAlpha = if (chat.isMuted) 1f else 0f
            val animator = ValueAnimator.ofFloat(startAlpha, endAlpha)
            animator.duration = 300
            animator.addUpdateListener { valueAnimator ->
                it.muteIcon.alpha = valueAnimator.animatedValue as Float
            }
            animator.start()
        }

        notifyItemChanged(position)
    }

    var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}