package com.sasuke.launcheroneplus.util

import android.os.Build

object GeneralUtils {

    val ATLEAST_MARSHMALLOW = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val ATLEAST_LOLLIPOP_MR1 =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
}