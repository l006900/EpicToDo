package com.example.epictodo.login.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.epictodo.R
import com.example.epictodo.databinding.ActivityLoginingBinding
import com.example.epictodo.home.HomeActivity

class LoginingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginingBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginNumber.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.loginMore.setOnClickListener {
            showMoreButton()
        }

        binding.loginSkip.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showMoreButton() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_login_other, null)

        val loginWechat = dialogView.findViewById<ImageView>(R.id.dialog_login_wechat)
        val loginQQ = dialogView.findViewById<ImageView>(R.id.dialog_login_qq)
        val loginGoogle = dialogView.findViewById<ImageView>(R.id.dialog_login_google)

        val builder = AlertDialog.Builder(this, R.style.customDialog)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        // 获取弹窗的窗口对象
        val window = alertDialog.window
        window?.let {
            // 设置弹窗的对齐方式为底部
            it.setGravity(Gravity.BOTTOM)

            // 调整弹窗的宽度和高度
            val params = it.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.attributes = params
        }


        loginWechat.setOnClickListener{
            try {
                Toast.makeText(this, "正在打开微信...", Toast.LENGTH_SHORT).show()

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("weixin://")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "未安装微信，请先安装微信", Toast.LENGTH_SHORT).show()
            }
        }

        loginQQ.setOnClickListener {
            try {
                Toast.makeText(this, "正在打开QQ...", Toast.LENGTH_SHORT).show()

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("mqq://")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "未安装QQ，请先安装QQ", Toast.LENGTH_SHORT).show()
            }
        }

        loginGoogle.setOnClickListener {
            Toast.makeText(this, "正在打开Google...", Toast.LENGTH_SHORT).show()

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.google.com")
            startActivity(intent
            )
        }
    }
}