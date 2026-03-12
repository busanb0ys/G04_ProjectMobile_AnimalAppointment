package com.example.app_proj

import android.media.MediaRecorder
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserAPI {

    // Register
    @POST("register")
    suspend fun registerStudent(
        @Body studentData: RegisterClass
    ): Response<RegisterResponse>

    // Login
    @POST("login")
    suspend fun loginStudent(
        @Body loginData: Map<String, String>
    ): Response<LoginClass>

    //  Search/Profile
    @GET("infoUser/{user_id}")
    suspend fun getUserInfo(
        @Path("user_id") userId: String
    ): Response<UserProfile>


    @PUT("updateUser/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: String,
        @Body user: UserProfile
    ): Response<UpdateResponse>



    // ---- Pet ----
    @GET("getpets/{user_id}")
    suspend fun getPetList(@Path("user_id") userId: String): Response<List<PetListItem>>

    @POST("insertPet/{user_id}")
    suspend fun insertPet(
        @Path("user_id") userId: String,
        @Body pet: PetInsertRequest
    ): Response<PetResponse>

    @GET("infopet/{pet_id}")
    suspend fun getPetInfo(@Path("pet_id") petId: String): Response<PetClass>

    @PUT("updatePet/{pet_id}")
    suspend fun updatePet(
        @Path("pet_id") petId: String,
        @Body pet: PetUpdateRequest
    ): Response<PetResponse>

    @DELETE("deletePet/{pet_id}")
    suspend fun deletePet(@Path("pet_id") petId: String): Response<PetResponse>

    // ----Medical Record----
    @GET("getMedicalRecord/{pet_id}")
    suspend fun getMedicalRecords(@Path("pet_id") petId: String): Response<List<MedicalRecord>>

    @GET("getMedicalRecordInfo/{record_id}")
    suspend fun getMedicalRecordInfo(@Path("record_id") recordId: String): Response<MedicalRecord>

    // เพิ่มฟังก์ชันสำหรับ Insert/Update เผื่อไว้ใช้ในอนาคตด้วยเลย
    @POST("insertMedRecord/{pet_id}")
    suspend fun insertMedRecord(
        @Path("pet_id") petId: String,
        @Body request: Map<String, String> // ส่ง visit_date, diagnosis, treatment
    ): Response<MedicalRecordResponse>

    @PUT("updateMedRecord/{record_id}")
    suspend fun updateMedRecord(
        @Path("record_id") recordId: String,
        @Body request: Map<String, String> // ส่ง visit_date, diagnosis, treatment
    ): Response<MedicalRecordResponse>
    
    // ---- Appointments ----
    @GET("getUserAppointmentslist/{user_id}")
    suspend fun getUserAppointmentslist(@Path("user_id") userId: String): Response<List<Appointment>>

    @PUT("updateAppointmentStatus/{app_id}")
    suspend fun updateAppointmentStatus(@Path("app_id") appId: String,
                                        @Body status: Map<String, String>): Response<List<PetResponse>>

    @PUT("deleteAppointment/{app_id}")
    suspend fun deleteAppointment(@Path("app_id") appId: String): Response<AppointmentDeleteResponse>

    @GET("getDoctors")
    suspend fun getDoctors(): Response<List<Doctor>>

    // --- NEW: Insert Appointment ---
    @POST("insertAppointment/{pet_id}/{user_id}/{doc_id}")
    suspend fun insertAppointment(
        @Path("pet_id") petId: Int,
        @Path("user_id") userId: String,
        @Path("doc_id") docId: Int,
        @Body request: AppointmentRequest
    ): Response<AppointmentResponse>

    @GET("getDoctorAppointmentslist/{doc_id}")
    suspend fun getDoctorAppointmentslist(@Path("doc_id") docId: String): Response<List<DoctorList>>

    @POST("insertMedRecord/{pet_id}/{doc_id}/{app_id}")
    suspend fun insertMedRecord(
        @Path("pet_id") petId: Int,
        @Path("doc_id") docId: Int,
        @Path("app_id") appId: Int,
        @Body request: MedRecord
    ): Response<AppointmentResponse>

    @GET("getMedicalRecordsByDoctor/{doc_id}")
    suspend fun getMedRecByDoctor(
        @Path("doc_id") docId: Int,
    ): Response<List<MedHistory>>

}

data class UpdateResponse(
    val error: Boolean,
    val message: String
)

data class Doctor(
    val doc_id: Int,
    val full_name: String,
    val nickname: String,
    val specialization: String,
    val work_time: String,
    val is_available: String // Changed from Int to String to handle "available", "Yes", etc.
)

// --- NEW: Appointment Request Body ---
data class AppointmentRequest(
    val app_date: String,
    val app_time: String,
    val reason: String
)

// --- NEW: Appointment Response for insert ---
data class AppointmentResponse(
    val error: Boolean,
    val message: String
)

data class DoctorList(
    val pet_id: String,
    val pet_name: String,
    val user_id: String,
    val user_name: String,
    val app_date: String,
    val app_time: String,
    val reason: String,
    val app_id: String,
    val status: String,
)

data class UpdateStatus(
    val app_id: String,
    val status: String
)

data class MedHistory(
    val record_id: String,
    val pet_id: String,
    val diagnosis: String,
    val treatment_detail: String,
    val cost: String,
    val treatment_date: String,
    val treatment_time: String
)