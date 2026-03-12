package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class LoginClass(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("role") val role: String?
)
