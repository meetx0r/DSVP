package com.meet.damnsmallvideoplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.meet.damnsmallvideoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val getAction = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                videoActivityLauncher(uri)
            } else {
                Toast.makeText(this, "Select a video file.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSelect.setOnClickListener {
            getAction.launch("video/*")
        }
    }

    private fun videoActivityLauncher(path: Uri?) {
        Intent(this, VideoActivity::class.java).also {
            it.putExtra("URI", path.toString())
            startActivity(it)
        }
    }
}
