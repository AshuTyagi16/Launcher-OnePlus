package com.sasuke.launcheroneplus

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sasuke.launcheroneplus.di.component.DaggerLauncherAppComponent
import com.sasuke.launcheroneplus.di.component.LauncherAppComponent
import com.sasuke.launcheroneplus.receiver.AppChangeReceiver
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
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
        initComponent()
        initTimber()
        initFont()
        initApps()
        register()
    }

    companion object {
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

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}