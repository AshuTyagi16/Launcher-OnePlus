package com.sasuke.launcheroneplus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.util.AppListUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            LauncherApp.get(it.applicationContext).getComponent().getAllListUtils().let { appListUtil ->
                intent?.let { intent ->
                    intent.action?.let {
                        when (it) {
                            Intent.ACTION_PACKAGE_ADDED -> {
                                intent.data?.encodedSchemeSpecificPart?.let {
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            appListUtil.addAppToDB(it)
                                        }
                                    }
                                }
                            }
                            Intent.ACTION_PACKAGE_REMOVED -> {
                                intent.data?.encodedSchemeSpecificPart?.let {
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            appListUtil.removeAppFromDB(it)
                                        }
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }

            }
        }
    }
}