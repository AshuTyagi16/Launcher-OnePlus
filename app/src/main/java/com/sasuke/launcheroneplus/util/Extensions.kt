package com.sasuke.launcheroneplus.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

fun String.toLowerCased(): String = this.toLowerCase(Locale.getDefault())


inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

fun Drawable.updateTint(color: Int) {
    DrawableCompat.wrap(this)?.let {
        DrawableCompat.setTint(it, color)
    }
}

fun Context.getIconFolderPath(label: String): String {
    return "${applicationContext.getExternalFilesDir(Constants.ICON_FOLDER_NAME)}${File.separator}${label.removeSpecialChars()}"
}

fun String.removeSpecialChars(): String {
    return replace(
        "[\\W]|_".toRegex(),
        ""
    )
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Int.alphaPercentage(): Int {
    return ((this.toDouble() / 100.0) * 255.0).toInt()
}