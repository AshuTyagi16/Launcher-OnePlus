package com.sasuke.launcheroneplus.util

import android.content.Context
import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Setting
import com.sasuke.launcheroneplus.data.model.SettingsType

class SettingUtils(private val context: Context) {

    @WorkerThread
    fun getSettings(): List<Setting> {
        return ArrayList<Setting>().apply {
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.home_screen),
                    context.getString(R.string.home_screen_description),
                    SettingsType.HOME_SCREEN
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.app_drawer),
                    context.getString(R.string.app_drawer_description),
                    SettingsType.APP_DRAWER,
                    true
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.folders),
                    context.getString(R.string.folders_description),
                    SettingsType.FOLDERS
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.look_n_feel),
                    context.getString(R.string.look_n_feel_description),
                    SettingsType.LOOK_FEEL
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.night_mode),
                    context.getString(R.string.night_mode_description),
                    SettingsType.NIGHT_MODE
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.gesture_n_inputs),
                    context.getString(R.string.gesture_n_inputs_description),
                    SettingsType.GESTURE_INPUT
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.notification_badges),
                    context.getString(R.string.notification_badges_description),
                    SettingsType.NOTIFICATION_BADGE
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.select_default_launcher),
                    context.getString(R.string.select_default_launcher_description),
                    SettingsType.DEFAULT_LAUNCHER,
                    true
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.say_thanks),
                    context.getString(R.string.say_thanks_description),
                    SettingsType.SAY_THANKS
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.about),
                    context.getString(R.string.about_description),
                    SettingsType.ABOUT
                )
            )
            add(
                Setting(
                    R.drawable.ic_settings_white,
                    context.getString(R.string.contact_support),
                    context.getString(R.string.contact_support_description),
                    SettingsType.CONTACT_SUPPORT
                )
            )
        }
    }
}