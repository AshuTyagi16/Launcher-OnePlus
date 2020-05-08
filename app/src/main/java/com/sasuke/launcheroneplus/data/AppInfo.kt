package com.sasuke.launcheroneplus.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val icon: Drawable,
    val packageName: String,
    val label: String,
    var isSelected: Boolean = false
)