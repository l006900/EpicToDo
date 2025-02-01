package com.example.epictodo.login.common

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.epictodo.MainActivity
import com.example.epictodo.R
import com.example.epictodo.databinding.ActivitySplashBinding
import com.example.epictodo.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimation()
    }

    private fun startAnimation() {
        // Background color animation
        val colorAnimator = ValueAnimator.ofArgb(
            ContextCompat.getColor(this, R.color.colorPrimaryDark),
            ContextCompat.getColor(this, R.color.new_yellow)
        )
        colorAnimator.duration = 2000
        colorAnimator.addUpdateListener { animator ->
            binding.splashContainer.setBackgroundColor(animator.animatedValue as Int)
        }

        // Logo animation
        binding.logoImage.setImageResource(R.drawable.logo_animation)
        val logoDrawable = binding.logoImage.drawable

        if (logoDrawable is AnimatedVectorDrawable) {
            logoDrawable.start()
        } else {
            Log.e("SplashActivity", "Drawable is not an AnimatedVectorDrawable")
        }

        // App name fade in
        val fadeIn = ObjectAnimator.ofFloat(binding.appName, View.ALPHA, 0f, 1f)
        fadeIn.duration = 1000
        fadeIn.startDelay = 1500

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator, fadeIn)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (logoDrawable is AnimatedVectorDrawable) {
                    logoDrawable.start()
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                // 读取登录状态
                val sharedPreferences: SharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                val isLoggedInPhone: Boolean = sharedPreferences.getBoolean("isLoggedIn_phone", false)
                val isLoggedInPassword: Boolean = sharedPreferences.getBoolean("isLoggedIn_password", false)
                val isSignInPassword: Boolean = sharedPreferences.getBoolean("isSignIn", false)

                // Start your main activity here
                val intent: Intent

                if (isLoggedInPassword || isLoggedInPhone || isSignInPassword) {
                    intent = Intent(this@SplashActivity, LoginingActivity::class.java)
                } else {
                    intent = Intent(this@SplashActivity, HomeActivity::class.java)
                }

                startActivity(intent)
                finish()

            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

}