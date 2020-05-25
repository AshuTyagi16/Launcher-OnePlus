package com.sasuke.launcheroneplus.ui.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.jaeger.library.StatusBarUtil
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.util.Constants
import dagger.android.support.DaggerAppCompatActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper

open class BaseActivity : DaggerAppCompatActivity() {

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTranslucent(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        newBase?.let {
            super.attachBaseContext(ViewPumpContextWrapper.wrap(it))
        }
    }

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        Toast.makeText(this, message, length).let {
            it.show()
            toast = it
        }
    }

    @SuppressLint("WrongConstant")
    fun openStatusBar() {
        val sbservice = getSystemService("statusbar")
        val statusbarManager = Class.forName("android.app.StatusBarManager")
        statusbarManager.getMethod("expandNotificationsPanel").invoke(sbservice)
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    fun openBrowser(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            startActivity(intent)

        }
    }

    fun startUninstall(packageName: String) {
        try {
            packageManager.getLaunchIntentForPackage(packageName)?.let {
                it.component?.let {
                    val i = Intent.parseUri(getString(R.string.delete_package_intent), 0)
                        .setData(Uri.fromParts("package", it.packageName, it.className))
                    startActivity(i)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openApp(view: View, appInfo: App) {
        packageManager.getLaunchIntentForPackage(appInfo.packageName)?.let {
            it.sourceBounds = getViewBounds(view)
            startActivity(
                it,
                getActivityLaunchOptions(
                    view,
                    view.findViewById<ImageView>(R.id.ivAppIcon).drawable
                )
            )
        }
    }

    private fun getViewBounds(v: View): Rect? {
        val pos = IntArray(2)
        v.getLocationOnScreen(pos)
        return Rect(
            pos[0],
            pos[1],
            pos[0] + v.width,
            pos[1] + v.height
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getActivityLaunchOptions(v: View, icon: Drawable): Bundle? {
        if (Constants.ATLEAST_MARSHMALLOW) {
            var width = v.measuredWidth
            val bounds = icon.bounds
            val left = (width - bounds.width()) / 2
            val top = v.paddingTop
            width = bounds.width()
            val height = bounds.height()
            return ActivityOptions.makeClipRevealAnimation(v, left, top, width, height).toBundle()
        } else if (Constants.ATLEAST_LOLLIPOP_MR1) {
            return ActivityOptions.makeCustomAnimation(
                this, R.anim.task_open_enter, R.anim.no_anim
            ).toBundle()
        }
        return null
    }

}