package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.fefumarket.ui.auth.LoginActivity

class LoadingActivity : AppCompatActivity() {

    private val splashDelay: Long = 2000 // 2 секунды

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // 🔹 Полноэкранный режим (скрываем status bar и navigation bar)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // 🔹 Переход на LoginActivity через задержку (Splash Screen)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java)) // 🔹 Логика перехода между страницами
            finish() // 🔹 Закрываем Splash, чтобы пользователь не мог вернуться назад
        }, splashDelay)
    }
}