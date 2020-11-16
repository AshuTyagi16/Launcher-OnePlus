package com.sasuke.launcheroneplus.data.model

import com.sasuke.launcheroneplus.util.Constants

data class SettingPreference(
    var primaryColor: Int,
    var isFastScrollEnabled: Boolean = true,
    var drawerStyle: Constants.DrawerStyle = Constants.DrawerStyle.VERTICAL,
    var backgroundColor: Int,
    var backgroundColorAlpha: Int
)