package com.example.fefumarket.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.fefumarket.R
import com.example.fefumarket.data.models.api.RegisterRequest
import com.example.fefumarket.data.models.api.RegisterResponse
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log
import com.example.fefumarket.ui.home.HomeActivity

class PasswordCreationActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password_creation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        session = SessionManager(this)

        val emailFromIntent = intent.getStringExtra("EMAIL") ?: ""

        val passwordInput = findViewById<EditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirm_password_input)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            } else {
                // 🔹 Отправка данных регистрации на сервер через API
                val api = RetrofitClient.create(this)
                lifecycleScope.launch {
                    try {
                        val response: RegisterResponse = api.register(
                            RegisterRequest(
                                email = emailFromIntent,
                                password = password
                            )
                        )

                        // 🔹 Сохраняем токен и email локально для последующего входа
                        session.saveToken(response.access_token)
                        session.saveLogin(emailFromIntent)

                        // 🔹 После успешной регистрации — переходим на главный экран
                        Toast.makeText(this@PasswordCreationActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@PasswordCreationActivity, HomeActivity::class.java))
                        finish()

                    } catch (e: Exception) {
                        Toast.makeText(this@PasswordCreationActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                        Log.e("REGISTER", "Registration error", e)
                    }
                }
            }
        }
    }
}