package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class EmailInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_input)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.email_input)
        val sendCodeButton = findViewById<Button>(R.id.send_code_button)

        sendCodeButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            } else {
                val verificationCode = String.format("%06d", Random.nextInt(1000000))
                Toast.makeText(this, "Код отправлен: $verificationCode", Toast.LENGTH_LONG).show()
                val intent = Intent(this, CodeVerificationActivity::class.java)
                intent.putExtra("VERIFICATION_CODE", verificationCode)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
                finish()
            }
        }
    }
}