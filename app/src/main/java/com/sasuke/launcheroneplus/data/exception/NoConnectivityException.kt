package com.sasuke.launcheroneplus.data.exception

import com.sasuke.launcheroneplus.util.Constants
import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String?
        get() = Constants.INTERNET_NOT_CONNECTED
}