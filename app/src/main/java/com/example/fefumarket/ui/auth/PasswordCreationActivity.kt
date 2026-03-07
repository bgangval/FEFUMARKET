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
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log
import com.example.fefumarket.ui.home.HomeActivity
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

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
            val nameInput = findViewById<EditText>(R.id.name_input)
            val name = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (emailFromIntent.isEmpty()) {
                showToast("Ошибка: email не найден. Начните регистрацию заново.", Toast.LENGTH_LONG)
                return@setOnClickListener
            }
            if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Заполните все поля")
            } else if (password != confirmPassword) {
                showToast("Пароли не совпадают")
            } else {
                val api = RetrofitClient.create(this)
                lifecycleScope.launch {
                    try {
                        val response = api.register(
                            RegisterRequest(
                                email = emailFromIntent,
                                password = password,
                                name = name
                            )
                        )
                        session.saveToken(response.access_token)
                        session.saveLogin(emailFromIntent)

                        showToast("Регистрация успешна")
                        startActivity(Intent(this@PasswordCreationActivity, HomeActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        val errorMsg = parseRegistrationError(e)
                        Log.e("REGISTER", "Registration error: $errorMsg", e)
                        showToast(errorMsg, Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this@PasswordCreationActivity, message, duration).show()
    }

    private fun parseRegistrationError(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val body = e.response()?.errorBody()?.string()
                if (!body.isNullOrBlank()) {
                    try {
                        JSONObject(body).optString("detail")
                            .takeIf { it.isNotBlank() }
                            ?: "Ошибка регистрации (${e.code()})"
                    } catch (_: Exception) {
                        "Ошибка регистрации (${e.code()})"
                    }
                } else {
                    when (e.code()) {
                        400 -> "Пользователь с таким email уже существует"
                        422 -> "Проверьте корректность введенных данных"
                        else -> "Ошибка регистрации (${e.code()})"
                    }
                }
            }
            is IOException -> "Не удалось подключиться к серверу"
            else -> e.message ?: "Неизвестная ошибка при регистрации"
        }
    }
}
