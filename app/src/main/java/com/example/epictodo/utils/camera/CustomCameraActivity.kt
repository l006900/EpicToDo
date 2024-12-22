package com.example.epictodo.utils.camera

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.epictodo.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CustomCameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var isRecording = false
    private lateinit var viewFinder: PreviewView
    private lateinit var captureButton: Button
    private lateinit var switchCameraButton: ImageButton
    private lateinit var switchModeButton: ImageButton
    private var isPhotoMode = true
    private var recordingStartTime: Long = 0

    // 动画集
    private lateinit var recordStartAnimation: AnimatorSet
    private lateinit var recordStopAnimation: AnimatorSet

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true) {
                startCamera()
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera)

        viewFinder = findViewById(R.id.viewFinder)
        captureButton = findViewById(R.id.captureButton)
        switchCameraButton = findViewById(R.id.switchCameraButton)
        switchModeButton = findViewById(R.id.switchModeButton)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 加载动画
        recordStartAnimation = AnimatorInflater.loadAnimator(this, R.anim.record_start_animation) as AnimatorSet
        recordStopAnimation = AnimatorInflater.loadAnimator(this, R.anim.record_stop_animation) as AnimatorSet

        captureButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isPhotoMode) {
                        startRecording()
                        recordStartAnimation.setTarget(captureButton)
                        recordStartAnimation.start()
                        recordingStartTime = System.currentTimeMillis()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isPhotoMode) {
                        takePhoto()
                    } else if (isRecording) {
                        val recordingDuration = System.currentTimeMillis() - recordingStartTime
                        if (recordingDuration >= 1000) { // Only stop if recording lasted at least 1 second
                            stopRecording()
                            recordStopAnimation.setTarget(captureButton)
                            recordStopAnimation.start()
                        } else {
                            cancelRecording()
                            recordStopAnimation.setTarget(captureButton)
                            recordStopAnimation.start()
                        }
                    }
                    true
                }
                else -> false
            }
        }


        switchCameraButton.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
            startCamera()
        }

        switchModeButton.setOnClickListener {
            isPhotoMode = !isPhotoMode
            updateCaptureMode()
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture
                )
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra("mediaUri", savedUri.toString())
                        putExtra("mediaType", "photo")
                    })
                    finish()
                }
            }
        )
    }

    private fun startRecording() {
        val videoCapture = videoCapture ?: return

        isRecording = true

        val videoFile = createFile(outputDirectory, FILENAME, VIDEO_EXTENSION)
        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val savedUri = recordEvent.outputResults.outputUri
                            Log.d(TAG, "Video capture succeeded: $savedUri")
                            setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra("mediaUri", savedUri.toString())
                                putExtra("mediaType", "video")
                            })
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                            setResult(Activity.RESULT_CANCELED)
                        }
                        isRecording = false
                        finish()
                    }
                }
            }
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }

    private fun cancelRecording() {
        recording?.close()
        recording = null
        isRecording = false
    }

    private fun updateCaptureMode() {
        switchModeButton.setImageResource(
            if (isPhotoMode) R.drawable.ic_photo_camera else R.drawable.ic_videocam
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        return filesDir
/*        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir*/
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CustomCameraActivity"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val VIDEO_EXTENSION = ".mp4"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun createFile(baseFolder: File, format: String, extension: String) =
        File(baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension)
}
