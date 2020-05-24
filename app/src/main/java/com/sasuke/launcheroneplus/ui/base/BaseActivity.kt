package com.sasuke.launcheroneplus.ui.base

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.jaeger.library.StatusBarUtil
import com.sasuke.launcheroneplus.R
import dagger.android.support.DaggerAppCompatActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.lang.Exception

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
}