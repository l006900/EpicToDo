package com.example.epictodo.find.add

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.epictodo.R

class MediaViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_view)

        val mediaPath = intent.getStringExtra("mediaPath")
        val imageView: ImageView = findViewById(R.id.mediaImageView)

        mediaPath?.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }
    }
}
