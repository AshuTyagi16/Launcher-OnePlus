package com.sasuke.launcheroneplus.ui.settings.app_drawer

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.SettingPreference
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.color_picker.ColorPickerFragment
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.SharedPreferencesSettingsLiveData
import kotlinx.android.synthetic.main.activity_app_drawer_setting.*
import kotlinx.android.synthetic.main.activity_wallpaper_settings.toolbar
import javax.inject.Inject

class AppDrawerActivity : BaseActivity(), ColorPickerFragment.OnClickListeners {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPreferencesSettingsLiveData: SharedPreferencesSettingsLiveData

    private lateinit var appDrawerActivityViewModel: AppDrawerActivityViewModel

    private lateinit var colorPickerFragment: ColorPickerFragment

    private var isFastScrollEnabled = false

    private var isSpinnerFirstCall = true

    private var isPrimaryColorPicker = false

    private var isBackgroundColorPicker = false

    companion object {
        fun newIntent(context: Context) = Intent(context, AppDrawerActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_drawer_setting)
        inject()
        setupToolbar()
        setupSpinner()
        observeLiveData()
        setupListeners()
        initColorPicker()
    }

    private fun inject() {
        appDrawerActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(AppDrawerActivityViewModel::class.java)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.app_drawer_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAppDrawerStyle.adapter = adapter
        }

    }

    private fun observeLiveData() {
        sharedPreferencesSettingsLiveData.observe(this, Observer {
            it?.let {
                isFastScrollEnabled = it.isFastScrollEnabled
                updateUI(it)
            }
        })
    }

    private fun setupListeners() {
        clScrollAccentColor.setOnClickListener {
            isBackgroundColorPicker = false
            isPrimaryColorPicker = true
            openColorPicker()
        }

        clBackgroundColor.setOnClickListener {
            isPrimaryColorPicker = false
            isBackgroundColorPicker = true
            openColorPicker()
        }

        clFastScroller.setOnClickListener {
            isFastScrollEnabled = !isFastScrollEnabled
            cbFastScroller.isChecked = isFastScrollEnabled
            appDrawerActivityViewModel.setFastScrollState(isFastScrollEnabled)
        }

        clAppDrawerStyle.setOnClickListener {
            spinnerAppDrawerStyle.performClick()
        }

        spinnerAppDrawerStyle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                parent?.let {
                    (it.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    if (!isSpinnerFirstCall) {
                        appDrawerActivityViewModel.setDrawerStyle(spinnerAppDrawerStyle.selectedItem.toString())
                    }
                    isSpinnerFirstCall = false
                }
            }
        }
        seekbarBackgroundTransparency.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar, progress: Int, p2: Boolean) {
                tvBackgroundTransparency.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekbar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekbar: SeekBar) {
                appDrawerActivityViewModel.setBackgroundTransparency(seekbar.progress)
            }
        })
    }

    private fun initColorPicker() {
        colorPickerFragment = ColorPickerFragment.newInstance()
        colorPickerFragment.setOnClickListeners(this)
    }

    private fun openColorPicker() {
        colorPickerFragment.show(supportFragmentManager, colorPickerFragment.tag)
    }

    private fun updateUI(settingPreference: SettingPreference) {
        cbFastScroller.isChecked = isFastScrollEnabled
        spinnerAppDrawerStyle.setSelection(settingPreference.drawerStyle)
        val color = settingPreference.primaryColor
        if (color != 0) {
            AppCompatResources.getDrawable(this, R.drawable.scroll_accent_drawable)?.let {
                val wrappedDrawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    color
                )
                ivIconScrollAccent.setImageDrawable(wrappedDrawable)
            }
            cbFastScroller.buttonTintList = ColorStateList.valueOf(color)
            tvHeaderScroll.setTextColor(color)
            tvHeaderLayout.setTextColor(color)
            seekbarBackgroundTransparency.progress = 100 - settingPreference.backgroundColorAlpha
            seekbarBackgroundTransparency.progressDrawable.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color,
                    BlendModeCompat.SRC_ATOP
                )
            seekbarBackgroundTransparency.thumb.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color,
                    BlendModeCompat.SRC_ATOP
                )
        }
        AppCompatResources.getDrawable(this, R.drawable.scroll_accent_drawable)?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(
                wrappedDrawable,
                settingPreference.backgroundColor
            )
            ivBackgroundColor.setImageDrawable(wrappedDrawable)
        }
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
        if (isPrimaryColorPicker)
            appDrawerActivityViewModel.setPrimaryColor(color)
        if (isBackgroundColorPicker)
            appDrawerActivityViewModel.setBackgroundColor(color)
    }
}