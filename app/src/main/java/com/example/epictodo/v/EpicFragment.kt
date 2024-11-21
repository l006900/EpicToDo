package com.example.epictodo.v

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.epictodo.R
import com.example.epictodo.vm.EpicMenuProvider

class EpicFragment: Fragment(){
    private var view: View? = null
    private lateinit var tomato: Tomato
    private var toolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_epic, container, false)

        // 设置菜单
        val menuProvider = EpicMenuProvider(this)

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
        toolbar = view?.findViewById(R.id.epic_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tomato = view.findViewById<Tomato>(R.id.tomato)
        val start = view.findViewById<Button>(R.id.start)
        val pause = view.findViewById<Button>(R.id.pause)
        val reset = view.findViewById<Button>(R.id.reset)

        start.setOnClickListener {
            tomato.start()
            start.visibility = View.GONE
            pause.visibility = View.VISIBLE
            reset.visibility = View.VISIBLE
        }

        pause.setOnClickListener {
            if (pause.text.toString() == "暂停") {
                pause.text = "继续"
                tomato.pause()
            } else {
                pause.text = "暂停"
                tomato.start()
            }
        }

        reset.setOnClickListener {
            tomato.reset()
            start.visibility = View.VISIBLE
            pause.visibility = View.GONE
            reset.visibility = View.GONE
        }
    }
}

