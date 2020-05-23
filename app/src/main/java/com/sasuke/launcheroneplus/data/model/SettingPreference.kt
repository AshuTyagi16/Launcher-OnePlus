package com.sasuke.launcheroneplus.data.model

import com.sasuke.launcheroneplus.util.Constants

data class SettingPreference(
    var primaryColor: Int,
    var isFastScrollEnabled: Boolean = true,
    var drawerStyle: Int = Constants.Drawer.STYLE_VERTICAL_INDICATOR
)