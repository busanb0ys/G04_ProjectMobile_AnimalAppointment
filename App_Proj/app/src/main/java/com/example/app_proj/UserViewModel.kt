package com.example.app_proj

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    // Login Result State
    private var _loginResult by mutableStateOf<LoginClass?>(null)
    val loginResult: LoginClass?
        get() = _loginResult

    // Error Message State
    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String?
        get() = _errorMessage

    private var _userProfile by mutableStateOf<UserProfile?>(null)
    val userProfile: UserProfile?
        get() = _userProfile

    // Reset Login Result
    fun resetLoginResult() {
        _loginResult = null
    }

    fun reseterrorMessage() {
        _errorMessage = null
    }

    var currentUserId by mutableStateOf("")
    var currentDocId by mutableStateOf("")
    var nickname by mutableStateOf("")
    var fullName by mutableStateOf("")
    var phone by mutableStateOf("")

    // State for API feedback
    var isLoading by mutableStateOf(false)
    var updateMessage by mutableStateOf("")

    // Inside your ViewModel
    fun updateUser(userId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Create the object using the CURRENT values of fullName and phone
                val request = UserProfile(
                    userId = null,
                    username = null,
                    fullName = fullName, // From ViewModel state
                    phone = phone,        // From ViewModel state
                    docId = null,
                    nickname = nickname
                )

                val response = UserClient.studentAPI.updateUser(userId, request)

                if (response.isSuccessful && response.body() != null) {
                    updateMessage = "อัปเดตข้อมูลสำเร็จ"
                    // Refresh the profile after update to keep UI in sync
                    getUserProfile(userId)
                    onSuccess()
                } else {
                    updateMessage = "ล้มเหลว: ${response.message()}"
                }
            } catch (e: Exception) {
                updateMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                fullName = ""
                phone = ""
                onSuccess()
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.getUserInfo(userId)
                if (response.isSuccessful) {
                    val profile = response.body()
                    _userProfile = profile
                    // FILL THE FIELDS
                    currentDocId = profile?.docId?.toString() ?: ""
                    fullName = profile?.fullName ?: ""
                    phone = profile?.phone ?: ""
                }
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun login(usernameInput: String, passwordInput: String) {
        viewModelScope.launch {
            try {
                val loginData = mapOf(
                    "username" to usernameInput,
                    "password" to passwordInput
                )
                val response = UserClient.studentAPI.loginStudent(loginData)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (!body.error) {
                        _loginResult = body
                        _errorMessage = null
                        currentUserId = body.userId.toString()

                        if (currentUserId.isNotEmpty()) {
                            getUserProfile(currentUserId)
                        }
                    } else {
                        _errorMessage = body.message
                    }
                } else {
                    _errorMessage = "Login failed: Invalid credentials"
                }
            } catch (e: Exception) {
                _errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun register(
        context: Context,
        student: RegisterClass,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.registerStudent(student)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (!result.error) {
                        _errorMessage = null
                        Toast.makeText(
                            context,
                            result.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        onSuccess()
                    } else {
                        _errorMessage = result.message
                        Toast.makeText(
                            context,
                            result.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val errorRawString = response.errorBody()?.string()
                    val finalMessage = if (!errorRawString.isNullOrEmpty()) {
                        try {
                            val errorData = Gson().fromJson(
                                errorRawString,
                                RegisterResponse::class.java
                            )
                            errorData.message
                        } catch (e: Exception) {
                            errorRawString
                        }
                    } else {
                        response.message()
                    }
                    _errorMessage = finalMessage

                    Toast.makeText(
                        context,
                        finalMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                val networkError = "Network Error: ${e.message}"
                _errorMessage = networkError

                Toast.makeText(
                    context,
                    networkError,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Pet States
    var petName by mutableStateOf("")
    var petSpecies by mutableStateOf("")
    var petBreed by mutableStateOf("")
    var petBloodtype by mutableStateOf("")
    var petBirthDate by mutableStateOf("")
    var petGender by mutableStateOf("")
    var petWeight by mutableStateOf("")
    var petAllergy by mutableStateOf("")

    fun clearPetForm() {
        petName = ""; petSpecies = ""; petBreed = ""
        petBloodtype = ""; petBirthDate = ""; petGender = ""
        petWeight = ""; petAllergy = ""
    }

    fun getPetInfo(petId: String) {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.getPetInfo(petId)
                if (response.isSuccessful) {
                    response.body()?.let { pet ->
                        petName      = pet.petName   ?: ""
                        petSpecies   = pet.species   ?: ""
                        petBreed     = pet.breed     ?: ""
                        petBloodtype = pet.bloodtype ?: ""

                        // ตัดแค่ YYYY-MM-DD
                        petBirthDate = pet.birthDate
                            ?.takeIf { it.length >= 10 }
                            ?.substring(0, 10) ?: ""

                        petGender  = pet.gender ?: ""

                        petWeight  = pet.weight?.let { w ->
                            if (w == w.toLong().toDouble()) w.toLong().toString()
                            else w.toString()
                        } ?: ""

                        petAllergy = pet.allergy ?: ""
                    }
                }
            } catch (e: Exception) { _errorMessage = e.message }
        }
    }

    fun insertPet(userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = PetInsertRequest(
                    pet_name   = petName,
                    species    = petSpecies,
                    pet_breed  = petBreed,
                    bloodtype  = petBloodtype,
                    birth_date = petBirthDate,
                    pet_gender = petGender,
                    weight     = petWeight,
                    allergy    = petAllergy
                )
                val response = UserClient.studentAPI.insertPet(userId, request)
                if (response.isSuccessful && response.body()?.error == false) {
                    getPetList()
                    onSuccess()
                } else {
                    _errorMessage = response.body()?.message ?: "Insert failed"
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun insertMedRecord(
        petId: Int,
        docId: Int,
        appId: Int,
        diagnosis: String,
        treatmentDetail: String,
        treatmentDate: String,
        treatmentTime: String,
        cost: Float
    ) {
        viewModelScope.launch {
            try {
                val request = MedRecord(
                    diagnosis = diagnosis,
                    treatment_detail = treatmentDetail,
                    treatment_date = treatmentDate,
                    treatment_time = treatmentTime,
                    cost = cost
                )
                val response = UserClient.studentAPI.insertMedRecord(
                    petId,
                    docId,
                    appId,
                    request
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {
                        println("Insert success")
                    } else {
                        println("Insert fail: ${body?.message}")
                    }
                } else {
                    println("API error: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    fun updatePet(petId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = PetUpdateRequest(
                    pet_name   = petName,
                    species    = petSpecies,
                    breed      = petBreed,
                    bloodtype  = petBloodtype,
                    birth_date = petBirthDate,
                    gender     = petGender,
                    weight     = petWeight,
                    allergy    = petAllergy
                )
                val response = UserClient.studentAPI.updatePet(petId, request)
                if (response.isSuccessful && response.body()?.error == false) {
                    getPetList()
                    onSuccess()
                } else {
                    _errorMessage = response.body()?.message ?: "Update failed"
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun updateAppointmentStatus(appId: String, stat: String) {
        viewModelScope.launch {
            try {
                val body = mapOf("status" to stat)
                val response = UserClient.studentAPI.updateAppointmentStatus(appId, body)
                if (response.isSuccessful && response.body() != null) {
                    println("Update success")
                } else {
                    println("Update failed")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    fun deletePet(petId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = UserClient.studentAPI.deletePet(petId)
                if (response.isSuccessful) {
                    getPetList()
                    onSuccess()
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // ---- Pet List ----
    var petList by mutableStateOf<List<PetListItem>>(emptyList())
        private set

    fun getPetList() {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.getPetList(currentUserId)
                if (response.isSuccessful) {
                    petList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            }
        }
    }

    // ----MedRecord
    var recordList by mutableStateOf<List<MedicalRecord>>(emptyList())
        private set
    var currentMedRecord by mutableStateOf<MedicalRecord?>(null)
        private set

    fun getMedicalRecords(petId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = UserClient.studentAPI.getMedicalRecords(petId)
                if (response.isSuccessful) {
                    recordList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun getMedicalRecordInfo(recordId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = UserClient.studentAPI.getMedicalRecordInfo(recordId)
                if (response.isSuccessful) {
                    currentMedRecord = response.body()
                } else {
                    _errorMessage = "ดึงข้อมูลล้มเหลว"
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // ---- Appointments ----
    var appointmentList by mutableStateOf<List<Appointment>>(emptyList())
        private set

    fun getAppointmentsList() {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.getUserAppointmentslist(currentUserId)
                if (response.isSuccessful) {
                    appointmentList = response.body() ?: emptyList()
                } else {
                    _errorMessage = "Failed to fetch appointments"
                }
            } catch (e: Exception) {
                _errorMessage = "Error: ${e.message}"
            }
        }
    }

    var doctorList by mutableStateOf<List<DoctorList>>(emptyList())
        private set

    fun getDoctorAppointmentslist() {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.getDoctorAppointmentslist(currentDocId)
                if (response.isSuccessful) {
                    doctorList = response.body() ?: emptyList()
                } else {
                    _errorMessage = "Failed to fetch doctor appointment list"
                }
            } catch (e: Exception) {
                _errorMessage = "Error: ${e.message}"
            }
        }
    }

    var medHist by mutableStateOf<List<MedHistory>>(emptyList())
        private set

    fun getMedRecByDoctor() {
        viewModelScope.launch {
            try {
                val response = UserClient.studentAPI.getMedRecByDoctor(currentDocId.toInt())
                if (response.isSuccessful) {
                    medHist = response.body() ?: emptyList()
                } else {
                    _errorMessage = "Failed to fetch doctor appointment list"
                }
            } catch (e: Exception) {
                _errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun deleteAppointment(appId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = UserClient.studentAPI.deleteAppointment(appId.toString())
                if (response.isSuccessful) {
                    getDoctorAppointmentslist()
                }
                if (response.isSuccessful && response.body()?.error == false) {
                    getAppointmentsList()
                    onSuccess()
                } else {
                    _errorMessage = response.body()?.message ?: "Delete failed"
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // --- Doctors State ---
    var doctors by mutableStateOf<List<Doctor>>(emptyList())
        private set
    var doctorsLoading by mutableStateOf(false)
        private set
    var doctorsError by mutableStateOf<String?>(null)
        private set

    fun fetchDoctors() {
        viewModelScope.launch {
            doctorsLoading = true
            doctorsError = null
            try {
                val response = UserClient.studentAPI.getDoctors()
                if (response.isSuccessful) {
                    doctors = response.body() ?: emptyList()
                } else {
                    doctorsError = "Failed to fetch doctors: ${response.message()}"
                }
            } catch (e: Exception) {
                doctorsError = "Error: ${e.message}"
            } finally {
                doctorsLoading = false
            }
        }
    }

    fun bookAppointment(
        petId: Int,
        docId: Int,
        appDate: String,
        appTime: String,
        reason: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = AppointmentRequest(appDate, appTime, reason)
                val response = UserClient.studentAPI.insertAppointment(
                    petId, currentUserId, docId, request
                )
                if (response.isSuccessful && response.body()?.error == false) {
                    onSuccess()
                } else {
                    _errorMessage = response.body()?.message ?: "Booking failed"
                }
            } catch (e: Exception) {
                _errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
