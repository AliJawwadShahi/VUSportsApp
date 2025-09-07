package com.example.vusportsapp.Coach

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vusportsapp.DataClass.Fontsizes
import com.example.vusportsapp.R
import com.example.vusportsapp.ViewModel.AuthViewModel

@Composable
fun CoachLoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF353A40), Color(0xFF121416)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "Coach Login",
                    fontSize = Fontsizes.title3(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(60.dp))

            // Email
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Email", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    placeholder = { Text("Enter Your Email", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = emailError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (emailError) {
                    Text("Please enter your Email", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Password", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    placeholder = { Text("Enter Your Password", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = passwordError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (passwordError) {
                    Text("Please enter your Password", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Login Button
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        emailError = email.isBlank()
                        passwordError = password.isBlank()

                        if (emailError || passwordError) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            // Firebase check with approval status
                            viewModel.loginCoach(email, password) { success, approved, message ->
                                if (success) {
                                    if (approved) {
                                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_LONG).show()
                                        navController.navigate("coach_dashboard") // Go to Coach Dashboard
                                    } else {
                                        Toast.makeText(context, "Admin approval pending", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}