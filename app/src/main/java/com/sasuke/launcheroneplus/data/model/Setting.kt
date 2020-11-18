package com.sasuke.launcheroneplus.data.model

data class Setting(
    val icon: Int,
    val title: String,
    val description: String,
    val type: SettingsType,
    val shouldShow: Boolean = false
)

enum class SettingsType {
    HOME_SCREEN,
    APP_DRAWER,
    FOLDERS,
    LOOK_FEEL,
    NIGHT_MODE,
    GESTURE_INPUT,
    NOTIFICATION_BADGE,
    DEFAULT_LAUNCHER,
    SAY_THANKS,
    ABOUT,
    CONTACT_SUPPORT
}

enum class DrawerStyle {
    VERTICAL, LIST
}