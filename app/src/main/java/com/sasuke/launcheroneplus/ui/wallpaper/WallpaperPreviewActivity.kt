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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wallpaper_preview.*
import javax.inject.Inject

class WallpaperPreviewActivity : BaseActivity() {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var url: String

    private var position = 0

    private lateinit var wallpaperManager: WallpaperManager

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_POSITION = "EXTRA_POSITION"

        fun newIntent(context: Context, url: String, position: Int): Intent {
            return Intent(context, WallpaperPreviewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_POSITION, position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_preview)
        supportPostponeEnterTransition()
        getArguments()
        ViewCompat.setTransitionName(ivWallpaperPreview, position.toString())
        initWallpaperManager()
        loadWallpaper()
    }

    private fun getArguments() {
        url = intent.getStringExtra(EXTRA_URL)!!
        position = intent.getIntExtra(EXTRA_POSITION, 0)
    }

    private fun loadWallpaper() {
        glide
            .load(url)
            .dontAnimate()
            .dontTransform()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }
            })
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