package com.example.epictodo.find.vm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.epictodo.R
import com.example.epictodo.find.add.FindDetailActivity
import com.example.epictodo.find.m.FindEntity
import de.hdodenhof.circleimageview.CircleImageView

class FindAdapter : RecyclerView.Adapter<FindAdapter.FindViewHolder>() {
    private var findEntities: List<FindEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_find, parent, false)
        return FindViewHolder(view)
    }

    override fun onBindViewHolder(holder: FindViewHolder, position: Int) {
        holder.bind(findEntities[position])
    }

    override fun getItemCount(): Int = findEntities.size

    fun setFindEntities(entities: List<FindEntity>) {
        findEntities = entities
        notifyDataSetChanged()
    }

    inner class FindViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.find_item_iv)
        private val avatarView: CircleImageView = itemView.findViewById(R.id.find_item_avatar)
        private val authorTextView: TextView = itemView.findViewById(R.id.find_item_author)
        private val titleTextView: TextView = itemView.findViewById(R.id.find_item_title)

        fun bind(findEntity: FindEntity) {
            authorTextView.text = findEntity.userName
            titleTextView.text = findEntity.title

            Glide.with(imageView.context)
                .load(findEntity.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)

            Glide.with(avatarView.context)
                .load(findEntity.userAvatar)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(avatarView)

            // Adjust the layout params for the waterfall effect
            val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = false
            itemView.layoutParams = layoutParams

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, FindDetailActivity::class.java)
                intent.putExtra("FIND_ENTITY_TIMESTAMP", findEntity.timestamp)
                itemView.context.startActivity(intent)
            }
        }
    }
}