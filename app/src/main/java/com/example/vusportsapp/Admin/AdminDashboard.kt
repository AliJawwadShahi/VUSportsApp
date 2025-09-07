package com.example.vusportsapp.Admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vusportsapp.DataClass.Fontsizes
import com.example.vusportsapp.ViewModel.Coach
import com.example.vusportsapp.ViewModel.Student
import com.google.firebase.firestore.FirebaseFirestore



@Composable
fun AdminDashboardScreen() {
    var coaches by remember { mutableStateOf<List<Coach>>(emptyList()) }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // Load participant data from firebase
    // Coaches
    LaunchedEffect(Unit) {
        db.collection("coaches")
            .get()
            .addOnSuccessListener { result ->
                coaches = result.documents.mapNotNull { doc ->
                    doc.toObject(Coach::class.java)?.copy(id = doc.id)
                }.filter { it.status == "pending" }
            }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF353A40), Color(0xFF121416)
                )
            )
        ))

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
            horizontalArrangement = Arrangement.Center) {
            Text("Admin Dashboard", style = MaterialTheme.typography.headlineLarge , color = Color(0xFFF18034))
        }

        Spacer(Modifier.height(24.dp))

        Text("Pending Coaches", style = MaterialTheme.typography.titleMedium , color = Color(0xFFF18034))
        LazyColumn {
            items(coaches) { coach ->
                UserCard(
                    name = coach.name,
                    email = coach.email,
                    sports = coach.sports,
                    category = coach.category,
                    isCoach = true,
                    onApprove = {
                        updateStatus(db, "coaches", coach.id, "approved")
                        coaches = coaches.filter { it.id != coach.id }
                        Toast.makeText(context , "Approved" , Toast.LENGTH_SHORT).show()
                    },
                    onReject = {
                        updateStatus(db, "coaches", coach.id, "rejected")
                        coaches = coaches.filter { it.id != coach.id }
                        Toast.makeText(context , "Rejected" , Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun UserCard(
    name: String,
    email: String,
    sports: String?,
    category: String?,   // Coach ki category
    isCoach: Boolean,    // true if user is a coach
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
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
                    Text("Email: $email" , color = Color.White)
                    if(isCoach){
                        Text("Category: ${category ?: "N/A"}" , color = Color.White)
                    }
                    else{
                        Text("Sports: ${sports ?: "N/A"}" , color = Color.White)
                    }
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
