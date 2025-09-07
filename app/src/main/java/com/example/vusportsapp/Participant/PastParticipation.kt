package com.example.vusportsapp.Participant



import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.tasks.await

@Composable
fun PastParticipationScreen(
    db: FirebaseFirestore,
    userId: String
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showDialog by remember { mutableStateOf(false) }
    val categories = listOf("All", "Sports", "Events", "Competitions")

    // State for user-added participations
    var userParticipations by remember { mutableStateOf(listOf<Tournament>()) }

    // Sample data
    val tournaments = remember {
        listOf(
            Tournament("Football Tournament", "Football", "12 July 2025", "VU Ground", 1, "3-1", true),
            Tournament("Weekly Tournament", "Football", "13 dec 2024", "VU Ground", 0, "0-0", false),
            Tournament("Football league" , "Football" , "20 April 2024" , "VU Ground" , 4 , "2-4" , true)
        )
    }

    // Filter tournaments based on category
    val filteredTournaments = tournaments.filter {
        (selectedCategory == "All" ||
                it.sport == selectedCategory ||
                (selectedCategory == "Sports" && it.sport in listOf(
                    "Football","Cricket","Basketball","Hockey","Tennis",
                    "Badminton","Volleyball","Swimming","Running","Baseball"
                )) ||
                (selectedCategory == "Events" && it.name.contains("Tournament|Cup|Open|Finals".toRegex())) ||
                (selectedCategory == "Competitions" && it.name.contains("League|Championship|Gala|Match".toRegex()))
                ) &&
                it.name.contains(searchQuery, ignoreCase = true)
    } + userParticipations // append user-added participations

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF353A40))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Past Participation",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search events", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, color = Color.White) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF353A40),
                        containerColor = Color.DarkGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Button to add user participation
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedCategory == "All" || selectedCategory == "Sports") {
            Button(
                onClick = { showDialog = true }, // just set state to true
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA05C))
            ) {
                Text("Add Participation", color = Color.White)
            }
        }

// 3️⃣ Show the dialog conditionally
        if (showDialog) {
            AddParticipationDialog(
                onSave = { newTournament ->
                    db.collection("users").document(userId)
                        .collection("participations")
                        .add(newTournament)
                    userParticipations = userParticipations + newTournament
                    showDialog = false // hide dialog after save
                },
                onDismiss = { showDialog = false } // hide dialog if cancelled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable vertical list for tournaments
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredTournaments) { t ->
                TournamentCard(t)
            }
        }
    }
}
// Dialog to add participation
@Composable
fun AddParticipationDialog(
    onSave: (Tournament) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Past Participation") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Event Name") })
                OutlinedTextField(value = sport, onValueChange = { sport = it }, label = { Text("Sport") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue") })
                OutlinedTextField(value = score, onValueChange = { score = it }, label = { Text("Score") })
                OutlinedTextField(value = position.toString(), onValueChange = { position = it.toIntOrNull() ?: 0 }, label = { Text("Position") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val tournament = Tournament(name, sport, date, venue, position, score, participated = position > 0)
                onSave(tournament)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

data class Tournament(
    val name: String,
    val sport: String,
    val date: String,
    val venue: String,
    val position: Int, // 0 = not participated
    val score: String,
    val participated: Boolean
)

@Composable
fun TournamentCard(tournament: Tournament) {
    Card(
        colors = CardDefaults.cardColors(Color.DarkGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(tournament.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${tournament.sport} • ${tournament.date}", color = Color.Gray, fontSize = 12.sp)
                }
                if(tournament.participated) {
                    Text("Top ${tournament.position}", color = Color(0xFFFFA05C), fontWeight = FontWeight.Bold)
                } else {
                    Text("Not participated", color = Color.Gray, fontSize = 12.sp)
                }
            }

            if(tournament.participated) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Venue: ${tournament.venue}", color = Color.White, fontSize = 12.sp)
                Text("Score: ${tournament.score}", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}