package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class RegisterClass(
    @SerializedName("std_id") val std_id: String? = null,
    @SerializedName("username") val username: String?= null,
    @SerializedName("password") val password: String?= null,
    @SerializedName("confirm_password") val confirm_password: String?= null,
    @SerializedName("full_name") val full_name: String?= null,
    @SerializedName("role") val role: String?= null
)
