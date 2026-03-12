package com.example.app_proj

import androidx.compose.ui.draw.BlurredEdgeTreatment
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.ContextualSerializer

data class PetClass(
    @SerializedName("pet_id")    val petId: Int?,
    @SerializedName("pet_name")  val petName: String?,
    @SerializedName("species")   val species: String?,
    @SerializedName("breed")     val breed: String?,
    @SerializedName("bloodtype") val bloodtype: String?,
    @SerializedName("birth_date")val birthDate: String?,
    @SerializedName("gender")    val gender: String?,
    @SerializedName("weight")    val weight: Double?,
    @SerializedName("allergy")   val allergy: String?,
    @SerializedName("image_url") val imageUrl: String?
)

data class PetInsertRequest(
    @SerializedName("pet_name")     val pet_name: String,
    @SerializedName("species")      val species: String,
    @SerializedName("pet_breed")    val pet_breed: String,
    @SerializedName("bloodtype")    val bloodtype: String,
    @SerializedName("birth_date")   val birth_date: String,
    @SerializedName("pet_gender")   val pet_gender: String,
    @SerializedName("weight")       val weight: String,
    @SerializedName("allergy")      val allergy: String,
    @SerializedName("pet_image_url")val pet_image_url: String = ""
)

data class PetUpdateRequest(
    @SerializedName("pet_name")     val pet_name: String,
    @SerializedName("species")      val species: String,
    @SerializedName("breed")        val breed: String,
    @SerializedName("bloodtype")    val bloodtype: String,
    @SerializedName("birth_date")   val birth_date: String,
    @SerializedName("gender")       val gender: String,
    @SerializedName("weight")       val weight: String,
    @SerializedName("allergy")      val allergy: String,
    @SerializedName("pet_image_url")val pet_image_url: String = ""
)

data class PetResponse(
    @SerializedName("error")   val error: Boolean,
    @SerializedName("message") val message: String
)

data class MedRecord(
    @SerializedName("diagnosis") val diagnosis: String,
    @SerializedName("treatment_detail") val treatment_detail: String,
    @SerializedName("treatment_date") val treatment_date: String,
    @SerializedName("treatment_time") val treatment_time: String,
    @SerializedName("cost") val cost: Float
)