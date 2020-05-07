package com.sasuke.launcheroneplus

import android.app.Application
import com.sasuke.launcheroneplus.di.component.DaggerLauncherAppComponent
import com.sasuke.launcheroneplus.di.component.LauncherAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import javax.inject.Inject

class LauncherApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private lateinit var component: LauncherAppComponent

    override fun onCreate() {
        super.onCreate()
        initComponent()
        initFont()
    }

    private fun initFont() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Lato.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }

    private fun initComponent() {
        component = DaggerLauncherAppComponent.factory().create(applicationContext)
        component.inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}