package com.sasuke.launcheroneplus.di.module.fragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.scope.PerFragmentScope
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.color_picker.ColorAdapter
import com.sasuke.launcheroneplus.ui.color_picker.ColorPickerFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class ColorPickerFragmentModule {

    companion object {

        @Provides
        @PerFragmentScope
        fun adapter(): ColorAdapter {
            return ColorAdapter()
        }

        @Provides
        @PerFragmentScope
        fun layoutManager(context: Context): GridLayoutManager {
            return GridLayoutManager(context, 4)
        }

        @Provides
        @PerFragmentScope
        fun itemDecoration(): ItemDecorator {
            return ItemDecorator(50, 30)
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(ColorPickerFragmentViewModel::class)
    abstract fun bindColorPickerFragmentViewModel(colorPickerFragmentViewModel: ColorPickerFragmentViewModel): ViewModel
}