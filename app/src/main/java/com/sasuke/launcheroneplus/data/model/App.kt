package com.sasuke.launcheroneplus.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "apps")
data class App(
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    val packageName: String,
    val label: String,
    var isSelected: Boolean = false,
    @ColumnInfo(name = "isHidden", index = true)
    var isHidden: Boolean = false
): Parcelable