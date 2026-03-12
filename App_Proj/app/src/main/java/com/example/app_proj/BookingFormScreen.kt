package com.example.app_proj

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    docId: Int,
    docName: String
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedPetId by remember { mutableStateOf<Int?>(null) }
    var selectedPetName by remember { mutableStateOf("เลือกสัตว์เลี้ยง") }
    var appDate by remember { mutableStateOf("") }
    var appTime by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var isPetExpended by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getPetList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("จองนัดหมาย", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Doctor Details
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = "Doctor", tint = Color(0xFF2196F3), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = docName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Pet Selection
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                OutlinedTextField(
                    value = selectedPetName,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("สัตว์เลี้ยง") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { isPetExpended = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = isPetExpended,
                    onDismissRequest = { isPetExpended = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    viewModel.petList.forEach { pet ->
                        DropdownMenuItem(
                            text = { Text(pet.petName ?: "") },
                            onClick = {
                                selectedPetId = pet.petId
                                selectedPetName = pet.petName ?: ""
                                isPetExpended = false
                            }
                        )
                    }
                }
            }

            // Date Picker
            OutlinedTextField(
                value = appDate,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            appDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                label = { Text("วันที่นัดหมาย") },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                ),
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
            )

            // Time Picker
            OutlinedTextField(
                value = appTime,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            appTime = String.format("%02d:%02d:00", hourOfDay, minute)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                label = { Text("เวลานัดหมาย") },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                ),
                trailingIcon = { Text("🕒", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp)) }
            )

            // Reason
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 24.dp),
                label = { Text("เหตุผลที่นัดหมาย") },
                maxLines = 4
            )

            // Submit Button
            Button(
                onClick = {
                    if (selectedPetId != null && appDate.isNotEmpty() && appTime.isNotEmpty() && reason.isNotEmpty()) {
                        viewModel.bookAppointment(selectedPetId!!, docId, appDate, appTime, reason) {
                            Toast.makeText(context, "จองนัดหมายสำเร็จ", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.AppointmentScreen.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    } else {
                        Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(25.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ยืนยันการจอง", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Button(
                onClick = {
                    navController.navigate(Screen.AppointmentBookingScreen.route)
                }
            ) {
                Text("กลับ")
            }
        }
    }
}
