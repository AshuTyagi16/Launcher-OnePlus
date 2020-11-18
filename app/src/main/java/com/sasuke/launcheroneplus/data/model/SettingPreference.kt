package com.sasuke.launcheroneplus.data.model

data class SettingPreference(
    var primaryColor: Int,
    var isFastScrollEnabled: Boolean = true,
    var drawerStyle: DrawerStyle = DrawerStyle.VERTICAL,
    var backgroundColor: Int,
    var backgroundColorAlpha: Int
)