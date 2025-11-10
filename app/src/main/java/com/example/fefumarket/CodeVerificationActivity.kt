package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CodeVerificationActivity : AppCompatActivity() {
    private var verificationCode: String? = null
    private val TAG = "CodeVerificationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_code_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Получаем код из Intent
        verificationCode = intent.getStringExtra("VERIFICATION_CODE")
        Log.d(TAG, "Received verificationCode: $verificationCode")
        Log.d(TAG, "Received email: ${intent.getStringExtra("EMAIL")}")

        val codeInput = findViewById<EditText>(R.id.code_input)
        val verifyButton = findViewById<Button>(R.id.verify_code_button)

        verifyButton.setOnClickListener {
            val enteredCode = codeInput.text.toString().trim()
            Log.d(TAG, "Entered code: $enteredCode")
            if (enteredCode == verificationCode) {
                val email = intent.getStringExtra("EMAIL")
                if (email != null) {
                    val intent = Intent(this, PasswordCreationActivity::class.java)
                    intent.putExtra("EMAIL", email)
                    Log.d(TAG, "Starting PasswordCreationActivity with email: $email")
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(TAG, "Email is null!")
                    Toast.makeText(this, "Ошибка: email не найден", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show()
            }
        }
    }
}