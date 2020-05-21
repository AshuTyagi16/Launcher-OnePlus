package com.sasuke.launcheroneplus.ui.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.util.hide
import com.sasuke.launcheroneplus.util.show
import kotlinx.android.synthetic.main.activity_wallpaper_preview.*
import javax.inject.Inject

class WallpaperPreviewActivity : BaseActivity() {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gson: Gson

    private lateinit var result: Result

    private var position = 0

    private lateinit var wallpaperManager: WallpaperManager

    companion object {
        private const val EXTRA_RESULT = "EXTRA_RESULT"
        private const val EXTRA_POSITION = "EXTRA_POSITION"

        fun newIntent(context: Context, result: String, position: Int): Intent {
            return Intent(context, WallpaperPreviewActivity::class.java).apply {
                putExtra(EXTRA_RESULT, result)
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
        result = gson.fromJson(intent.getStringExtra(EXTRA_RESULT), Result::class.java)
        position = intent.getIntExtra(EXTRA_POSITION, 0)
    }

    private fun loadWallpaper() {
        glide
            .load(result.urls.regular)
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

        btnSetWallpaper.setOnClickListener {
            showToast(getString(R.string.setting_wallpaper), Toast.LENGTH_LONG)
            setCustomWallpaper()
            btnSetWallpaper.hide()
        }

        ivWallpaperPreview.setOnClickListener {
            TransitionManager.beginDelayedTransition(clParent, Fade())

            if (tvPhotoBy.isVisible)
                tvPhotoBy.hide()
            else
                tvPhotoBy.show()

            if (btnSetWallpaper.isVisible)
                btnSetWallpaper.hide()
            else
                btnSetWallpaper.show()
        }

        val text = "${getString(R.string.photo_by)} ${result.user.name} on ${getString(R.string.unsplash)}"
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View) {
                openBrowser(result.links.html)
            }

        }
        spannableString.setSpan(clickableSpan, 9, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvPhotoBy.text = spannableString
        tvPhotoBy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initWallpaperManager() {
        wallpaperManager = WallpaperManager.getInstance(this)
    }

    private fun setCustomWallpaper() {
        glide
            .asBitmap()
            .load(result.urls.regular)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    wallpaperManager.setBitmap(resource)
                    showToast(getString(R.string.wallpaper_updated_successfully))
                    btnSetWallpaper.show()
                }

            })

    }


}