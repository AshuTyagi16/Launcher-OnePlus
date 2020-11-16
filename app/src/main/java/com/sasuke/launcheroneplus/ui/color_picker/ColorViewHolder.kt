package com.sasuke.launcheroneplus.ui.color_picker

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.DefaultColor
import com.sasuke.launcheroneplus.util.hide
import com.sasuke.launcheroneplus.util.show
import com.sasuke.launcheroneplus.util.updateTint
import kotlinx.android.synthetic.main.cell_default_color.view.*

class ColorViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    fun setColor(color: DefaultColor) {
        AppCompatResources.getDrawable(itemView.context, R.drawable.scroll_accent_drawable)
            ?.let { drawable ->
                drawable.updateTint(color.colorHex)
                itemView.ivColor.background = drawable
            }

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemClick(adapterPosition, color.colorHex)
        }
    }

    fun toggle(flag: Boolean) {
        if (flag)
            itemView.ivSelected.show()
        else
            itemView.ivSelected.hide()
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, color: Int)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}