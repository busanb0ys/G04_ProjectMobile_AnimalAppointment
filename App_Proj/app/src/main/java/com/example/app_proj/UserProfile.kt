package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("doc_id") val docId: Int?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("phone") val phone: String?
)