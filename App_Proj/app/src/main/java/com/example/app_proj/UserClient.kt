package com.example.app_proj

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserClient {

        private const val BASE_URL = "https://e2a8-223-206-232-104.ngrok-free.app/"

        val studentAPI: UserAPI by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserAPI::class.java)
        }
    }
