package com.connect.demo

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroRequest {
    private const val TIMEOUT_SECONDS:Long = 30
    fun getRetroFitRestAdapter(): Retrofit {
        val clientObjBuilder: OkHttpClient.Builder =
                OkHttpClient.Builder().connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

        val okHttpClient = clientObjBuilder.build()

        return Retrofit.Builder().baseUrl(ConfigProvider.getBaseUrl()).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson())).build()
    }

}