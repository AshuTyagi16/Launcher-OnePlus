package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.AppInfo
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import kotlinx.android.synthetic.main.activity_app_selection.*
import javax.inject.Inject

class AppSelectionActivity : BaseActivity(), AppSelectionAdapter.OnClickListeners {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var adapter: AppSelectionAdapter

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    private lateinit var appSelectionActivityViewModel: AppSelectionActivityViewModel

    private var appCount = 0

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
        setListeners()
    }

    private fun inject() {
        appSelectionActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(AppSelectionActivityViewModel::class.java)
    }

    private fun setupRecyclerView() {
        rvApps.layoutManager = layoutManager
        rvApps.addItemDecoration(itemDecoration)
        rvApps.adapter = adapter
        adapter.setOnClickListeners(this)
    }

    private fun getApps() {
        appSelectionActivityViewModel.getAppList()
    }

    private fun observeLiveData() {
        appSelectionActivityViewModel.selectedAppCountLiveData.observe(this, Observer {
            appCount = it
            tvStatus.text = getString(R.string.n_app_selected, it)
            if (it > 0)
                btnCheck.setImageResource(R.drawable.ic_check_white)
            else
                btnCheck.setImageResource(R.drawable.ic_check_white_disabled)
        })
    }

    private fun setListeners() {
        btnCheck.setOnClickListener {
            if (appCount > 0)
                finish()
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(position: Int, appInfo: AppInfo) {
        appSelectionActivityViewModel.toggleSelection(position)
    }
}