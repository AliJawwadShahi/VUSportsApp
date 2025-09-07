package com.example.vusportsapp.Coach

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.vusportsapp.Admin.UserCard
import com.example.vusportsapp.Admin.updateStatus
import com.example.vusportsapp.ViewModel.Student
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CoachDashboardScreen(navController: NavHostController) {
        val db = FirebaseFirestore.getInstance()
        var students by remember { mutableStateOf<List<Student>>(emptyList()) }
        val context = LocalContext.current

// Load All pending participants
        LaunchedEffect(Unit) {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    students = result.documents.mapNotNull { doc ->
                        doc.toObject(Student::class.java)?.copy(id = doc.id)
                    }.filter { it.status == "pending" }

                }
        }
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
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Coach Dashboard",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFFF18034)
                )
            }
            Spacer(Modifier.height(16.dp))

            Text(
                "Pending Participants", style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF18034)
            )
            LazyColumn {
                items(students) { student ->
                    com.example.vusportsapp.Admin.UserCard(
                        name = student.name,
                        email = student.email,
                        sports = student.sports,
                        category = "",
                        isCoach = false,
                        onApprove = {
                            com.example.vusportsapp.Admin.updateStatus(db, "users", student.id, "approved")
                            students = students.filter { it.id != student.id }
                            Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show()
                        },
                        onReject = {
                            com.example.vusportsapp.Admin.updateStatus(db, "users", student.id, "rejected")
                            students = students.filter { it.id != student.id }
                            Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun UserCard(
        name: String,
        vuId: String,
        email: String,
        sports: String?,
        category: String?,   // Coach ki category
        isCoach: Boolean,    // true if user is a coach
        onApprove: () -> Unit,
        onReject: () -> Unit
    ){
        var showDialog by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF353A40))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row (modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onApprove,
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF353A40))
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF18034))
                        }
                        Button(
                            onClick = onReject,
                            colors = ButtonDefaults.buttonColors(Color(0xFF353A40))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFF18034))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "More Details",
                    color = Color.White,
                    modifier = Modifier
                        .clickable { showDialog = true }
                        .padding(top = 4.dp)
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {showDialog = false},
                title = { Text("User Detail" , color = Color.White) },
                text = {
                    Column {
                        Text("Name: $name" , color = Color.White)
                        Text("VuId: $vuId" , color = Color.White)
                        Text("Email: $email" , color = Color.White)
                        Text("Sports: ${sports ?: "N/A"}" , color = Color.White)

                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog = false } , colors = ButtonDefaults.buttonColors(Color(0xFFF18034)) ) {
                        Text("Close" , color = Color.White)
                    }
                },
                containerColor = Color(0xFF32383E)
            )
        }
    }
    // Firestore status update function
    fun updateStatus(db:FirebaseFirestore , collection: String , id: String , newStatus: String){
        db.collection(collection).document(id).update("status" , newStatus)
    }
