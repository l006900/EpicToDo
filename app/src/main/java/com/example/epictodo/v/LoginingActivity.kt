package com.example.epictodo.v

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.epictodo.R
import com.google.android.material.textfield.TextInputLayout

class LoginingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logining)

        val textInputLayout: TextInputLayout = findViewById(R.id.logining_password)

        textInputLayout.isPasswordVisibilityToggleEnabled = true
        textInputLayout.passwordVisibilityToggleDrawable =
            ContextCompat.getDrawable(this, R.drawable.baseline_vpn_key_24)
    }
}