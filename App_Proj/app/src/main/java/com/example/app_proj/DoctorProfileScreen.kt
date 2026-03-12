package com.example.app_proj

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun DoctorProfileScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    userId:String
) {
    // 1. Get the current backstack entry as state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // 2. Extract the route string
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, currentRoute) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Blue Header with Back and Logout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = Color(0xFF2196F3),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                }

                IconButton(
                    onClick = { showLogoutDialog = true }, // Open the dialog
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Log Out",
                        tint = Color(0xFF2196F3)
                    )
                }
            }

            // 2. Profile Image Section
            Spacer(modifier = Modifier.height(20.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                Card(
                    shape = RectangleShape,
                    border = BorderStroke(2.dp, Color.Black),
                    modifier = Modifier.size(150.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 8.dp, y = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. User Info Card (The Gray Box)
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ข้อมูลส่วนตัว", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    // BIND TO VIEWMODEL
                    DProfileTextField(
                        label = "ชื่อ-สกุล:",
                        value = viewModel.fullName,
                        onValueChange = { viewModel.fullName = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DProfileTextField(
                        label = "ชื่อ-เล่น:",
                        value = viewModel.nickname,
                        onValueChange = { viewModel.nickname = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DProfileTextField(
                        label = "เบอร์โทรศัพท์:",
                        value = viewModel.phone,
                        onValueChange = { viewModel.phone = it }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            viewModel.updateUser(userId) {
                                Toast.makeText(context, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Doctor.route)
                            }
                        },
                        enabled = !viewModel.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                        modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("บันทึก", color = Color.White, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Log Out") },
                text = { Text("ต้องการออกจากระบบ?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.logout {
                            showLogoutDialog = false
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }) {
                        Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}