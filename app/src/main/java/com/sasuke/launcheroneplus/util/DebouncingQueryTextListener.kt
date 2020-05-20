package com.sasuke.launcheroneplus.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.coroutines.*

class DebouncingQueryTextListener(
    lifecycle: Lifecycle,
    private val onDebouncingQueryTextChange: (String?) -> Unit
) : MaterialSearchView.OnQueryTextListener, LifecycleObserver {
    private var debouncePeriod: Long = 700

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private var searchJob: Job? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            onDebouncingQueryTextChange(it)
            return false
        } ?: run {
            return false
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            newText?.let {
                delay(debouncePeriod)
                onDebouncingQueryTextChange(newText)
            }
        }
        return false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroy() {
        searchJob?.cancel()
    }
}