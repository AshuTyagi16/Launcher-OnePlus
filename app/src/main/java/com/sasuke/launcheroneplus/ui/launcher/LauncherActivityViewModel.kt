package com.sasuke.launcheroneplus.ui.launcher

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sasuke.launcheroneplus.data.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LauncherActivityViewModel @Inject constructor(private val context: Context) : ViewModel() {

    private val _appList = MutableLiveData<MutableList<AppInfo>>()
    val appList: LiveData<MutableList<AppInfo>>
        get() = _appList

    private lateinit var list: MutableList<AppInfo>
    private lateinit var listFiltered: MutableList<AppInfo>
    private val packageManager = context.packageManager

    fun getAppsList() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                context.packageManager.queryIntentActivities(mainIntent, 0).let {
                    val appInoList: MutableList<AppInfo> = ArrayList(it.size)
                    it.forEach { resolveInfo ->
                        val packageInfo = AppInfo(
                            resolveInfo.loadIcon(packageManager),
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.loadLabel(packageManager).toString()
                        )
                        appInoList.add(packageInfo)
                    }
                    appInoList.sortBy {
                        it.label.toLowerCase(Locale.getDefault())
                    }
                    list = appInoList
                    _appList.postValue(appInoList)
                }
            }
        }
    }

    fun getDefaultList() {
        if (::list.isInitialized) {
            _appList.postValue(list)
        }
    }

    fun filterApps(query: String) {
        if (::list.isInitialized && query.isNotEmpty()) {
            val filtered = ArrayList<AppInfo>()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    list.forEach {
                        if (matches(
                                it.label.toLowerCase(Locale.getDefault()), query.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            filtered.add(it)
                        }
                    }
                    listFiltered = filtered
                    _appList.postValue(listFiltered)
                }
            }
        }
    }

    private fun matches(
        haystack: String,
        needle: String
    ): Boolean {
        val queryLength = needle.length
        val titleLength = haystack.length
        if (titleLength < queryLength || queryLength <= 0) {
            return false
        }
        var ni = 0
        var hi = 0
        while (hi < titleLength) {
            if (haystack[hi] == needle[ni]) {
                ni++
                if (ni == queryLength) return true
            }
            hi++
        }
        return false
    }

}