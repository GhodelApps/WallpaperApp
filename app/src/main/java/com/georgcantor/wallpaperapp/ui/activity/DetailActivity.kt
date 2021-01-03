package com.georgcantor.wallpaperapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityDetailBinding
import com.georgcantor.wallpaperapp.model.response.CommonPic
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.loadImage
import com.georgcantor.wallpaperapp.util.viewBinding

class DetailActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityDetailBinding::inflate)
    private lateinit var zoomyBuilder: Zoomy.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pic = intent.getParcelableExtra(PIC_EXTRA) as CommonPic?

        loadImage(pic?.imageURL, binding.image, binding.progressBar, R.color.black)

        zoomyBuilder = Zoomy.Builder(this).target(binding.image)
        zoomyBuilder.register()
    }
}