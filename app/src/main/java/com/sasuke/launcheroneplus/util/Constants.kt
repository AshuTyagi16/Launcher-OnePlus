package com.sasuke.launcheroneplus.util

object Constants {

    const val APP_LIST_SPAN_COUNT = 5
    const val APP_LIST_HORIZONTAL_SPACING = 10
    const val APP_LIST_VERTICAL_SPACING = 60

    const val EXTRA_API_VERSION_HEADER = "Accept-Version"
    const val EXTRA_API_VERSION = "v1"

    const val EXTRA_API_KEY = "S3wNzdfiMMhrwSDEJFIp-fPrzORJMXLBSyz_OIk5khE"

    const val PAGE_SIZE = 15

    object Settings {
        const val HOME_SCREEN = 1
        const val APP_DRAWER = 2
        const val FOLDERS = 3
        const val LOOK_FEEL = 4
        const val NIGHT_MODE = 5
        const val GESTURE_INPUT = 6
        const val NOTIFICATION_BADGE = 7
        const val DEFAULT_LAUNCHER = 8
        const val SAY_THANKS = 9
        const val ABOUT = 10
        const val CONTACT_SUPPORT = 11
        const val PREFERENCES = "SETTINGS_PREFERENCES"
        const val IS_PREFERENCES_SET = "IS_PREFERENCES_SET"
    }

    object Drawer {
        const val STYLE_VERTICAL_INDICATOR = 0
        const val STYLE_LIST_INDICATOR = 1
        const val STYLE_HORIZONTAL_INDICATOR = 2

        const val VERTICAL = "Vertical"
        const val LIST = "List"
        const val HORIZONTAL = "Horizontal"
    }

    const val MOVE_THRESHOLD_HORIZONTAL = 10f
    const val MOVE_THRESHOLD_VERTICAL = 8f

}