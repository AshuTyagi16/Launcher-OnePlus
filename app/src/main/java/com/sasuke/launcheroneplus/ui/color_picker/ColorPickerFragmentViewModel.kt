package com.sasuke.launcheroneplus.ui.color_picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sasuke.launcheroneplus.data.model.DefaultColor
import com.sasuke.launcheroneplus.util.ColorUtils
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.SharedPreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ColorPickerFragmentViewModel @Inject constructor(
    private val colorUtils: ColorUtils,
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val gson: Gson
) :
    ViewModel() {

    private val _defaultColorsLiveData = MutableLiveData<MutableList<DefaultColor>>()
    val defaultColorsLiveData: LiveData<MutableList<DefaultColor>>
        get() = _defaultColorsLiveData

    fun getDefaultColorList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _defaultColorsLiveData.postValue(colorUtils.getDefaultColorHexList())
            }
        }
    }

    fun setPrimaryColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.primaryColor = color
                    sharedPreferenceUtil.putString(Constants.Settings.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

}