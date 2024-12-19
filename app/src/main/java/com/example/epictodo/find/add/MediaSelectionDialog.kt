package com.example.epictodo.find.add

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.example.epictodo.R

class MediaSelectionDialog(context: Context, private val listener: MediaSelectionListener) : Dialog(context) {

    interface MediaSelectionListener {
        fun openCamera()
        fun pickMediaFromGallery()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_media)

        findViewById<Button>(R.id.btnTakePhoto).setOnClickListener { listener.openCamera(); dismiss() }
        findViewById<Button>(R.id.btnPickImage).setOnClickListener { listener.pickMediaFromGallery(); dismiss() }
    }
}