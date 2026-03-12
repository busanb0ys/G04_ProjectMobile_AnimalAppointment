package com.example.app_proj

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedhistoryScreen(
    navController: NavHostController,
    viewModel: UserViewModel, // เพิ่ม ViewModel เข้ามาเพื่อดึงข้อมูล
    petId: String,            // เพิ่ม petId เพื่อใช้ค้นหาประวัติ
    petName: String
) {
    // ดึงข้อมูลเมื่อเข้ามาหน้านี้
    LaunchedEffect(petId) {
        if (petId.isNotEmpty()) {
            viewModel.getMedicalRecords(petId)
        }
    }

    // อ่านค่า list ของประวัติการรักษาจาก ViewModel
    val medicalRecords = viewModel.recordList

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ประวัติของ $petName",
                        color = Color.White,
                        fontSize = 20.sp, // ปรับขนาดฟอนต์ให้พอดีกับรูป
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
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    ) {
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
            //BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFFBE6)) // พื้นหลังสีเหลืองอ่อนตามรูป
        ) {
            // ส่วนแสดงรูปสัตว์เลี้ยง (พื้นหลังสีขาว)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // เปลี่ยนเป็นรูปสัตว์เลี้ยงจริง หรือใช้ placeholder ที่มีอยู่
                Box(
                    modifier = Modifier.size(130.dp)
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("🐾", fontSize = 76.sp) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ส่วนแสดงรายการประวัติการรักษา
            if (viewModel.isLoading) {
                // แสดงตอนกำลังโหลดข้อมูล
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            } else if (medicalRecords.isEmpty()) {
                // แสดงเมื่อไม่มีประวัติ
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "ยังไม่มีประวัติการรักษา", color = Color.Gray)
                }
            } else {
                // แสดงรายการประวัติ
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(medicalRecords) { record ->
                        MedicalRecordItem(
                            record = record,
                            navController = navController,
                            petName = petName
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalRecordItem(
    record: MedicalRecord,
    navController: NavHostController,
    petName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // เปลี่ยนมาส่ง record_id แทน
                val recordId = record.recordId?.toString() ?: ""
                navController.navigate("medrecordRoute/$petName/$recordId")
            },
        shape = RoundedCornerShape(12.dp), // ขอบมนตามรูป
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            // วันที่ อยู่ด้านบนซ้าย
            Text(
                text = record.treatment_date ?: "-",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // อาการ อยู่ตรงกลาง
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.diagnosis ?: "ไม่ระบุอาการ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}