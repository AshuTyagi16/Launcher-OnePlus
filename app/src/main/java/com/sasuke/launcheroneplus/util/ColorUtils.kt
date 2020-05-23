package com.sasuke.launcheroneplus.util

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.DefaultColor

class ColorUtils(private val context: Context) {

    @WorkerThread
    suspend fun getDefaultColorHexList(): MutableList<DefaultColor> {
        val list = ArrayList<DefaultColor>()

        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_one)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_two)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_three)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_four)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_five)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_six)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_seven)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_eight)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_nine)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_ten)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_eleven)))
        list.add(DefaultColor(ContextCompat.getColor(context, R.color.default_search_bar_twelve)))
        return list
    }
}