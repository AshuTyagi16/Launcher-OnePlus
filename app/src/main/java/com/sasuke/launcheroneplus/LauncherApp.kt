package com.sasuke.launcheroneplus

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.sasuke.launcheroneplus.data.model.SettingPreference
import com.sasuke.launcheroneplus.di.component.DaggerLauncherAppComponent
import com.sasuke.launcheroneplus.di.component.LauncherAppComponent
import com.sasuke.launcheroneplus.receiver.AppChangeReceiver
import com.sasuke.launcheroneplus.util.Constants
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.inflationx.viewpump.ViewPump
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LauncherApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private lateinit var component: LauncherAppComponent

    override fun onCreate() {
        super.onCreate()
        color = ContextCompat.getColor(this, R.color.search_bar)
        initComponent()
        initTimber()
        initFont()
        initApps()
        register()
        setDefaultPreferences()
        observeLiveData()
    }

    companion object {
        var color = 0
        fun get(context: Context): LauncherApp {
            return context as LauncherApp
        }
    }

    fun getComponent(): LauncherAppComponent {
        return component
    }

    private fun initFont() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(component.calligraphyInterceptor())
                .build()
        )
    }

    private fun initComponent() {
        component = DaggerLauncherAppComponent.factory().create(applicationContext)
        component.inject(this)
    }

    private fun initApps() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                component.getAllListUtils().saveAppsInDB()
            }
        }
    }

    private fun initTimber() {
        Timber.plant(component.timberTree())
    }

    private fun register() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(AppChangeReceiver(), intentFilter)
    }

    private fun setDefaultPreferences() {
        val sharedPrefUtil = component.getSharedPreferenceUtil()
        if (!sharedPrefUtil.getBoolean(Constants.Settings.IS_PREFERENCES_SET, false)) {
            val defaultSettings =
                SettingPreference(
                    ContextCompat.getColor(this, R.color.search_bar),
                    true,
                    Constants.Drawer.STYLE_VERTICAL_INDICATOR,
                    ContextCompat.getColor(this, R.color.black_transparent),
                    20
                )
            sharedPrefUtil.putStringSync(
                Constants.Settings.PREFERENCES,
                component.gson().toJson(defaultSettings)
            )
            sharedPrefUtil.putBoolean(Constants.Settings.IS_PREFERENCES_SET, true)
        }
    }

    private fun observeLiveData() {
        component.getSharedPreferencesSettingsLiveData().observeForever(Observer {
            it?.let {
                color = it.primaryColor
            }
        })
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}