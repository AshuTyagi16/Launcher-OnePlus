package com.sasuke.launcheroneplus.ui.base

import android.graphics.Canvas
import android.graphics.Point
import android.view.View
import android.widget.ImageView

class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = (v as ImageView).drawable

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to half the width of the original View
        val width: Int = (view.width)

        // Sets the height of the shadow to half the height of the original View
        val height: Int = (view.height)

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height)

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set((width / 2), (height * 1.5).toInt())
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}