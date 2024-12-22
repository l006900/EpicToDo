package com.example.epictodo.find.add

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
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

                // 设置 MediaController 以便用户可以控制播放/暂停等操作
                val mediaController = MediaController(this)
                mediaController.setAnchorView(videoView)
                videoView.setMediaController(mediaController)

                videoView.setVideoURI(uri)
                videoView.setOnPreparedListener { mediaPlayer ->
                    // 视频准备就绪后开始播放
                    mediaPlayer.start()
                }
                videoView.setOnErrorListener { _, _, _ ->
                    // 播放错误处理
                    Log.e("MediaViewActivity", "视频播放失败")
                    Toast.makeText(this, "视频播放失败，请检查文件格式或网络连接", Toast.LENGTH_SHORT).show()
                    false
                }
            } else {
                videoView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                Glide.with(this)
                    .load(it)
                    .into(imageView)
            }
        } ?: run {
            // 如果没有接收到 mediaPath，显示错误信息
            Toast.makeText(this, "未接收到媒体文件路径", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 判断是否为视频文件
    private fun isVideo(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return mimeType != null && mimeType.startsWith("video/")
    }
}
