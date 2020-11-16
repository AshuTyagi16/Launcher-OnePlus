package com.sasuke.launcheroneplus.util

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*

class DebouncingEditTextQueryTextListener(
    lifecycle: Lifecycle,
    private val onDebouncingQueryTextChange: (CharSequence?) -> Unit
) : TextWatcher, LifecycleObserver {

    private var debouncePeriod: Long = 100

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private var searchJob: Job? = null

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroy() {
        searchJob?.cancel()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            newText?.let {
                delay(debouncePeriod)
                withContext(Dispatchers.Main) {
                    onDebouncingQueryTextChange(it.toString())
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}