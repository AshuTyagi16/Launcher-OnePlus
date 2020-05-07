package com.sasuke.launcheroneplus.util

import androidx.annotation.WorkerThread

object SearchUtils {

    @WorkerThread
    suspend fun matches(
        haystack: String,
        needle: String
    ): Boolean {
        val queryLength = needle.length
        val titleLength = haystack.length
        if (titleLength < queryLength || queryLength <= 0) {
            return false
        }
        var ni = 0
        var hi = 0
        while (hi < titleLength) {
            if (haystack[hi] == needle[ni]) {
                ni++
                if (ni == queryLength) return true
            }
            hi++
        }
        return false
    }

}