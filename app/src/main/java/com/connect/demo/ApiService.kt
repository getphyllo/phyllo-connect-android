package com.connect.demo

import okhttp3.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("/v1/users")
    fun createUser(@Header("Authorization") auth: String,
                   @Body body: RequestCreateUser): Call<UserResponse>

    @POST("/v1/sdk-tokens")
    fun generateToken(@Header("Authorization") auth: String, @Body body: TokenRequest): Call<TokenResponse>

}

object ApiServiceProvider {

    fun createUser(user: String, externalId: String, callback: UserResponseCallback) {
        val authorization = Credentials.basic(ConfigProvider.getConfig()?.clientId!!, ConfigProvider.getConfig()?.clientSecret!!)
        val restAdapter = RetroRequest.getRetroFitRestAdapter()
        val connectService = restAdapter.create(ApiService::class.java)
        val connectServiceCallback = object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    callback.onUserResponse(response.body())
                } else {
                    callback.onUserResponse(null)
                }

            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                callback.onUserResponse(null)
            }
        }

        val userRequest = RequestCreateUser(user, externalId)
        val call: Call<UserResponse> = connectService.createUser( authorization,body = userRequest)
        call.enqueue(connectServiceCallback)

    }

    fun generateToken(userId: String, redirectUrl: String, callback: TokenResponseCallback) {
        val authorization = Credentials.basic(ConfigProvider.getConfig()?.clientId!!, ConfigProvider.getConfig()?.clientSecret!!)
        val restAdapter = RetroRequest.getRetroFitRestAdapter()
        val connectService = restAdapter.create(ApiService::class.java)
        val connectServiceCallback = object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    callback.onTokenResponse(response.body())
                } else {
                    callback.onTokenResponse(null)
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                callback.onTokenResponse(null)
            }
        }

        val userRequest = TokenRequest(userId)
        val call: Call<TokenResponse> = connectService.generateToken( authorization,body = userRequest)
        call.enqueue(connectServiceCallback)

    }

    interface UserResponseCallback {
        fun onUserResponse(response: UserResponse?)
    }

    interface TokenResponseCallback {
        fun onTokenResponse(response: TokenResponse?)
    }
}