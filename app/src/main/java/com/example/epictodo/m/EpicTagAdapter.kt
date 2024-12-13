package com.example.epictodo.m

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.epictodo.R
import com.google.android.material.button.MaterialButton

class EpicTagAdapter(private val epicTagItems: List<EpicTagItem>) :
    RecyclerView.Adapter<EpicTagAdapter.EpicTagViewHolder>() {

    interface OnTagClickListener {
        fun onTagClickListener(tagItem: EpicTagItem)
    }

    private var selectedPosition: Int = -1

    class EpicTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagEpic: TextView = itemView.findViewById(R.id.item_epic_tag)
        val time: TextView = itemView.findViewById(R.id.item_epic_time)
        val selectButton: MaterialButton = itemView.findViewById(R.id.item_epic_select)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpicTagViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_epic_tag, parent, false)
        return EpicTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpicTagViewHolder, position: Int) {
        val item = epicTagItems[position]
        holder.tagEpic.text = item.tag
        holder.time.text = item.time

        val currentPosition = holder.getAdapterPosition()
        if (currentPosition == selectedPosition) {
            holder.selectButton.text = "已选择"
            holder.selectButton.isChecked = true
        } else {
            holder.selectButton.text = "选择"
            holder.selectButton.isChecked = false
        }

        // 点击事件
        holder.selectButton.setOnClickListener {
            val newPosition = holder.getAdapterPosition()
            if (newPosition != RecyclerView.NO_POSITION && selectedPosition != newPosition) {
                val previousSelectedPosition = selectedPosition
                selectedPosition = newPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(newPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return epicTagItems.size
    }
}