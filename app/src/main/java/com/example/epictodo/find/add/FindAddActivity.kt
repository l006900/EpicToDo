package com.example.epictodo.find.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.epictodo.R
import com.example.epictodo.find.m.FindEntity
import com.example.epictodo.find.vm.FindViewModel
import com.google.android.material.chip.Chip
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Rect
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.example.epictodo.utils.camera.CustomCameraActivity
import com.google.android.material.chip.ChipGroup

class FindAddActivity : AppCompatActivity(), MediaSelectionDialog.MediaSelectionListener {

    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var addMediaButton: ImageView
    private lateinit var mediaRecyclerView: RecyclerView
    private lateinit var tagChipGroup: ChipGroup
    private lateinit var customTagInput: EditText
    private lateinit var scrollView: ScrollView
    private lateinit var vibrator: Vibrator
    private lateinit var privacySettingValue: TextView
    private lateinit var locationSettingValue: TextView

    private lateinit var mediaAdapter: MediaAdapter
    private val predefinedTags = listOf("旅行", "美食", "健康", "求助", "学习", "工作")
    private lateinit var viewModel: FindViewModel

    private val PERMISSION_REQUEST_CODE = 100
    private val IMAGE_CAPTURE_REQUEST_CODE = 101
    private val VIDEO_CAPTURE_REQUEST_CODE = 102
    private val PICK_IMAGE_REQUEST_CODE = 103
    private val PICK_VIDEO_REQUEST_CODE = 104
    private val PICK_MEDIA_REQUEST_CODE = 105 // 添加这个常量

    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_add)

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        viewModel = ViewModelProvider(this).get(FindViewModel::class.java)

        // 初始化视图
        initializeViews()
        // 设置监听器
        setupListeners()
        // 设置媒体 RecyclerView
        setupMediaRecyclerView()
        // 设置预定义标签
        setupPredefinedTags()
        // 设置拖动删除监听器
        setupDragToDeleteListener()
    }

    internal fun vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }

    // 初始化视图控件
    private fun initializeViews() {
        titleInput = findViewById(R.id.titleInput)
        contentInput = findViewById(R.id.contentInput)
        addMediaButton = findViewById(R.id.addMediaButton)
        mediaRecyclerView = findViewById(R.id.mediaRecyclerView)
        tagChipGroup = findViewById(R.id.tagChipGroup)
        customTagInput = findViewById(R.id.customTagInput)
        scrollView = findViewById(R.id.find_add_scroll)
        privacySettingValue = findViewById(R.id.privacySettingValue)
        locationSettingValue = findViewById(R.id.locationSettingValue)

        scrollView.isVerticalScrollBarEnabled = false // Hide scrollbar

        // 设置返回按钮和发布按钮的点击事件
        findViewById<android.widget.ImageButton>(R.id.backButton).setOnClickListener { onBackPressed() }
        findViewById<android.widget.Button>(R.id.publishButton).setOnClickListener { publishContent() }
    }

    private fun showPrivacySettingDialog() {
        val options = arrayOf("公开", "仅好友可见", "私密")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择隐私设置")
            .setItems(options) { _, which ->
                privacySettingValue.text = options[which]
            }
            .show()
    }

    private fun showLocationSettingDialog() {
        val options = arrayOf("获取当前位置", "选择位置", "不显示位置")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择位置设置")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> getCurrentLocation()
                    1 -> selectLocation()
                    2 -> locationSettingValue.text = "不显示位置"
                }
            }
            .show()
    }

    private fun getCurrentLocation() {
        // Implement location retrieval logic here
        locationSettingValue.text = "当前位置"
    }

    private fun selectLocation() {
        // Implement location selection logic here
        locationSettingValue.text = "选择的位置"
    }

    // 设置监听器
    private fun setupListeners() {
        addMediaButton.setOnClickListener {
            checkPermissionsAndShowMediaOptions()
        }

        customTagInput.setOnEditorActionListener { _, _, _ ->
            addCustomTag()
            true
        }

        privacySettingValue.setOnClickListener {
            showPrivacySettingDialog()
        }

        locationSettingValue.setOnClickListener {
            showLocationSettingDialog()
        }
    }

    // 设置图片 RecyclerView
    private fun setupMediaRecyclerView() {
        mediaAdapter = MediaAdapter(
            onMediaClick = { mediaPath ->
                // 打开图片查看活动
                val intent = Intent(this, MediaViewActivity::class.java)
                intent.putExtra("mediaPath", mediaPath)
                startActivity(intent)
            },
            onMediaLongClick = { mediaPath ->
                // 长按时可以执行其他操作
            },
            activity = this
        )

        mediaRecyclerView.layoutManager = GridLayoutManager(this, 3)
        mediaRecyclerView.adapter = mediaAdapter
        mediaAdapter.setAddMediaButton(addMediaButton)

        // 设置拖动排序监听器
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                mediaAdapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }
        })

        itemTouchHelper.attachToRecyclerView(mediaRecyclerView)
    }

    // 设置预定义标签
    private fun setupPredefinedTags() {
        predefinedTags.forEach { tag ->
            val chip = Chip(this).apply {
                text = tag
                isCheckable = true
            }
            tagChipGroup.addView(chip)
        }
    }

    // 添加自定义标签
    private fun addCustomTag() {
        val tagText = customTagInput.text.toString().trim()
        if (tagText.isNotEmpty()) {
            val chip = Chip(this).apply {
                text = tagText
                isCheckable = true
                isChecked = true
                setChipBackgroundColorResource(R.color.chip_background_color)
                setTextColor(ContextCompat.getColor(this@FindAddActivity, R.color.chip_text_color))
                checkedIcon = null
            }
            tagChipGroup.addView(chip)
            customTagInput.text.clear()
        }
    }

    // 检查权限并显示图片选项
    private fun checkPermissionsAndShowMediaOptions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            showMediaSelectionDialog()
        }
    }

    // 显示媒体选择对话框
    private fun showMediaSelectionDialog() {
        MediaSelectionDialog(this, this).show()
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("mediaUri")?.let { mediaUri ->
                    mediaAdapter.addMedia(mediaUri)
                }
            }
        }

    // 打开相机
    override fun openCamera() {
        val intent = Intent(this, CustomCameraActivity::class.java)
        cameraLauncher.launch(intent)
    }

    // 从图库选择图片和视频
    override fun pickMediaFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/*", "video/*")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "选择图片和视频"),
            PICK_MEDIA_REQUEST_CODE
        )
    }

    // 处理权限请求结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showMediaSelectionDialog()
            } else {
                Toast.makeText(this, "权限被拒绝，无法添加媒体", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 处理活动结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_CAPTURE_REQUEST_CODE -> {
                    currentPhotoPath?.let { path ->
                        mediaAdapter.addMedia(path)
                    }
                }

                VIDEO_CAPTURE_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        mediaAdapter.addMedia(uri.toString())
                    }
                }

                PICK_IMAGE_REQUEST_CODE, PICK_VIDEO_REQUEST_CODE -> {
                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val uri = data.clipData!!.getItemAt(i).uri
                            mediaAdapter.addMedia(uri.toString())
                        }
                    } else {
                        data?.data?.let { uri ->
                            mediaAdapter.addMedia(uri.toString())
                        }
                    }
                }
            }
        }
    }

    // 发布内容
    private fun publishContent() {
        if (validateInput()) {
            val title = titleInput.text.toString()
            val content = contentInput.text.toString()
            val tags =
                tagChipGroup.checkedChipIds.map { tagChipGroup.findViewById<Chip>(it).text.toString() }
            val privacy = privacySettingValue.text.toString()
            val mediaItems = mediaAdapter.getMediaItems()

            // 创建 FindEntity 对象
            val findEntity = FindEntity().apply {
                userId = "user123" // Replace with actual user ID
                this.title = title
                description = content
                imageUrl = mediaItems.firstOrNull() ?: ""
                timestamp = System.currentTimeMillis()
            }

            // 插入新实体到数据库
            viewModel.insert(findEntity)

            Toast.makeText(this, "分享已发布", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 验证输入
    private fun validateInput(): Boolean {
        var isValid = true

        if (titleInput.text.isNullOrBlank()) {
            titleInput.error = "请输入标题"
            isValid = false
        }

        if (contentInput.text.isNullOrBlank()) {
            contentInput.error = "请输入内容"
            isValid = false
        }

        if (tagChipGroup.checkedChipIds.isEmpty()) {
            Toast.makeText(this, "请至少选择一个标签", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    // 设置拖动删除监听器
    private fun setupDragToDeleteListener() {
        val deleteZone = findViewById<View>(R.id.deleteZone)
        val deleteText = findViewById<TextView>(R.id.deleteText)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                mediaAdapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    deleteZone.visibility = View.VISIBLE
                    deleteText.visibility = View.VISIBLE
                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    deleteZone.visibility = View.GONE
                    deleteText.visibility = View.GONE
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if (deleteZone.visibility == View.VISIBLE) {
                    val deleteZoneLocation = IntArray(2)
                    deleteZone.getLocationOnScreen(deleteZoneLocation)
                    val deleteZoneRect = Rect(
                        deleteZoneLocation[0],
                        deleteZoneLocation[1],
                        deleteZoneLocation[0] + deleteZone.width,
                        deleteZoneLocation[1] + deleteZone.height
                    )

                    val viewLocation = IntArray(2)
                    viewHolder.itemView.getLocationOnScreen(viewLocation)
                    val viewRect = Rect(
                        viewLocation[0],
                        viewLocation[1],
                        viewLocation[0] + viewHolder.itemView.width,
                        viewLocation[1] + viewHolder.itemView.height
                    )

                    if (Rect.intersects(deleteZoneRect, viewRect)) {
                        mediaAdapter.removeItem(viewHolder.adapterPosition)
                    }
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(mediaRecyclerView)
    }
}

// 图片适配器
class MediaAdapter(
    private val onMediaClick: (String) -> Unit,
    private val onMediaLongClick: (String) -> Unit,
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {
    private val mediaItems: MutableList<String> = mutableListOf()
    private lateinit var addMediaButton: ImageView

    // ViewHolder 类
    class MediaViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.mediaImageView)
        val orderNumber: TextView = itemView.findViewById(R.id.orderNumber) // 添加这行
    }

    // 创建 ViewHolder
    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): MediaViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_glide, parent, false)
        return MediaViewHolder(view)
    }

    // 绑定 ViewHolder
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = mediaItems[position]
        Glide.with(holder.imageView.context)
            .load(mediaItem)
            .into(holder.imageView)

        holder.orderNumber.text = (position + 1).toString()

        holder.itemView.setOnClickListener {
            onMediaClick(mediaItem)
        }

        holder.itemView.setOnLongClickListener {
            onMediaLongClick(mediaItem)
            (activity as FindAddActivity).vibrateDevice()
            holder.imageView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start()
            holder.imageView.elevation = 20f
            holder.itemView.postDelayed({
                holder.imageView.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                holder.imageView.elevation = 0f
            }, 1000)
            true
        }
    }

    // 获取项目数量
    override fun getItemCount(): Int = mediaItems.size

    // 添加图片
    fun addMedia(mediaPath: String) {
        mediaItems.add(mediaPath)
        notifyItemInserted(mediaItems.size - 1)
    }

    // 获取图片
    fun getMediaItems(): List<String> = mediaItems

    // 移动项目
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = mediaItems.removeAt(fromPosition)
        mediaItems.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    // 移除项目
    fun removeItem(position: Int) {
        mediaItems.removeAt(position)
        notifyItemRemoved(position)
    }

    // 设置添加媒体按钮
    fun setAddMediaButton(button: ImageView) {
        this.addMediaButton = button
    }
}