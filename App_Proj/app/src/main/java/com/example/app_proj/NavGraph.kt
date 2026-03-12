package com.example.app_proj

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController) {

    val studentViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController, studentViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, studentViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController, studentViewModel)
        }

        composable(Screen.Doctor.route) {
            DoctorScreen(navController, studentViewModel)
        }

        composable(Screen.DoctorHistory.route) {
            DoctorHistoryScreen(navController, studentViewModel)
        }

        composable(Screen.DoctorProfile.route) {
            DoctorProfileScreen(navController, studentViewModel, studentViewModel.currentUserId)
        }

        composable(
            route = Screen.Treatment.route,
            arguments = listOf(
                navArgument("pet_id") { type = NavType.StringType },
                navArgument("app_id") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val petId = backStackEntry.arguments?.getString("pet_id") ?: ""
            val appId = backStackEntry.arguments?.getString("app_id") ?: ""

            TreatmentScreen(
                navController = navController,
                viewModel = studentViewModel,
                pet_id = petId,
                app_id = appId
            )
        }

        composable(Screen.UserProfileScreen.route) {
            UserProfileScreen(
                navController = navController,
                viewModel = studentViewModel,
                userId = studentViewModel.currentUserId
            )
        }
        // Edit Pet
        composable(
            route = Screen.EditPet.routeWithArg,
            arguments = listOf(navArgument("petId") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { back ->
            EditPetScreen(navController, studentViewModel, back.arguments?.getString("petId"))
        }

        // Pet Detail
        composable(
            route = Screen.PetDetail.routeWithArg,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { back ->
            PetDetailScreen(navController, studentViewModel, back.arguments?.getString("petId") ?: "")
        }

        // Med Record
        composable(
            // สมมติว่าสร้าง route เป็น "medhistory/{petId}/{petName}"
            route = "medhistory/{petId}/{petName}",
            arguments = listOf(
                navArgument("petId") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            val petName = backStackEntry.arguments?.getString("petName") ?: "สัตว์เลี้ยง"

            // ใส่ viewModel และ petId ลงไป
            MedhistoryScreen(navController, studentViewModel, petId, petName)
        } //add

        composable(
            route = "medrecordRoute/{petName}/{recordId}", // เปลี่ยนตรงนี้
            arguments = listOf(
                navArgument("petName") { type = NavType.StringType },
                navArgument("recordId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val petName = backStackEntry.arguments?.getString("petName") ?: "สัตว์เลี้ยง"
            val recordId = backStackEntry.arguments?.getString("recordId") ?: ""

            MedRecordScreen(navController, studentViewModel, petName, recordId)
        } //add
        // Appointment List
        composable(Screen.AppointmentScreen.route) {
            AppointmentScreen(navController, studentViewModel)
        }

        // Appointment Booking - List Doctors
        composable(Screen.AppointmentBookingScreen.route) {
            AppointmentBookingScreen(navController, studentViewModel)
        }

        // Booking Form
        composable(
            route = Screen.BookingFormScreen.routeWithArg,
            arguments = listOf(
                navArgument("docId") { type = NavType.IntType },
                navArgument("docName") { type = NavType.StringType }
            )
        ) { back ->
            val docId = back.arguments?.getInt("docId") ?: 0
            val docName = back.arguments?.getString("docName") ?: ""
            BookingFormScreen(navController, studentViewModel, docId, docName)
        }
    }
}
