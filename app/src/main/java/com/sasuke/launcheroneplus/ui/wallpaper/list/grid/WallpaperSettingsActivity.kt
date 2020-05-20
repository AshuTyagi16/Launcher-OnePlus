package com.sasuke.launcheroneplus.ui.wallpaper.list.grid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Status
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.wallpaper.list.pager.WallpaperPagerActivity
import com.sasuke.launcheroneplus.util.DebouncingQueryTextListener
import kotlinx.android.synthetic.main.activity_wallpaper_settings.*
import javax.inject.Inject

class WallpaperSettingsActivity : BaseActivity(), WallpaperAdapter.OnItemClickListener {

    @Inject
    lateinit var adapter: WallpaperAdapter

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var wallpaperActivityViewModel: WallpaperActivityViewModel

    private lateinit var query: String

    companion object {
        fun newIntent(context: Context) = Intent(context, WallpaperSettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_settings)
        inject()
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeLiveData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.wallpaper, menu)
        menu?.findItem(R.id.action_search)?.let {
            searchView.setMenuItem(it)
        }
        return true
    }

    private fun inject() {
        wallpaperActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(WallpaperActivityViewModel::class.java)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.wallpaper)
    }

    private fun setupRecyclerView() {
        rvWallpaper.layoutManager = layoutManager
        rvWallpaper.adapter = adapter
        adapter.setOnItemClickListener(this)
        rvWallpaper.addItemDecoration(itemDecoration)
    }

    private fun setupListeners() {
        searchView.setOnQueryTextListener(DebouncingQueryTextListener(this.lifecycle) {
            it?.let {
                if (it.isNotBlank()) {
                    query = it
                    wallpaperActivityViewModel.getWallpapersForQuery(it)
                }
            }
        })
    }

    private fun observeLiveData() {
        wallpaperActivityViewModel.wallpaperLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    it.data?.let {
                        adapter.addWallpapers(it.results)
                        adapter.notifyDataSetChanged()
                    }
                }
                Status.ERROR -> {
                }
            }
        })
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onItemClick(position: Int) {
        startActivity(WallpaperPagerActivity.newIntent(this, query, position))
    }
}