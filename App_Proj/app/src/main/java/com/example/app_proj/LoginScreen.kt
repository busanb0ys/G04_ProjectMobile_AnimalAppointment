package com.example.app_proj
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: UserViewModel
) {

    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }

    // Set ID
    var Users by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    val loginResult = viewModel.loginResult

    val errorResult = viewModel.errorMessage

    LaunchedEffect(loginResult) {

        loginResult?.let { result ->

            if (!result.error) {

                val username = result.username ?: ""

                // Save login
                sharedPref.saveLoginStatus(
                    isLoggedIn = true,
                    stdId = username,
                    role = result.role ?: ""
                )

                viewModel.getUserProfile(username)

                Toast.makeText(
                    context,
                    "Login Successful",
                    Toast.LENGTH_SHORT
                ).show()

                when (result.role) {

                    "user" -> {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }

                    "doctor" -> {
                        navController.navigate(Screen.Doctor.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
                viewModel.resetLoginResult()
            } else {
                Toast.makeText(
                    context,
                    "Login Fail",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(errorResult) {
        errorResult?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

            viewModel.reseterrorMessage()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Log In",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Student ID
        OutlinedTextField(
            value = Users,
            onValueChange = { Users = it },
            label = { Text("Username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(Users, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account? ")
            TextButton(onClick = {

                navController.navigate(Screen.Register.route)
            }) {
                Text(
                    text = "Register",
                    color = Color(0xFF3F51B5),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}
