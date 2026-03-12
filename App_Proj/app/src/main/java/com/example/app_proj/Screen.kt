package com.example.app_proj

sealed class Screen(val route:String, val name :String ){
    data object Login: Screen(route = "login_screen", name = "Login")
    data object Register: Screen(route = "register_screen", name = "Register")
    data object Home: Screen(route = "Home_screen", name = "Home")

    data object StudentList : Screen(route = "student_list_screen", name = "Student List")

    data object UserProfileScreen : Screen(route = "user_profile_screen", name = "userprofile List")

    data object EditPet : Screen(route = "edit_pet_screen", name = "Edit Pet") {
        const val routeWithArg = "edit_pet_screen?petId={petId}"
        fun createRoute(petId: String? = null) =
            if (petId != null) "edit_pet_screen?petId=$petId" else "edit_pet_screen"
    }

    data object PetDetail : Screen("pet_detail_screen", "Pet Detail") {
        const val routeWithArg = "pet_detail_screen/{petId}"
        fun createRoute(petId: String) = "pet_detail_screen/$petId"
    }

    // MedicalRecord
    data object MedhistoryScreen : Screen(route = "medhistory_screen/{petName}", name = "Medical History") {
        fun createRoute(petName: String) = "medhistory_screen/$petName"
    }

    data object MedrecordScreen : Screen(route = "medrecord_screen/{petName}/{date}", name = "Medical Record Details") {
        fun createRoute(petName: String, date: String) = "medrecord_screen/$petName/$date"
    }

    data object AppointmentScreen: Screen(route = "appointment_screen", name = "Appointment")
    data object AppointmentBookingScreen: Screen(route = "appointment_booking_screen", name = "Appointment Booking")
    data object Doctor : Screen(route = "doctor_screen", name = "Doctor Screen")
    data object DoctorHistory : Screen(route = "doctor_history_screen", name = "Doctor History Screen")
    data object DoctorProfile : Screen(route = "doctor profile screen", name = "Doctor_Profile_Screen")
    data object Treatment : Screen(
        route = "treatment_screen/{pet_id}/{app_id}",
        name = "Treatment_Screen"
    )

    data object BookingFormScreen : Screen(route = "booking_form_screen", name = "Booking Form") {
        const val routeWithArg = "booking_form_screen/{docId}/{docName}"
        fun createRoute(docId: Int, docName: String) = "booking_form_screen/$docId/$docName"
    }
}
