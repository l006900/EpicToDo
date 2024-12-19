package com.example.epictodo.find.add

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.epictodo.R

class MediaViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_view)

        val mediaPath = intent.getStringExtra("mediaPath")
        val imageView: ImageView = findViewById(R.id.mediaImageView)
        val videoView: VideoView = findViewById(R.id.mediaVideoView)

        mediaPath?.let {
            val uri = Uri.parse(it)
            if (isVideo(uri)) {
                imageView.visibility = View.GONE
                videoView.visibility = View.VISIBLE
                videoView.setVideoURI(uri)
                videoView.start()
            } else {
                videoView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                Glide.with(this)
                    .load(it)
                    .into(imageView)
            }
        }
    }

    // 判断是否为视频文件
    private fun isVideo(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return mimeType != null && mimeType.startsWith("video/")
    }
}
