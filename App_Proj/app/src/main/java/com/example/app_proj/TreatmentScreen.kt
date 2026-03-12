package com.example.app_proj

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TreatmentScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    pet_id: String,
    app_id: String
) {

    var diagnosis by remember { mutableStateOf("") }
    var treatmentDetail by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D7BE5))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )

            Text(
                text = "การรักษา",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.clickable {
                    navController.navigate("home")
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "รายละเอียดการรักษา",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Diagnosis Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text("สรุปอาการ")

                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("กรอกอาการ") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Treatment Detail
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("คำแนะนำ")

                    OutlinedTextField(
                        value = treatmentDetail,
                        onValueChange = { treatmentDetail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("กรอกคำแนะนำ") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("ค่ารักษา")

                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("กรอกราคา") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val date = LocalDate.now().toString()
                    val time = LocalTime.now().toString().substring(0, 5)
                    viewModel.insertMedRecord(
                        petId = pet_id.toInt(),
                        docId = viewModel.currentDocId.toInt(),
                        appId = app_id.toInt(),
                        diagnosis = diagnosis,
                        treatmentDetail = treatmentDetail,
                        treatmentDate = date,
                        treatmentTime = time,
                        cost = cost.toFloatOrNull() ?: 0f
                    )
                    viewModel.updateAppointmentStatus(app_id, "approve")
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1C5DB6)
                )
            ) {
                Text(
                    text = "🗒️ บันทึกการรักษา",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}