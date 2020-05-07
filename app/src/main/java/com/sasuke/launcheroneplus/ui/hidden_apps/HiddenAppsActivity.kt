package com.sasuke.launcheroneplus.ui.hidden_apps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.ui.base.BaseActivity

class HiddenAppsActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, HiddenAppsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_apps)
    }
}