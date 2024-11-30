package com.example.epictodo.v

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.epictodo.R


class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.login_number)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginingActivity::class.java)
            startActivity(intent)
        }

    }
}