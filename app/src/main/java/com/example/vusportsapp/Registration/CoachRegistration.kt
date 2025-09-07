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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.vusportsapp.DataClass.Fontsizes
import com.example.vusportsapp.R
import com.example.vusportsapp.ViewModel.AuthViewModel


@Composable
fun CoachRegisterScreen(viewModel: AuthViewModel, onRegister: () -> Unit , navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    val context = LocalContext.current
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
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
            modifier = Modifier.padding(16.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "Register Your Account",
                    fontSize = Fontsizes.title3(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(60.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Name", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Enter Your Name", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF3F3F46)),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0)),
                    isError = nameError
                )
                if (nameError) {
                Text("Please enter your Name", color = Color.Red, fontSize = 12.sp)
            }

            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Email", color = Color.White,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter Your Email", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF3F3F46)),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0)),
                    isError = emailError
                )
                if (emailError) {
                    Text("Please enter your Email", color = Color.Red, fontSize = 12.sp)
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Password", color = Color.White,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Enter Your Password", color = Color(0xFFC0C0C0)) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF3F3F46)),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFFC0C0C0)),
                    isError = passwordError
                )
                if (passwordError) {
                    Text("Please enter your Password", color = Color.Red, fontSize = 12.sp)
                }

            }
            Spacer(modifier = Modifier.height(24.dp))
            // Dropdown for category
            var expanded by remember { mutableStateOf(false) }
           Column (modifier = Modifier.fillMaxWidth()) {
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
                            expanded = false
                        }, text = { Text(category) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.reg),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        nameError = name.isBlank()
                        emailError = email.isBlank()
                        passwordError = password.isBlank()
                        if (nameError || emailError || passwordError) {
                            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        } else if (selectedCategory.isEmpty()) {
                            Toast.makeText(context, "Please select a sport category", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.registerCoach(
                                name, email, password, category = selectedCategory, context
                            ) { success, message ->
                                if (success) {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    navController.navigate("coach_login") {
                                        popUpTo("coach_login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    })
            }
            Spacer(modifier = Modifier.height(40.dp))
            Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                Text("Already registered?" , fontSize = Fontsizes.title2(), color = Color(0xFFC0C0C0))
                Text("Login" ,  color = Color(0xFFC0C0C0), fontSize = Fontsizes.title2(), modifier = Modifier
                    .clickable {
                        navController.navigate("coach_login")
                    })
            }
        }
    }
}