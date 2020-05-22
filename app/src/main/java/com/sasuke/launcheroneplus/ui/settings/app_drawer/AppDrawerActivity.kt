package com.sasuke.launcheroneplus.ui.settings.app_drawer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.event.PrimaryColorChangedEvent
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.color_picker.ColorPickerFragment
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.activity_app_drawer_setting.*
import kotlinx.android.synthetic.main.activity_wallpaper_settings.toolbar
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AppDrawerActivity : BaseActivity(), ColorPickerFragment.OnClickListeners {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var colorPickerFragment: ColorPickerFragment

    companion object {
        fun newIntent(context: Context) = Intent(context, AppDrawerActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_drawer_setting)
        setupToolbar()
        setupListeners()
        initCells(LauncherApp.color)
        initColorPicker()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupListeners() {
        clScrollAccentColor.setOnClickListener {
            openColorPicker()
        }
    }

    private fun initCells(color: Int) {
        if (color != 0)
            AppCompatResources.getDrawable(this, R.drawable.scroll_accent_drawable)?.let {
                val wrappedDrawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    color
                )
                glide.load(wrappedDrawable)
                    .into(ivIconScrollAccent)
            }
    }

    private fun initColorPicker() {
        colorPickerFragment = ColorPickerFragment.newInstance()
        colorPickerFragment.setOnClickListeners(this)
    }

    private fun openColorPicker() {
        colorPickerFragment.show(supportFragmentManager, colorPickerFragment.tag)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        if (::colorPickerFragment.isInitialized) {
            colorPickerFragment.setOnClickListeners(null)
            if (colorPickerFragment.isVisible)
                colorPickerFragment.dismiss()
        }
        super.onDestroy()
    }

    override fun onItemClick(color: Int) {
        initCells(color)
    }
}