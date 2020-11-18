package com.sasuke.launcheroneplus.ui.settings.set_as_default

import android.annotation.TargetApi
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.sasuke.launcheroneplus.BuildConfig
import com.sasuke.launcheroneplus.util.GeneralUtils

abstract class DefaultLauncherHandler(protected val activity: Activity) {

    companion object {
        fun create(activity: Activity): DefaultLauncherHandler {
            if (GeneralUtils.ATLEAST_Q) {
                return DefaultHomeCompatVQ(activity)
            }
            return DefaultHomeCompatVNMr1(activity)
        }
    }

    abstract fun isDefaultHome(): Boolean

    fun requestDefaultHome() {
        activity.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
    }

    class DefaultHomeCompatVNMr1(activity: Activity) : DefaultLauncherHandler(activity) {

        override fun isDefaultHome(): Boolean {
            return BuildConfig.APPLICATION_ID == resolveDefaultHome()
        }

        private fun resolveDefaultHome(): String? {
            val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val info = activity.packageManager
                .resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            return info?.activityInfo?.packageName
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    class DefaultHomeCompatVQ(activity: Activity) : DefaultLauncherHandler(activity) {

        private val roleManager = activity.getSystemService(RoleManager::class.java)!!

        override fun isDefaultHome(): Boolean {
            return roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        }
    }
}