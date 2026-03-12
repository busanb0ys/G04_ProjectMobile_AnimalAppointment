package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class MedicalRecord(
    @SerializedName("record_id") val recordId: Int?,
    @SerializedName("pet_id") val petId: Int?,
    @SerializedName("doc_id") val docId: Int?,
    @SerializedName("app_id") val appId: Int?,
    @SerializedName("diagnosis") val diagnosis: String?,
    @SerializedName("treatment_detail") val treatment_detail: String?,
    @SerializedName("cost") val cost: Double = 0.0,
    @SerializedName("treatment_date") val treatment_date: String?,
    @SerializedName("treatment_time") val treatment_time: String?,
    @SerializedName("doc_name") val doc_name: String? = null

)

data class MedicalRecordResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String
)
