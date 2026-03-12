package com.example.app_proj

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app_proj.DoctorProfileScreen

@Composable
fun DoctorScreen(
    navController: NavHostController,
    viewModel: UserViewModel
) {
    val profile = viewModel.userProfile
    val doctorappointments = viewModel.doctorList

    LaunchedEffect(viewModel.currentUserId) {
        if (viewModel.currentUserId.isNotEmpty()) {
            viewModel.getDoctorAppointmentslist()
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showUpdateDialog by remember { mutableStateOf(false) }
    var appointmentToUpdate by remember { mutableStateOf<DoctorList?>(null) }

    Scaffold(
        bottomBar = { BottomNavigationBarForD(navController, currentRoute) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(color = Color(0xFF2196F3))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Doctor View",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { navController.navigate(Screen.DoctorProfile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val pendingAppointments = doctorappointments.filter {
                    it.status != "approve" && it.status != "reject"
                }

// 2. Check if the FILTERED list is empty
                if (pendingAppointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ยังไม่มีสัตว์เลี้ยง\nที่จองคิวมา",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingAppointments) { docappointment ->
                            DoctorAppointmentCard(
                                appointment = docappointment,
                                onUpdateStatus = {
                                    showUpdateDialog = true
                                    appointmentToUpdate = it
                                }
                            )
                        }
                    }
                }

                if (showUpdateDialog) {
                    AlertDialog(
                        onDismissRequest = { showUpdateDialog = false },
                        title = { Text("จัดการการนัดหมาย") },
                        text = {
                            Text("ต้องการตอบรับหรือปฏิเสธการนัดหมายของ ${appointmentToUpdate?.user_name}?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val petId = appointmentToUpdate?.pet_id
                                    val appId = appointmentToUpdate?.app_id
                                    if (petId != null && appId != null) {
                                        showUpdateDialog = false
                                        navController.navigate("treatment_screen/$petId/$appId")
                                    }
                                }
                            ) {
                                Text("ตอบรับ")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    appointmentToUpdate?.app_id?.let { id ->
                                        viewModel.updateAppointmentStatus(id, "reject")
                                    }
                                    showUpdateDialog = false
                                }
                            ) {
                                Text("ปฏิเสธ", color = Color.Red)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBarForD(navController: NavHostController, currentRoute: String?) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Text("📅", fontSize = 20.sp) },
            label = { Text("นัดหมาย") },
            selected = currentRoute == Screen.Doctor.route,
            onClick = {
                navController.navigate(Screen.Doctor.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Text("🗒️", fontSize = 20.sp) },
            label = { Text("ประวัติการรักษา") },
            selected = currentRoute == Screen.DoctorHistory.route,
            onClick = {
                navController.navigate(Screen.DoctorHistory.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun DoctorAppointmentCard(appointment: DoctorList, onUpdateStatus: (DoctorList) -> Unit ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for pet image or icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text("🐾", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.pet_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "อาการ : ${appointment.reason}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "วันที่ ${appointment.app_date.split("T")[0]} - เวลา ${appointment.app_time.split(":").take(2).joinToString(":")}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Status : ${appointment.status}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { onUpdateStatus(appointment) }) {
                Icon(
                    imageVector = (Icons.Default.MoreVert),
                    contentDescription = "Update Status",
                    tint = Color.DarkGray
                )
            }
        }
    }
}