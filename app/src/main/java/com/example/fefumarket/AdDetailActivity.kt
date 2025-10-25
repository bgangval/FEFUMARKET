package com.example.fefumarket

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        val adImage: ImageView = findViewById(R.id.productImage)
        val adTitle: TextView = findViewById(R.id.titleText)
        val adPrice: TextView = findViewById(R.id.priceText)
        val adDorm: TextView = findViewById(R.id.dormText)
        val adDescription: TextView = findViewById(R.id.descriptionText)

        val btnChat: Button = findViewById(R.id.contactButton)
        val btnCall: Button = findViewById(R.id.favoriteButton)

        // Получаем данные из intent
        val title = intent.getStringExtra("title")
        val dorm = intent.getStringExtra("dorm")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("description")
        val imageResId = intent.getIntExtra("imageResId", R.drawable.laptop_ic_test)

        adTitle.text = title
        adPrice.text = price
        adDorm.text = "Общежитие: $dorm"
        adDescription.text = description
        adImage.setImageResource(imageResId)

        btnChat.setOnClickListener {
            Toast.makeText(this, "Написать продавцу (TODO)", Toast.LENGTH_SHORT).show()
        }

        btnCall.setOnClickListener {
            Toast.makeText(this, "В избранное (TODO)", Toast.LENGTH_SHORT).show()
        }
    }
}