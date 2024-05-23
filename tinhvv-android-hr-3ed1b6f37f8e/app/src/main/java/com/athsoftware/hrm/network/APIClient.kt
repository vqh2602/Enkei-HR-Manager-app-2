package com.athsoftware.hrm.network

import com.athsoftware.hrm.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by tinhvv on 11/10/18.
 */

class APIClient {
    companion object {
        val shared = APIClient()
    }

    private val httpClient by lazy {
        val httpClientBuilder = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val headerInterceptor = Interceptor{ chain ->
            val builder = chain.request().newBuilder().removeHeader("Content-Type")
            val request = builder.build()
            chain.proceed(request)
        }

        httpClientBuilder.pingInterval(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)


        httpClientBuilder.build()
    }

    private val retrofitAuthen by lazy {
        val builder = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .client(httpClient)
        builder.build()
    }


    val api: API by lazy {
        retrofitAuthen.create(API::class.java)
    }
}