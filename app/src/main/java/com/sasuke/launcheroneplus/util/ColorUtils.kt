package com.sasuke.launcheroneplus.util

import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.data.model.DefaultColor

object ColorUtils {

    @WorkerThread
    suspend fun getDefaultColorHexList(): MutableList<DefaultColor> {
        val list = ArrayList<DefaultColor>()

        list.add(DefaultColor("#303960"))
        list.add(DefaultColor("#ea9a96"))
        list.add(DefaultColor("#2fc4b2"))
        list.add(DefaultColor("#00adb5"))
        list.add(DefaultColor("#f08a5d"))
        list.add(DefaultColor("#b83b5e"))
        list.add(DefaultColor("#95e1d3"))
        list.add(DefaultColor("#f5f5f5"))
        list.add(DefaultColor("#3f72af"))
        list.add(DefaultColor("#e84545"))
        list.add(DefaultColor("#a2d5f2"))
        list.add(DefaultColor("#769fcd"))
        return list
    }
}