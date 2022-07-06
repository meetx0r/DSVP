package com.meet.damnsmallvideoplayer

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.meet.damnsmallvideoplayer.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private var position: Int = 0
    private var newPath: Uri? = "".toUri()
    private lateinit var viewModel: VideoActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(VideoActivityViewModel::class.java)
        window.navigationBarColor = getColor(R.color.video_layout_color)
        window.statusBarColor = getColor(R.color.video_layout_color)
        val path = intent.getStringExtra("URI")
        newPath = path?.toUri()
        val mediaController = MediaController(this)
        val videoView = binding.video
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(path?.toUri())
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            rotateScreen()
            fullScreen()
            viewModel.currentPosition.observe(this) {
                if (it != 0) {
                    position = it
                    videoView.seekTo(it)
                }
            }
            videoView.setOnClickListener {
                fullScreen()
            }
            videoView.setOnCompletionListener {
                finish()
            }
        }
        videoView.start()

    }


    @SuppressLint("SourceLockedOrientationActivity")
    private fun rotateScreen() {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, newPath)
            val bmp: Bitmap? = retriever.frameAtTime
            val videoWidth = bmp!!.width
            val videoHeight = bmp.height
            // if width > height => Landscape and if height > width => Portrait
            if (videoWidth > videoHeight) {
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                // So if auto rotate is turned on then we don't force the orientation
                if (android.provider.Settings.System.getInt(
                        contentResolver,
                        android.provider.Settings.System.ACCELEROMETER_ROTATION,
                        0
                    ) == 1
                ) {
                    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }

            }
            if (videoWidth < videoHeight) {
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                // if auto rotate is turned on then we don't force the orientation
                if (android.provider.Settings.System.getInt(
                        contentResolver,
                        android.provider.Settings.System.ACCELEROMETER_ROTATION,
                        0
                    ) == 1
                ) {
                    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        } catch (ex: RuntimeException) {
            Toast.makeText(this, "$ex", Toast.LENGTH_SHORT).show()
        }
    }

    // If a user changes orientation then set the width of video view either as wrap_content or match_parent depending on orientation
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.video.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                binding.video.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.video.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                binding.video.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Exception on Config change: $e", Toast.LENGTH_SHORT).show()
        }
    }

    // For FullScreen Function
    @Suppress("Deprecation")
    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars()) // hides status bar
            window.insetsController!!.hide(WindowInsets.Type.navigationBars()) // hides navigation bar
        } else {
            // FLAG_FULLSCREEN is deprecated with newer API versions but is still needed for targeting older API versions
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    // Pause the video when app is paused and set current time of video to position variable.
    override fun onPause() {
        super.onPause()
        position = binding.video.currentPosition
        viewModel.updatePosition(binding.video.currentPosition)
//        viewModel.currentPosition.value = binding.video.currentPosition // Update value of viewModel currentPosition Variable
    }

    // Resume the video when app is resumed and seek to the position when app was paused.
    override fun onResume() {
        super.onResume()
        binding.video.seekTo(position)
        fullScreen()
        binding.video.start()
    }

    // Usually when a video is playing a user needs to press back button two or three times to end the activity, here it fixes that weird bug.
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}