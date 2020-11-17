package com.sasuke.launcheroneplus.util

import android.view.View
import com.sasuke.launcheroneplus.data.model.App

interface OnCustomEventListeners {
    fun onItemClick(position: Int, parent: View, appInfo: App)
    fun onItemLongClick(position: Int, parent: View, appInfo: App)
    fun onDragStart(position: Int, parent: View, appInfo: App)
    fun onEventCancel(position: Int, appInfo: App)
}