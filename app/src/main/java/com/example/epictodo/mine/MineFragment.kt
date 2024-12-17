package com.example.epictodo.mine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epictodo.R
import com.example.epictodo.mine.setting.SettingActivity

class MineFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mine, container, false)

        recyclerView = view.findViewById(R.id.mine_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val items = listOf(
            MineItem("设置"),
            MineItem("演示中心"),
            MineItem("帮助与客服"),
            MineItem("系统权限设置"),
            MineItem("隐私政策"),
            MineItem("个人信息收集清单"),
            MineItem("第三方共享信息清单"),
            MineItem("版本")
        )

        adapter = MineAdapter(items) { item ->
            when (item.title) {
                "设置" -> startActivity(Intent(requireContext(), SettingActivity::class.java))
                "演示中心" -> {} // 处理演示中心点击事件
                "帮助与客服" -> {} // 处理帮助与客服点击事件
                "系统权限设置" -> {} // 处理系统权限设置点击事件
                "隐私政策" -> {} // 处理隐私政策点击事件
                "个人信息收集清单" -> {} // 处理个人信息收集清单点击事件
                "第三方共享信息清单" -> {} // 处理第三方共享信息清单点击事件
                "版本" -> {} // 处理版本点击事件
                else -> {} // 默认处理
            }
        }

        recyclerView.adapter = adapter

        return view
    }
}