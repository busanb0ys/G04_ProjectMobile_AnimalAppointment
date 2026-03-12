package com.example.app_proj

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    petId: String? = null
) {
    val context = LocalContext.current
    val isNewPet = petId == null

    LaunchedEffect(petId) {
        if (!isNewPet && petId != null) viewModel.getPetInfo(petId)
        else viewModel.clearPetForm()
    }

    // ---- Dialog States ----
    var showSaveDialog   by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ---- DatePicker ----
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.petBirthDate
            .takeIf { it.isNotEmpty() }
            ?.let { parseDateToMillis(it) }
            ?: System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.petBirthDate = millisToDateString(it)
                    }
                    showDatePicker = false
                }) { Text("ตกลง") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("ยกเลิก") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // ---- Confirm Save Dialog ----
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("ยืนยันการบันทึก") },
            text  = { Text("ต้องการบันทึกข้อมูลของ \"${viewModel.petName}\" หรือไม่?") },
            confirmButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    if (isNewPet) {
                        viewModel.insertPet(viewModel.currentUserId) {
                            Toast.makeText(context, "บันทึกสำเร็จ!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    } else {
                        viewModel.updatePet(petId!!) {
                            Toast.makeText(context, "บันทึกสำเร็จ!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                }) { Text("ยืนยัน") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("ยกเลิก") }
            }
        )
    }

// ---- Confirm Delete Dialog ----
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ยืนยันการลบ") },
            text  = { Text("ต้องการลบ \"${viewModel.petName}\" ออกจากระบบหรือไม่?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    petId?.let {
                        viewModel.deletePet(it) {
                            Toast.makeText(context, "ลบสำเร็จ", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                }) { Text("ยืนยัน") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("ยกเลิก") }
            }
        )
    }

    // ---- Dropdown options ----
    val speciesOptions = listOf("สุนัข", "แมว", "กระต่าย", "แฮมสเตอร์", "นก", "เต่า", "งู")
    val genderOptions  = listOf("ผู้", "เมีย")

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFF2196F3))
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = if (isNewPet) "เพิ่มสัตว์เลี้ยง" else "Edit Pet",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // รูป placeholder
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🐾", fontSize = 76.sp) }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ชื่อ
                PetFormField(
                    label = "ชื่อสัตว์เลี้ยง",
                    value = viewModel.petName,
                    onValueChange = { viewModel.petName = it }
                )

                // ประเภท (Dropdown) + วันเกิด (DatePicker)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        PetDropdownField(
                            label = "ประเภท",
                            value = viewModel.petSpecies,
                            options = speciesOptions,
                            onValueChange = { viewModel.petSpecies = it }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("วันเกิด", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = formatDisplayDate(viewModel.petBirthDate),
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("DD/MM/YYYY", color = Color.LightGray) },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, null, tint = Color(0xFF2196F3))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedContainerColor = Color.White
                            )
                        )
                    }
                }

                // ---- หาโค้ดส่วนนี้ (สายพันธุ์ + กรุ๊ปเลือด) ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // แก้ไขตรงนี้: เพิ่ม label, value, onValueChange
                        PetFormField(
                            label = "สายพันธุ์",
                            value = viewModel.petBreed,
                            onValueChange = { viewModel.petBreed = it }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        // แก้ไขตรงนี้ด้วยเช่นกัน
                        PetFormField(
                            label = "กรุ๊ปเลือด",
                            value = viewModel.petBloodtype,
                            onValueChange = { viewModel.petBloodtype = it }
                        )
                    }
                }

                // ---- ส่วนถัดไป (น้ำหนัก) ก็ต้องแก้ให้เหมือนกันครับ ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        PetDropdownField(
                            label = "เพศ",
                            value = viewModel.petGender,
                            options = genderOptions,
                            onValueChange = { viewModel.petGender = it }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        // แก้ไขตรงน้ำหนักด้วย
                        PetFormField(
                            label = "น้ำหนัก (kg)",
                            value = viewModel.petWeight,
                            onValueChange = { viewModel.petWeight = it }
                        )
                    }
                }

                // ประวัติแพ้/อาหาร
                Text("ประวัติแพ้/อาหาร", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                OutlinedTextField(
                    value = viewModel.petAllergy,
                    onValueChange = { viewModel.petAllergy = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ปุ่มล่าง
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isNewPet) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(26.dp)
                    ) { Text("ลบสัตว์เลี้ยง", color = Color.White, fontSize = 16.sp) }
                }

                Button(
                    onClick = {
                        if (viewModel.petName.isBlank()) {
                            Toast.makeText(context, "กรุณาใส่ชื่อสัตว์เลี้ยง", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        showSaveDialog = true
                    },
                    enabled = !viewModel.isLoading,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text("บันทึก", color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ---- Dropdown Component ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// ---- Helper Functions ----
fun millisToDateString(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

fun formatDisplayDate(dbDate: String): String {
    if (dbDate.isBlank()) return ""
    return try {
        val parts = dbDate.split("-")
        if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else dbDate
    } catch (e: Exception) { dbDate }
}

fun parseDateToMillis(dbDate: String): Long? {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.parse(dbDate)?.time
    } catch (e: Exception) { null }
}

// ---- PetFormField ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "") {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color.White
            )
        )
    }
}