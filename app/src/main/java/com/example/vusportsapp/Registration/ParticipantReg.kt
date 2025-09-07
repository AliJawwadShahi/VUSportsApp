package com.example.vusportsapp.Registration

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vusportsapp.DataClass.Fontsizes
import com.example.vusportsapp.R
import com.example.vusportsapp.ViewModel.AuthViewModel


@Composable
fun ParticipantReg(navController: NavController, viewModel: AuthViewModel = viewModel()){
    var VuId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf("") }
    var achievements by remember { mutableStateOf("") }
    val context = LocalContext.current
    var nameError by remember { mutableStateOf(false) }
    var vuIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var sportsError by remember { mutableStateOf(false) }
    var achievementsError by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val sportsCategories = listOf(
        "Cricket", "Football", "Hockey", "Tennis", "Badminton",
        "Swimming", "Athletics", "Basketball", "Volleyball", "Table Tennis"
    )
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
            Spacer(modifier = Modifier.height(40.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    })
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    "Register To Your Account",
                    fontSize = Fontsizes.title3(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(60.dp))

            // Name
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Name", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    placeholder = { Text("Enter Your Name", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = nameError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (nameError) {
                    Text("Please enter your Name", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // VU ID
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("VU ID", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = VuId,
                    onValueChange = {
                        VuId = it
                        vuIdError = false
                    },
                    placeholder = { Text("Enter Your VU id", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = vuIdError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (vuIdError) {
                    Text("Please enter your VU ID", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = emailError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (emailError) {
                    Text("Please enter your Email", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = passwordError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (passwordError) {
                    Text("Please enter your Password", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sports
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    placeholder = { Text("Select Sport", color = Color(0xFFC0C0C0) , fontSize = Fontsizes.title2()) },
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true }.background(Color(0xFF3F3F46)),
                    enabled = false,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    sportsCategories.forEach { category ->
                        DropdownMenuItem(onClick = {
                            selectedCategory = category
                            sports = category
                            expanded = false
                        }, text = { Text(category) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Achievements
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Achievements", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = achievements,
                    onValueChange = {
                        achievements = it
                        achievementsError = false
                    },
                    placeholder = { Text("Enter Your Achievements", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3F3F46)),
                    singleLine = true,
                    isError = achievementsError,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0))
                )
                if (achievementsError) {
                    Text("Please enter your Achievements", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Register button
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.reg),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        // Validation check
                        nameError = name.isBlank()
                        vuIdError = VuId.isBlank()
                        emailError = email.isBlank()
                        passwordError = password.isBlank()
                        sportsError = selectedCategory.isBlank()
                        achievementsError = achievements.isBlank()

                        if (nameError || vuIdError || emailError || passwordError || sportsError || achievementsError) {
                            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()

                        }
                        else if (selectedCategory.isEmpty()) {
                            Toast.makeText(context, "Please select a sport category", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            // Call Firebase register only when valid
                            viewModel.registerParticipant(
                                name, VuId, email, password, sports, achievements,selectedCategory
                            ) { success, message ->
                                if (success) {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    navController.navigate("stlogin_screen")
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column (modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Text("Already Registered? " , color = Color.White , fontSize = Fontsizes.title2())
                Text("Login" , color = Color.White, fontSize = Fontsizes.title2(),
                    modifier = Modifier.clickable {
                        navController.navigate("stlogin_screen")
                    })
            }
        }
    }
    }
