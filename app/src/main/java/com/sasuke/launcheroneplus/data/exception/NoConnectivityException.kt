package com.sasuke.launcheroneplus.data.exception

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String?
        get() = "Internet not connected"
}