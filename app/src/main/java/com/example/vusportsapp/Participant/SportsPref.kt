package com.example.vusportsapp.Participant

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.vusportsapp.DataClass.Fontsizes
import com.example.vusportsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SportsPreferenceScreen(
    db:FirebaseFirestore = FirebaseFirestore.getInstance(),
    userId: String,
    navController: NavHostController
) {
    val allSports = listOf(
        "Football" to "⚽",
        "Cricket" to "🏏",
        "Basketball" to "🏀",
        "Hockey" to "🏑",
        "Table Tennis" to "🎾",
        "Badminton" to "🏸",
        "Volleyball" to "🏐",
        "Baseball" to "⚾",
        "Swimming" to "🏊",
        "Athletics" to "🏃"
    )

    val categories = listOf("All", "Outdoor", "Indoor", "Team Sports")
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") } // Search bar is empty
    var selectedSports by remember { mutableStateOf(listOf<String>()) } // Tick and untick
    // Skill level
    var skillLevel by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid
    val selectedSkills = remember { mutableStateListOf<String>() }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Choose Your Favorite Sports",
                        fontSize = Fontsizes.title5(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }

            //  Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    placeholder = { Text("Search Sports...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF353A40),
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = Color(0xFF3B82F6),
                        cursorColor = Color(0xFFF18034)
                    )
                )
            }
            // 🏷️ Category Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    color = Color.White
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.Transparent,
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF353A40),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            //  Sports List
            val filteredSports = allSports.filter {
                it.first.contains(
                    searchQuery,
                    ignoreCase = true
                ) && // if i search any text in search query then any single text matching with list will show
                        (selectedCategory == "All" || when (selectedCategory) {
                            "Outdoor" -> it.first in listOf(
                                "Football",
                                "Cricket",
                                "Tennis",
                                "Hockey",
                                "Running"
                            )

                            "Indoor" -> it.first in listOf(
                                "Badminton",
                                "Basketball",
                                "Volleyball",
                                "Swimming"
                            )

                            "Team Sports" -> it.first in listOf(
                                "Football",
                                "Basketball",
                                "Volleyball",
                                "Cricket",
                                "Hockey"
                            )

                            else -> true
                        })
            }


            items(filteredSports) { sport ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedSports.contains(sport.first)) {
                                selectedSports = selectedSports - sport.first
                            } else {
                                if (selectedSports.size < 3) {
                                    selectedSports = selectedSports + sport.first
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "You can only select up to 3 sports!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(sport.second, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(sport.first, fontSize = 18.sp, color = Color.White) //  White text

                    Spacer(modifier = Modifier.weight(1f))

                    if (selectedSports.contains(sport.first)) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFF18034)
                        )
                    }
                }
            }

            // 🎚️ Skill Progress
            item {
                Column {
                    Text("Skill Level", fontWeight = FontWeight.Medium, color = Color.White)
                    Slider(
                        value = skillLevel,
                        onValueChange = { skillLevel = it },
                        modifier = Modifier.fillMaxWidth(),
                        valueRange = 0f..1f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF353A40),
                            activeTrackColor = Color(0xFFF18034),
                            inactiveTrackColor = Color(0xFFFFA05C),
                            activeTickColor = Color(0xFFF18034),
                            inactiveTickColor = Color.Gray
                        )
                    )
                    Text(
                        text = when {
                            skillLevel < 0.25f -> "Beginner"
                            skillLevel < 0.5f -> "Intermediate"
                            skillLevel < 0.75f -> "Advanced"
                            else -> "Expert"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            //  Save Button
            item {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if (userId != null) {
                                val userPreferences = hashMapOf(
                                    "selectedSports" to selectedSports,
                                    "skillLevels" to selectedSkills
                                )

                                db.collection("users").document(userId)
                                    .set(userPreferences, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Preferences Saved",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFF18034))
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}