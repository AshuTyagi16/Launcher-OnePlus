package com.sasuke.launcheroneplus.util

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import com.sasuke.launcheroneplus.data.exception.NoConnectivityException
import com.sasuke.launcheroneplus.data.model.Error
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

abstract class ApiCallback<T> : retrofit2.Callback<T> {

    final override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                success(body)
            } ?: run {
                createError(response)
            }
        } else {
            createError(response)
        }
    }

    final override fun onFailure(call: Call<T>, t: Throwable) {
        Timber.e(t.cause)
        var error = Error()
        if (t is NoConnectivityException) {
            error = Error(true, t.message!!)
            failure(error)
        } else if (t is SSLHandshakeException) {
            error = Error(true, "Internet not working properly")
            failure(error)
        } else if (t is UnknownHostException) {
            error = Error(true, "Internet not working properly")
            failure(error)
        } else
            failure(error)
    }

    private fun createError(response: Response<T>) {
        response.errorBody()?.let { errorBody ->
            try {
                val error = Gson().fromJson<Error>(errorBody.string(), Error::class.java)
                failure(error)
            } catch (e: Exception) {
                failure(Error(true, e.localizedMessage))
            }
        } ?: run { failure(Error()) }
    }

    abstract fun success(response: T)

    abstract fun failure(error: Error)

}
