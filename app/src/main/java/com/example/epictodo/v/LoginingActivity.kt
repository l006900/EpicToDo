package com.example.epictodo.v

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.epictodo.R

class LoginingActivity : AppCompatActivity() {
    private var loginNumberFragment: LoginNumberFragment? = null
    private var loginAccountFragment: LoginAccountFragment? = null
    private var loginFastFragment: LoginFastFragment? = null
    private lateinit var loginGroup: RadioGroup
    private lateinit var loginNumber: RadioButton
    private lateinit var loginWeChar: ImageView
    private lateinit var buttonMore: MoreLoginButton
    private lateinit var buttonProblem: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logining)

        loginGroup = findViewById(R.id.login_button)
        loginNumber = findViewById(R.id.login_radio_number)
        loginWeChar = findViewById(R.id.login_wechat)
        buttonMore = findViewById(R.id.login_more)
        buttonProblem = findViewById(R.id.login_problem)

        loginNumber.isChecked = true

        selectedFragment(0)
        loginGroup.setOnCheckedChangeListener() { group, checkedId ->
            when (checkedId) {
                R.id.login_radio_number -> selectedFragment(0)
                R.id.login_radio_account -> selectedFragment(1)
                else -> selectedFragment(2)
            }
        }

        loginWeChar.setOnClickListener {
            openWechat()
        }

        buttonMore.getQq().setOnClickListener {
            openQq()
        }

        buttonMore.getGoogle().setOnClickListener {
            openGoogle()
        }

        buttonProblem.setOnClickListener {
//            startActivity(Intent(this, LoginProblem::class.java))
        }

    }

    private fun selectedFragment(position: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        hideFragment(fragmentTransaction)
        when (position) {
            0 -> {
                if (loginNumberFragment == null) {
                    loginNumberFragment = LoginNumberFragment()
                    fragmentTransaction.add(R.id.login_frame, loginNumberFragment!!)
                } else {
                    fragmentTransaction.show(loginNumberFragment!!)
                }
            }

            1 -> {
                if (loginAccountFragment == null) {
                    loginAccountFragment = LoginAccountFragment()
                    fragmentTransaction.add(R.id.login_frame, loginAccountFragment!!)
                } else {
                    fragmentTransaction.show(loginAccountFragment!!)
                }
            }

            else -> {
                if (loginFastFragment == null) {
                    loginFastFragment = LoginFastFragment()
                    fragmentTransaction.add(R.id.login_frame, loginFastFragment!!)
                } else {
                    fragmentTransaction.show(loginFastFragment!!)
                }
            }
        }
        fragmentTransaction.commit()
    }

    private fun hideFragment(fragmentTransaction: FragmentTransaction) {
        loginNumberFragment?.let { fragmentTransaction.hide(it) }
        loginAccountFragment?.let { fragmentTransaction.hide(it) }
        loginFastFragment?.let { fragmentTransaction.hide(it) }
    }

    private fun openWechat() {
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

    private fun openQq() {
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

    private fun openGoogle() {
        Toast.makeText(this, "正在打开Google...", Toast.LENGTH_SHORT).show()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://www.google.com")
        startActivity(intent
        )
    }
}