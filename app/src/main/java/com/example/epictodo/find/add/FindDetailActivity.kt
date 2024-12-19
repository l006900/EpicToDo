package com.example.epictodo.find.add

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.epictodo.R
import com.example.epictodo.databinding.ActivityFindDetailBinding
import com.example.epictodo.find.vm.FindViewModel

class FindDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindDetailBinding
    private lateinit var viewModel: FindViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FindViewModel::class.java)

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

                    Glide.with(this)
                        .load(it.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(binding.postImage)
                }
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
}