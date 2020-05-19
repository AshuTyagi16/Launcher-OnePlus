package com.sasuke.launcheroneplus.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.jaeger.library.StatusBarUtil
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
}