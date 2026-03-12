package com.example.app_proj

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedRecordScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    petName: String,
    recordId: String
) {
    LaunchedEffect(recordId) {
        if (recordId.isNotEmpty()) {
            viewModel.getMedicalRecordInfo(recordId)
        }
        if (viewModel.appointmentList.isEmpty()) {
            viewModel.getAppointmentsList()
        }
        if (viewModel.doctors.isEmpty()) {
            viewModel.fetchDoctors()
        }
    }

    val record = viewModel.currentMedRecord
    val linkedAppointment = viewModel.appointmentList.find { it.appId == record?.appId }

    // ค้นหาข้อมูลหมอจากรายชื่อหมอทั้งหมด (เผื่อต้องการ Nickname)
    val docIdToSearch = record?.docId ?: linkedAppointment?.docId
    val targetDoctor = viewModel.doctors.find { it.doc_id == docIdToSearch }

    // Logic การแสดงชื่อหมอ:
    // 1. ถ้าเจอในรายชื่อหมอ (มี Nickname)
    // 2. ถ้ามีชื่อหมอส่งมาจาก API (doc_name)
    // 3. ถ้าเจอชื่อหมอจากข้อมูลนัดหมาย
    val doctorDisplayName = when {
        targetDoctor != null -> "น.สพ. ${targetDoctor.full_name} (หมอ${targetDoctor.nickname})"
        !record?.doc_name.isNullOrEmpty() -> "น.สพ. ${record?.doc_name}"
        linkedAppointment != null -> "น.สพ. ${linkedAppointment.docFullName}"
        else -> "กำลังรอข้อมูลสัตวแพทย์..."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ประวัติของ $petName",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, "") 
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = record?.treatment_date?.split("T")?.get(0) ?: linkedAppointment?.appDate?.split("T")?.get(0) ?: "-",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Doctor",
                                modifier = Modifier.size(40.dp),
                                tint = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "สัตวแพทย์ผู้ตรวจสอบ", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    text = doctorDisplayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

                        Text(text = "สรุปอาการ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = record?.diagnosis ?: "ไม่พบข้อมูลอาการ",
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(text = "การรักษาและคำแนะนำ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = record?.treatment_detail ?: "ไม่พบข้อมูลการรักษา",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "ค่ารักษา", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${record?.cost ?: 0.0} ฿",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C5DB6)
                            )
                        }
                    }
                }
            }
        }
    }
}
