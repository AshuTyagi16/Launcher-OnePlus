package com.sasuke.launcheroneplus.ui.hidden_apps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.hidden_apps.app_selector.AppSelectionActivity
import kotlinx.android.synthetic.main.activity_hidden_apps.*

class HiddenAppsActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, HiddenAppsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_apps)
        setupListeners()
    }

    private fun setupListeners() {
        btnAddAppToHide.setOnClickListener {
            startActivity(AppSelectionActivity.newIntent(this))
        }
    }
}