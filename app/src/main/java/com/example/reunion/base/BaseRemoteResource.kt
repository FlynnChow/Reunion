package com.example.reunion.base

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseRemoteResource {
    companion object{
        @JvmStatic
        val BASE_URL_FACE = ""
        @JvmStatic
        val BASE_URL_SERVER = ""
        @JvmStatic
        val client:OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpClient.Builder().run {
                readTimeout(15,TimeUnit.SECONDS)
                writeTimeout(15,TimeUnit.SECONDS)
                build()
            }
        }

        @JvmStatic
        val serverRetrofit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().run {
                baseUrl(BASE_URL_SERVER)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }
        }

        @JvmStatic
        val faceRetrofit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().run {
                baseUrl(BASE_URL_FACE)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }
        }
    }

    protected suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null){
                        it.resume(body)
                    }else{
                        it.resumeWithException(RuntimeException("BodyNullException"))
                    }
                }
            })
        }
    }
}