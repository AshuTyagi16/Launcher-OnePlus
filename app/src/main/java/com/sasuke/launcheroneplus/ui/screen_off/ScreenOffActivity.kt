package com.sasuke.launcheroneplus.ui.screen_off

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.ui.base.BaseActivity

class ScreenOffActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, ScreenOffActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTimeout()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_off)
    }

    private fun setTimeout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val params = window.attributes
        params.screenBrightness = 0f
        window.attributes = params
    }
}