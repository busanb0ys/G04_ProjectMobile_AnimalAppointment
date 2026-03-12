package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class HomeClass(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("user_id") val user_id: Int?,      // Changed from std_id
    @SerializedName("username") val username: String?, // This is your login ID
    @SerializedName("full_name") val fullName: String?, // Matches your UI
    @SerializedName("phone") val phone: String?,        // Matches your UI
    @SerializedName("role") val role: String?
)
