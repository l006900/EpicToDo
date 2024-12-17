package com.example.epictodo.epic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epictodo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EpicTagDialog : BottomSheetDialogFragment(), EpicTagAdapter.OnTagClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var epicTagAdapter: EpicTagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_epic_tag, container, false)

        recyclerView = view.findViewById(R.id.dialog_epic_tag_recycle)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val epicTagItems = listOf(
            EpicTagItem(1, "学习", "10:00", true),
            EpicTagItem(2, "运动", "11:00", false),
            EpicTagItem(3, "工作", "12:00", false)
        )

        epicTagAdapter = EpicTagAdapter(epicTagItems)
        recyclerView.adapter = epicTagAdapter

        return view
    }

    override fun onTagClickListener(tagItem: EpicTagItem) {

        dismiss()
        // 更新Fragment中的UI
//        (parentFragment as? EpicFragment)?.updateTag()
    }
}