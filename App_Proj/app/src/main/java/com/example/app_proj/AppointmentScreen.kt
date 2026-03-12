package com.example.app_proj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppointmentScreen(
    navController: NavHostController,
    viewModel: UserViewModel
) {
    val appointments = viewModel.appointmentList

    LaunchedEffect(viewModel.currentUserId) {
        if (viewModel.currentUserId.isNotEmpty()) {
            viewModel.getAppointmentsList()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            BottomNavigationBar(navController, currentRoute)
        }
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
                        text = "รายการนัดหมาย",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { navController.navigate(Screen.UserProfileScreen.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (appointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ยังไม่มีการนัดหมาย",
                            fontSize = 24.sp,
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
                        items(appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onDeleteClick = {
                                    appointmentToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("ยืนยันการลบนัดหมาย") },
                    text = { Text("คุณแน่ใจหรือไม่ว่าต้องการลบนัดหมายของ ${appointmentToDelete?.petName} กับ ${appointmentToDelete?.docFullName}?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                appointmentToDelete?.appId?.let {
                                    viewModel.deleteAppointment(it)
                                }
                                showDeleteDialog = false
                            }
                        ) {
                            Text("ลบ", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("ยกเลิก")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment, onDeleteClick: (Appointment) -> Unit) {
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
                    text = appointment.petName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "น.สพ. ${appointment.docFullName}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "วันที่ ${appointment.appDate.split("T")[0]} - เวลา ${appointment.appTime.split(":").take(2).joinToString(":")}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "เหตุผล: ${appointment.reason}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "สถานะ: ${appointment.status}",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = { onDeleteClick(appointment) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Appointment",
                    tint = Color.Red
                )
            }
        }
    }
}
