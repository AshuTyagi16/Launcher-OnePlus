package com.sasuke.launcheroneplus.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Setting
import com.sasuke.launcheroneplus.data.model.Status
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.settings.app_drawer.AppDrawerActivity
import com.sasuke.launcheroneplus.util.SharedPreferencesSettingsLiveData
import kotlinx.android.synthetic.main.activity_launcher_settings.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class LauncherSettingsActivity : BaseActivity(), LauncherSettingAdapter.OnItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var launcherAdapter: LauncherSettingAdapter

    @Inject
    lateinit var sharedPreferencesSettingsLiveData: SharedPreferencesSettingsLiveData

    private lateinit var launcherSettingsActivityViewModel: LauncherSettingsActivityViewModel

    companion object {
        fun newIntent(context: Context) = Intent(context, LauncherSettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_settings)
        inject()
        setupToolbar()
        setupRecyclerView()
        getSettings()
        observeLiveData()
    }

    private fun inject() {
        launcherSettingsActivityViewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(LauncherSettingsActivityViewModel::class.java)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupRecyclerView() {
        rvSettings.layoutManager = layoutManager
        rvSettings.adapter = launcherAdapter
        launcherAdapter.setOnItemClickListener(this)
    }

    private fun getSettings() {
        launcherSettingsActivityViewModel.getSettings()
    }

    private fun observeLiveData() {
        launcherSettingsActivityViewModel.settingsLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    it.data?.let {
                        launcherAdapter.setSettings(it)
                        runLayoutAnimation()
                    }
                }
                Status.ERROR -> {
                }
            }
        })

        sharedPreferencesSettingsLiveData.observe(this, Observer {
            it?.let {
                launcherAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun runLayoutAnimation() {
        val context = rvSettings.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        rvSettings.layoutAnimation = controller
        rvSettings.adapter?.notifyDataSetChanged()
        rvSettings.scheduleLayoutAnimation()
    }

    override fun onItemClick(setting: Setting) {
        startActivity(AppDrawerActivity.newIntent(this))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}