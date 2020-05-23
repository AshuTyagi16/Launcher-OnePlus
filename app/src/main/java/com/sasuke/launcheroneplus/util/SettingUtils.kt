package com.sasuke.launcheroneplus.util

import android.content.Context
import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Setting

class SettingUtils(private val context: Context) {

    @WorkerThread
    suspend fun getSettings(): List<Setting> {
        val list = ArrayList<Setting>()
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.home_screen),
                context.getString(R.string.home_screen_description),
                Constants.Settings.HOME_SCREEN
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.app_drawer),
                context.getString(R.string.app_drawer_description),
                Constants.Settings.APP_DRAWER
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.folders),
                context.getString(R.string.folders_description),
                Constants.Settings.FOLDERS
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.look_n_feel),
                context.getString(R.string.look_n_feel_description),
                Constants.Settings.LOOK_FEEL
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.night_mode),
                context.getString(R.string.night_mode_description),
                Constants.Settings.NIGHT_MODE
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.gesture_n_inputs),
                context.getString(R.string.gesture_n_inputs_description),
                Constants.Settings.GESTURE_INPUT
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.notification_badges),
                context.getString(R.string.notification_badges_description),
                Constants.Settings.NOTIFICATION_BADGE
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.select_default_launcher),
                context.getString(R.string.select_default_launcher_description),
                Constants.Settings.DEFAULT_LAUNCHER
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.say_thanks),
                context.getString(R.string.say_thanks_description),
                Constants.Settings.SAY_THANKS
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.about),
                context.getString(R.string.about_description),
                Constants.Settings.ABOUT
            )
        )
        list.add(
            Setting(
                R.drawable.ic_settings_white,
                context.getString(R.string.contact_support),
                context.getString(R.string.contact_support_description),
                Constants.Settings.CONTACT_SUPPORT
            )
        )
        return list
    }
}