package com.sasuke.launcheroneplus.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class KeyboardTriggerBehavior(activity: Activity, val minKeyboardHeight: Int = 0) : LiveData<KeyboardTriggerBehavior.Status>() {
    enum class Status {
        OPEN, CLOSED
    }

    val contentView = activity.findViewById<View>(android.R.id.content)

    val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val displayRect = Rect().apply { contentView.getWindowVisibleDisplayFrame(this) }
        val keypadHeight = contentView.rootView.height - displayRect.bottom
        if (keypadHeight > minKeyboardHeight) {
            setDistinctValue(Status.OPEN)
        } else {
            setDistinctValue(Status.CLOSED)
        }
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in Status>) {
        super.observe(owner, observer)
        observersUpdated()
    }

    override fun observeForever(observer: Observer<in Status>) {
        super.observeForever(observer)
        observersUpdated()
    }

    override fun removeObservers(owner: LifecycleOwner) {
        super.removeObservers(owner)
        observersUpdated()
    }

    override fun removeObserver(observer: Observer<in Status>) {
        super.removeObserver(observer)
        observersUpdated()
    }

    private fun setDistinctValue(newValue: KeyboardTriggerBehavior.Status) {
        if (value != newValue) {
            value = newValue
        }
    }

    private fun observersUpdated() {
        if (hasObservers()) {
            contentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        } else {
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
    }
}