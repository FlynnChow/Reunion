package com.example.reunion.base

import com.example.reunion.repostory.remote_resource.ServerApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.RuntimeException
import java.net.ConnectException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseRemoteResource {
    companion object{
        @JvmStatic
        val BASE_URL_FACE = ""
        @JvmStatic
        val BASE_URL_SERVER = "https://reunion.yulinzero.xyz/"
        @JvmStatic
        val BASE_URL_NEWS = "https://api.jisuapi.com/news/"
        val client:OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpClient.Builder().run {
                readTimeout(15,TimeUnit.SECONDS)
                writeTimeout(15,TimeUnit.SECONDS)
                connectTimeout(10,TimeUnit.SECONDS)
                build()
            }
        }

        @JvmStatic
        private val serverRetrofit: Retrofit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().run {
                baseUrl(BASE_URL_SERVER)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }
        }

        @JvmStatic
        fun getServiceRemote() = serverRetrofit.create(ServerApi::class.java)

        @JvmStatic
        private val faceRetrofit: Retrofit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().run {
                baseUrl(BASE_URL_FACE)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }
        }

        fun getFaceRemote() = faceRetrofit.create(ServerApi::class.java)

        @JvmStatic
        private val newsRetrofit: Retrofit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Retrofit.Builder().run {
                baseUrl(BASE_URL_NEWS)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }
        }

        fun getNewsRemote() = newsRetrofit.create(ServerApi::class.java)
    }

    protected suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (t is HttpException){
                        it.resumeWithException(ConnectException("网络开小差了"))
                    }else if(t is ConnectException){
                        it.resumeWithException(ConnectException("网络连接失败"))
                    }
                    else
                        it.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null){
                        it.resume(body)
                    }else{
                        val code = response.code()
                        when(code){
                            in 100 until 200->{
                                it.resumeWithException(RuntimeException("服务器正在处理"))
                            }
                            in 300 until 400->{
                                it.resumeWithException(RuntimeException("应用异常"))
                            }
                            in 400 until 500->{
                                it.resumeWithException(RuntimeException("服务器异常"))
                            }else ->{
                                it.resumeWithException(RuntimeException("服务器异常"))
                            }
                        }
                    }
                }
            })
        }
    }
}