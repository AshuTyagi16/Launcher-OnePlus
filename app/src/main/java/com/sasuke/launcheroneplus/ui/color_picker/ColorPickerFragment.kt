package com.sasuke.launcheroneplus.ui.color_picker

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.ui.base.SpaceItemDecoration
import com.sasuke.launcheroneplus.ui.base.RoundedBottomSheetDialogFragment
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.transitionseverywhere.Rotate
import kotlinx.android.synthetic.main.fragment_color_picker.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ColorPickerFragment : RoundedBottomSheetDialogFragment(), ColorAdapter.OnClickListeners {

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var colorAdapter: ColorAdapter

    @Inject
    lateinit var spaceItemDecoration: SpaceItemDecoration

    private var isCustom = AtomicBoolean(true)

    private lateinit var colorPickerFragmentViewModel: ColorPickerFragmentViewModel

    private var onClickListeners: OnClickListeners? = null

    private var color = 0

    private var lastPosition = -1

    override fun getLayoutResId(): Int {
        return R.layout.fragment_color_picker
    }

    companion object {
        fun newInstance(): ColorPickerFragment {
            return ColorPickerFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inject()
        initColorPicker()
        setupRecyclerView()
        setupListeners()
        getDefaultColorList()
        observeLiveData()
    }

    override fun onStart() {
        super.onStart()
        configurePeekHeight()
    }

    override fun onDismiss(dialog: DialogInterface) {
        lastPosition = -1
        super.onDismiss(dialog)
    }

    private fun inject() {
        colorPickerFragmentViewModel =
            ViewModelProvider(this, viewModelFactory).get(ColorPickerFragmentViewModel::class.java)
    }

    private fun initColorPicker() {
        colorPicker.preferenceName = getString(R.string.color_preference_name)
        colorPicker.attachAlphaSlider(alphaSlideBar)
        colorPicker.attachBrightnessSlider(brightnessSlide)
        colorPicker.setLifecycleOwner(viewLifecycleOwner)
        colorPicker.setColorListener(object : ColorEnvelopeListener {
            override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
                color = envelope.color
                if (lastPosition != -1) {
                    colorAdapter.toggle(lastPosition)
                    lastPosition = -1
                }
            }
        })
    }

    private fun setupRecyclerView() {
        rvDefaultColors.layoutManager = layoutManager
        rvDefaultColors.adapter = colorAdapter
        rvDefaultColors.addItemDecoration(spaceItemDecoration)
        colorAdapter.setOnClickListeners(this)
    }

    private fun setupListeners() {
        btnCancel.setOnClickListener {
            lastPosition = -1
            dismiss()
        }

        btnDone.setOnClickListener {
            lastPosition = -1
            if (color != 0) {
                onClickListeners?.onItemClick(color)
            }
            dismiss()
        }

        val rotate = Rotate()
        rotate.duration = 500
        btnTogglePallete.setOnClickListener {
            TransitionManager.beginDelayedTransition(clParent, rotate)
            if (isCustom.get()) {
                btnTogglePallete.rotation = 360f
                colorPicker.setHsvPaletteDrawable()
                isCustom.set(false)
            } else {
                btnTogglePallete.rotation = 0f
                colorPicker.setPaletteDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.final_pallete
                    )!!
                )
                isCustom.set(true)
            }
        }
    }

    private fun getDefaultColorList() {
        colorPickerFragmentViewModel.getDefaultColorList()
    }

    private fun observeLiveData() {
        colorPickerFragmentViewModel.defaultColorsLiveData.observe(viewLifecycleOwner, Observer {
            colorAdapter.setColors(it)
        })
    }

    private fun configurePeekHeight() {
        dialog?.let { dialog ->
            val bottomSheet = dialog.findViewById(R.id.clParent) as View
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

            view?.let { view ->
                view.post {
                    val parent = view.parent as View
                    val params = parent.layoutParams as CoordinatorLayout.LayoutParams
                    val behavior = params.behavior
                    val bottomSheetBehavior = behavior as BottomSheetBehavior<*>
                    bottomSheetBehavior.peekHeight = view.measuredHeight + 100
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.isDraggable = false
                }
            }

        }
    }

    override fun onItemClick(position: Int, color: Int) {
        if (colorAdapter.toggle(position)) {
            lastPosition = position
            this.color = color
        } else {
            this.color = 0
            lastPosition = -1
        }
    }

    interface OnClickListeners {
        fun onItemClick(color: Int)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners?) {
        this.onClickListeners = onClickListeners
    }

}