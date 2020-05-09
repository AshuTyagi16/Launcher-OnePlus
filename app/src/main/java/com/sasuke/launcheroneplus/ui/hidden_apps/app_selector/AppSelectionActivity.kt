package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.di.qualifiers.HiddenAppLayoutManager
import com.sasuke.launcheroneplus.di.qualifiers.VisibleAppLayoutManager
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.util.hide
import com.sasuke.launcheroneplus.util.show
import kotlinx.android.synthetic.main.activity_app_selection.*
import javax.inject.Inject

class AppSelectionActivity : BaseActivity(), VisibleAppSelectionAdapter.OnClickListeners,
    HiddenAppSelectionAdapter.OnClickListeners {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var adapterHiddenApps: HiddenAppSelectionAdapter

    @Inject
    lateinit var adapterVisibleApps: VisibleAppSelectionAdapter

    @Inject
    @VisibleAppLayoutManager
    lateinit var visibleAppsLayoutManager: GridLayoutManager

    @Inject
    @HiddenAppLayoutManager
    lateinit var hiddenAppsLayoutManager: GridLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    private lateinit var appSelectionActivityViewModel: AppSelectionActivityViewModel

    companion object {
        fun newIntent(context: Context) = Intent(context, AppSelectionActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selection)
        inject()
        setupRecyclerView()
        getApps()
        observeLiveData()
    }

    private fun inject() {
        appSelectionActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(AppSelectionActivityViewModel::class.java)
    }

    private fun setupRecyclerView() {
        rvHideApps.layoutManager = visibleAppsLayoutManager
        rvHideApps.addItemDecoration(itemDecoration)
        rvHideApps.adapter = adapterVisibleApps
        adapterVisibleApps.setOnClickListeners(this)

        rvUnHideApps.layoutManager = hiddenAppsLayoutManager
        rvUnHideApps.addItemDecoration(itemDecoration)
        rvUnHideApps.adapter = adapterHiddenApps
        adapterHiddenApps.setOnClickListeners(this)
    }

    private fun getApps() {
        appSelectionActivityViewModel.visibleAppListLiveData.observe(this, Observer {
            if (it.isEmpty()) {
                rvHideApps.hide()
            } else {
                rvHideApps.show()
            }
            appSelectionActivityViewModel.setVisibleApps()
        })

        appSelectionActivityViewModel.hiddenAppListLiveData.observe(this, Observer {
            if (it.isEmpty()) {
                rvUnHideApps.hide()
            } else {
                rvUnHideApps.show()
            }
            appSelectionActivityViewModel.setHiddenApps()
        })
    }

    private fun observeLiveData() {
        appSelectionActivityViewModel.selectedAppCountLiveData.observe(this, Observer {
            tvStatus.text = getString(R.string.n_app_selected, it)
            if (it > 0)
                btnCheck.setImageResource(R.drawable.ic_check_white)
            else
                btnCheck.setImageResource(R.drawable.ic_check_white_disabled)
        })

        appSelectionActivityViewModel.showSeparatorLiveData.observe(this, Observer {
            progressBar.hide()
            scrollView.show()
            setListeners()

            if (it)
                ivSeparatorRvs.show()
            else
                ivSeparatorRvs.hide()
        })
    }

    private fun setListeners() {
        btnCheck.setOnClickListener {
            appSelectionActivityViewModel.hideSelectedApps()
            appSelectionActivityViewModel.unhideSelectedApps()
            finish()
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onHiddenItemClick(position: Int, appInfo: App) {
        appSelectionActivityViewModel.toggleHiddenSelection(position)
    }

    override fun onVisibleItemClick(position: Int, appInfo: App) {
        appSelectionActivityViewModel.toggleVisibleSelection(position)
    }
}