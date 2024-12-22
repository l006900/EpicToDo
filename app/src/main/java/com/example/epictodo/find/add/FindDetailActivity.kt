package com.example.epictodo.find.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.epictodo.R
import com.example.epictodo.databinding.ActivityFindDetailBinding
import com.example.epictodo.find.vm.FindViewModel
import java.io.File

class FindDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindDetailBinding
    private lateinit var viewModel: FindViewModel
    private lateinit var mediaAdapter: MediaPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FindViewModel::class.java)

        setupViews()
        loadData()
        setupListeners()
    }

    private fun setupViews() {
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        // Set up comments adapter here

        mediaAdapter = MediaPagerAdapter()
        binding.mediaViewPager.adapter = mediaAdapter
        binding.mediaViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun loadData() {
        val timestamp = intent.getLongExtra("FIND_ENTITY_TIMESTAMP", -1L)
        if (timestamp != -1L) {
            viewModel.getItemByTimestamp(timestamp).observe(this) { findEntity ->
                findEntity?.let {
                    binding.authorName.text = it.userName
                    binding.postTitle.text = it.title
                    binding.postDescription.text = it.description

                    Glide.with(this)
                        .load(it.userAvatar)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(binding.authorAvatar)

                    // Update media items
                    mediaAdapter.setMediaItems(it.mediaUrls ?: emptyList())

                    // Load like, favorite, and comment counts
                    binding.starCount.text = it.starCount.toString()
                    binding.favoriteCount.text = it.favoriteCount.toString()
                    binding.commentCount.text = it.commentCount.toString()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { onBackPressed() }
        binding.followButton.setOnClickListener { /* Implement follow functionality */ }
        binding.shareButton.setOnClickListener { /* Implement share functionality */ }
        binding.starButton.setOnClickListener { /* Implement like functionality */ }
        binding.favoriteButton.setOnClickListener { /* Implement favorite functionality */ }
        binding.commentButton.setOnClickListener { /* Implement comment functionality */ }

        // Implement comment submission
        binding.commentInput.setOnEditorActionListener { _, _, _ ->
            submitComment()
            true
        }
    }

    private fun submitComment() {
        val commentText = binding.commentInput.text.toString()
        if (commentText.isNotBlank()) {
            // Implement comment submission logic
            binding.commentInput.text.clear()
        }
    }
}

class MediaPagerAdapter : RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {
    private var mediaItems: List<String> = emptyList()

    fun setMediaItems(items: List<String>) {
        mediaItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(mediaItems[position])
    }

    override fun getItemCount(): Int = mediaItems.size

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mediaView: ImageView = itemView.findViewById(R.id.mediaView)

        fun bind(mediaUrl: String) {
            Glide.with(mediaView.context)
                .load(File(mediaUrl))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(mediaView)
        }
    }
}