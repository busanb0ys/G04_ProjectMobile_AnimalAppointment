package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class Appointment(
    @SerializedName("app_id") val appId: Int,
    @SerializedName("pet_id") val petId: Int,
    @SerializedName("pet_name") val petName: String,
    @SerializedName("doc_id") val docId: Int,
    @SerializedName("full_name") val docFullName: String, // Doctor's full name
    @SerializedName("app_date") val appDate: String,
    @SerializedName("app_time") val appTime: String,
    val reason: String,
    val status: String
)

data class AppointmentDeleteResponse(
    val error: Boolean,
    val message: String
)
