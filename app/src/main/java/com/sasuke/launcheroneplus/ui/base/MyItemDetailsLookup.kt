package com.sasuke.launcheroneplus.ui.base

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.sasuke.launcheroneplus.ui.launcher.apps.AppViewHolder

class MyItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
//            return (recyclerView.getChildViewHolder(view) as AppViewHolder)
//                .getItemDetails()
        }
        return null
    }
}
