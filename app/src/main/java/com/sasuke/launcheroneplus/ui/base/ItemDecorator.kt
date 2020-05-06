package com.sasuke.launcheroneplus.ui.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sasuke.launcheroneplus.util.Constants

class ItemDecorator(private val horizontalSpace: Int, private val verticalSpace: Int) :
    RecyclerView.ItemDecoration() {

    companion object {
        private const val EDGE_SPACING_FACTOR = 2
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = horizontalSpace
        outRect.right = horizontalSpace

        if (parent.getChildLayoutPosition(view) == state.itemCount - 1)
            outRect.bottom = EDGE_SPACING_FACTOR * verticalSpace
        else
            outRect.bottom = verticalSpace

        if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) < Constants.APP_LIST_SPAN_COUNT)
            outRect.top = EDGE_SPACING_FACTOR * verticalSpace
        else
            outRect.top = verticalSpace
    }
}