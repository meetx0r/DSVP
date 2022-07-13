package com.meet.damnsmallvideoplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.meet.damnsmallvideoplayer.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private var position: Int = 0
    private var path: Uri? = "".toUri()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = getColor(R.color.video_layout_color)
        window.statusBarColor = getColor(R.color.video_layout_color)
        intent?.let {
            path = if (it.action == Intent.ACTION_VIEW) {
                intent.data
            } else {
                intent.getStringExtra("URI")?.toUri()
            }
        }
        val videoView = binding.video
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
//        videoView.setVideoURI(path?.toUri())
        videoView.setVideoURI(path)
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            fullScreen()
            fullBrightness()
            if (android.provider.Settings.System.getInt(
                    contentResolver,
                    android.provider.Settings.System.ACCELEROMETER_ROTATION,
                    0
                ) != 1
            ) {
                rotateScreen()
            }
        }
        videoView.start()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun rotateScreen() {
        val bmp: Bitmap? = bitmap()
        val videoWidth = bmp!!.width
        val videoHeight = bmp.height
        // if width > height => Landscape and if height > width => Portrait
        if (videoWidth > videoHeight) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        if (videoWidth < videoHeight) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun bitmap(): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, path)
        return retriever.frameAtTime
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.video.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            binding.video.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.video.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
            binding.video.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT

        }
    }

    @Suppress("Deprecation")
    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.systemBars())
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    private fun fullBrightness() {
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
    }

    override fun onPause() {
        super.onPause()
        position = binding.video.currentPosition
    }

    override fun onResume() {
        super.onResume()
        binding.video.seekTo(position)
        binding.video.start()
        fullScreen()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}