package com.sasuke.launcheroneplus.ui.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wallpaper_preview.*
import javax.inject.Inject

class WallpaperPreviewActivity : BaseActivity() {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var url: String

    private lateinit var wallpaperManager: WallpaperManager

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"

        fun newIntent(context: Context, url: String): Intent {
            return Intent(context, WallpaperPreviewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_preview)
        ViewCompat.setTransitionName(ivWallpaperPreview, getString(R.string.wallpaper))
        getArguments()
        initWallpaperManager()
        loadWallpaper()
    }

    private fun getArguments() {
        url = intent.getStringExtra(EXTRA_URL)!!
    }

    private fun loadWallpaper() {
        glide
            .asBitmap()
            .load(url)
            .into(ivWallpaperPreview)

        ivWallpaperPreview.setOnLongClickListener {
            showToast(getString(R.string.setting_wallpaper), Toast.LENGTH_LONG)
            setCustomWallpaper()
            return@setOnLongClickListener true
        }
    }

    private fun initWallpaperManager() {
        wallpaperManager = WallpaperManager.getInstance(this)
    }

    private fun setCustomWallpaper() {
        glide
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    wallpaperManager.setBitmap(resource)
                    showToast(getString(R.string.wallpaper_updated_successfully))
                }

            })

    }


}