package com.meet.damnsmallvideoplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meet.damnsmallvideoplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSource.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/meetx0r/dsvp")).also {
                startActivity(it)
            }
        }

        binding.ivGPL.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://choosealicense.com/licenses/gpl-3.0/")
            ).also {
                startActivity(it)
            }
        }
    }
}