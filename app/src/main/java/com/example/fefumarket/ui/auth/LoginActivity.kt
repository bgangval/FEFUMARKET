package com.example.fefumarket.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fefumarket.R
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.ui.home.HomeActivity
import com.example.fefumarket.network.RetrofitClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.fefumarket.data.models.LoginRequest
import com.example.fefumarket.data.models.LoginResponse

class LoginActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManager(this)

        // 🔹 Если пользователь уже залогинен, переходим сразу на HomeActivity
        session.getLogin()?.let {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val register = findViewById<TextView>(R.id.register)

        loginButton.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            if (emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = RetrofitClient.create(this)

            lifecycleScope.launch {
                try {
                    // 🔹 Логика авторизации через API
                    val response = api.login(
                        LoginRequest(
                            email = emailText,
                            password = passText
                        )
                    )

                    // 🔹 Сохраняем токен и логин пользователя в SessionManager
                    session.saveToken(response.access_token)
                    session.saveLogin(emailText)

                    Log.d("AUTH", "TOKEN = ${response.access_token}")

                    // 🔹 Переходим на главный экран после успешного логина
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка входа",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.e("AUTH", "Login error", e)
                }
            }
        }

        // 🔹 Переход на экран регистрации (EmailInputActivity)
        register.setOnClickListener {
            val intent = Intent(this, EmailInputActivity::class.java)
            startActivity(intent)
        }
    }
}