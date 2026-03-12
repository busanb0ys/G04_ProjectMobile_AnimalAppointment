package com.example.app_proj

import com.google.gson.annotations.SerializedName

data class PetListItem(
    @SerializedName("pet_id")    val petId: Int?,
    @SerializedName("pet_name")  val petName: String?,
    @SerializedName("image_url") val imageUrl: String?
)