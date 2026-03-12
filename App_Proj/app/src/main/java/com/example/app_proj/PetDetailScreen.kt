package com.example.app_proj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
fun PetDetailScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    petId: String
) {
    LaunchedEffect(petId) {
        viewModel.getPetInfo(petId)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            // ส่วน Header เดิมที่คุณปรับระยะไว้
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // เพิ่มความสูงเล็กน้อย
                    .background(Color(0xFF2196F3))
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = viewModel.petName.ifEmpty { "รายละเอียดสัตว์เลี้ยง" },
                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.Home, "Home", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        // ใช้ Column + verticalScroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // สำคัญ: ป้องกันเนื้อหาจมใต้ TopBar
                .verticalScroll(scrollState) // ทำให้ไถขึ้นลงได้
                .background(Color.White)
        ) {
            // --- ใส่เนื้อหาทั้งหมดที่นี่ ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp), // เผื่อระยะขอบล่างสุด
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // รูปสัตว์เลี้ยง
                Box(
                    modifier = Modifier.size(130.dp)
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("🐾", fontSize = 76.sp) }

                Spacer(modifier = Modifier.height(16.dp))

                // ปุ่มแก้ไข (วางขวา)
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    // ชื่อสัตว์เลี้ยง label
                    Text(
                        text = "ชื่อสัตว์เลี้ยง",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    TextButton(
                        onClick = {
                            navController.navigate(Screen.EditPet.createRoute(petId))
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("+ แก้ไข", color = Color(0xFF2196F3), fontSize = 14.sp)
                    }
                }

                // ข้อมูลสัตว์เลี้ยง
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ชื่อ
                    PetDetailField(value = viewModel.petName)

                    // ประเภท + วันเกิด
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            PetDetailLabel("ประเภท")
                            PetDetailField(value = viewModel.petSpecies)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            PetDetailLabel("อายุ")
                            PetDetailField(value = calculateAge(viewModel.petBirthDate))
                        }
                    }

                    // สายพันธุ์ + กรุ๊ปเลือด
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            PetDetailLabel("สายพันธุ์")
                            PetDetailField(value = viewModel.petBreed)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            PetDetailLabel("กรุ๊ปเลือด")
                            PetDetailField(value = viewModel.petBloodtype)
                        }
                    }

                    // เพศ + น้ำหนัก
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            PetDetailLabel("เพศ")
                            PetDetailField(value = viewModel.petGender)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            PetDetailLabel("น้ำหนัก (kg)")
                            PetDetailField(value = viewModel.petWeight)
                        }
                    }

                    // ประวัติแพ้/อาหาร
                    PetDetailLabel("ประวัติแพ้/อาหาร")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 80.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = viewModel.petAllergy.ifEmpty { "-" },
                            fontSize = 15.sp,
                            color = if (viewModel.petAllergy.isEmpty()) Color.LightGray else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ปุ่มประวัติการรักษา
                Button(
                    onClick = { navController.navigate("medhistory/$petId/${viewModel.petName}")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        "ประวัติการรักษา",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
@Composable
fun PetDetailLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color.DarkGray,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun PetDetailField(value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = value.ifEmpty { "-" },
            fontSize = 15.sp,
            color = if (value.isEmpty()) Color.LightGray else Color.Black
        )
    }
}

// ---- คำนวณอายุจาก "YYYY-MM-DD" ----
fun calculateAge(birthDateStr: String): String {
    if (birthDateStr.isBlank()) return "-"
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birthDateStr.substring(0, 10), formatter)
        val today     = LocalDate.now()
        val period    = Period.between(birthDate, today)
        when {
            period.years  > 0 -> "${period.years} ปี ${period.months} เดือน"
            period.months > 0 -> "${period.months} เดือน ${period.days} วัน"
            else              -> "${period.days} วัน"
        }
    } catch (e: Exception) { "-" }
}