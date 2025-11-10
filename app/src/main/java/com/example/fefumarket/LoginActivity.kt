package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManager(this)

        // Если пользователь уже залогинен – отправляем в HomeActivity
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
            } else if (emailText == "test@mail.com" && passText == "123") {

                session.saveLogin(emailText)

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Неверная почта или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        register.setOnClickListener {
            val intent = Intent(this, EmailInputActivity::class.java)
            startActivity(intent)
        }
    }
}