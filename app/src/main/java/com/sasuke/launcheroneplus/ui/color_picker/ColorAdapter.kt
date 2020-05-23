package com.sasuke.launcheroneplus.ui.color_picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.DefaultColor

class ColorAdapter : RecyclerView.Adapter<ColorViewHolder>(),
    ColorViewHolder.OnClickListeners {

    private lateinit var colors: List<DefaultColor>
    private lateinit var onClickListeners: OnClickListeners

    private var position = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_default_color, parent, false)
        val holder = ColorViewHolder(view)
        holder.setOnClickListeners(this)
        return holder
    }

    override fun getItemCount(): Int {
        return if (::colors.isInitialized) colors.size else 0
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        if (::colors.isInitialized) {
            holder.setColor(colors[position])
        }
    }

    override fun onBindViewHolder(
        holder: ColorViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (::colors.isInitialized) {
            if (payloads.isNotEmpty()) {
                val flag = payloads[0] as Boolean
                holder.toggle(flag)
            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, color: Int)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }

    override fun onItemClick(position: Int, color: Int) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemClick(position, color)
    }

    fun setColors(list: List<DefaultColor>) {
        this.colors = list
        notifyDataSetChanged()
    }

    fun toggle(position: Int): Boolean {
        if (this.position != -1 && this.position != position) {
            colors[this.position].isSelected = false
            notifyItemChanged(this.position, false)
            this.position = position
        } else {
            this.position = position
        }
        var flag = colors[position].isSelected
        flag = !flag
        colors[position].isSelected = flag
        notifyItemChanged(position, flag)
        return flag
    }
}