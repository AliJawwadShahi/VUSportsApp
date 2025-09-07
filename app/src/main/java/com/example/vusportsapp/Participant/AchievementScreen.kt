package com.example.vusportsapp.Participant


import AchievementViewModel
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vusportsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.tasks.await


@Composable
fun AchievementsScreen(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    userId: String,
    navController: NavHostController
) {
    val sports = listOf(
        "Football", "Cricket", "Basketball", "Hockey", "Tennis",
        "Badminton", "Volleyball", "Baseball", "Swimming", "Athletics"
    )


    var selectedSport by remember { mutableStateOf(sports.first()) }

    // user state map: achievementId -> UserAchievementState
    var userStates by remember { mutableStateOf<Map<String, UserAchievementState>>(emptyMap()) }

    val achievements = remember(selectedSport) { commonAchievementsFor(selectedSport) }

    // load user states for selected sport
    LaunchedEffect(userId, selectedSport) {
        try {
            val snap = db.collection("users")
                .document(userId)
                .collection("achievements")
                .document(selectedSport)
                .get()
                .await()

            @Suppress("UNCHECKED_CAST")
            val raw = (snap.data ?: emptyMap<String, Any>()) as Map<String, Map<String, Any>>
            val mapped = raw.mapValues { (_, v) ->
                UserAchievementState(
                    progress = (v["progress"] as? Long ?: 0L).toInt(),
                    unlocked = v["unlocked"] as? Boolean ?: false
                )
            }
            userStates = mapped
        } catch (_: Exception) {
            // ignore; keep empty
        }
    }

    // background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF353A40), Color(0xFF121416))
                )
            )
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                })

                Text(
                    "Achievements",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF18034)
                )
        }

        Spacer(Modifier.height(12.dp))

        // Sport chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sports.size) { idx ->
                val sport = sports[idx]
                FilterChip(
                    selected = selectedSport == sport,
                    onClick = { selectedSport = sport },
                    label = { Text(sport, color = Color.White) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        labelColor = Color.White,
                        selectedContainerColor = Color(0xFF353A40),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(achievements, key = { it.id }) { ach ->
                val state = userStates[ach.id] ?: UserAchievementState()
                AchievementCard(
                    achievement = ach,
                    userState = state,
                    onIncrement = { inc ->
                        // demo: increment progress or toggle unlock if target reached
                        val newProgress = (state.progress + inc).coerceAtMost(ach.target)
                        val newUnlocked = newProgress >= ach.target || state.unlocked
                        val updated = state.copy(progress = newProgress, unlocked = newUnlocked)

                        userStates = userStates.toMutableMap().apply { put(ach.id, updated) }

                        // save to Firestore (merge)
                        val payload = mapOf(
                            ach.id to mapOf(
                                "progress" to updated.progress,
                                "unlocked" to updated.unlocked
                            )
                        )
                        db.collection("users")
                            .document(userId)
                            .collection("achievements")
                            .document(selectedSport)
                            .set(payload, SetOptions.merge())

                        // Optional: toast feedback can be added in your Activity if needed
                    }
                )
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    userState: UserAchievementState,
    onIncrement: (Int) -> Unit
) {
    val unlocked = userState.unlocked
    val progressFraction by animateFloatAsState(
        targetValue = if (achievement.target == 0) 1f else userState.progress.toFloat() / achievement.target.toFloat(),
        animationSpec = tween(500),
        label = "progress"
    )
    val scale by animateFloatAsState(
        targetValue = if (unlocked) 1.03f else 1f,
        animationSpec = tween(400),
        label = "scale"
    )

    val bgGradient = if (unlocked) {
        Brush.linearGradient(listOf(Color(0xFFF7971E), Color(0xFFFFD200)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF2C3036), Color(0xFF1E2226)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                // demo action: +1 progress per tap
//                onIncrement(1)
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(bgGradient)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(achievement.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        achievement.title,
                        color = if (unlocked) Color.Black else Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        achievement.description,
                        color = if (unlocked) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
                if (unlocked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1A6E1A),
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar (animated)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (unlocked) Color(0xFF0E4D0E) else Color(0xFF3A3F45)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (unlocked) Color(0xFF34C759) else Color(0xFFF18034)
                        )
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${userState.progress}/${achievement.target}",
                    color = if (unlocked) Color.Black else Color.White,
                    fontSize = 12.sp
                )
                Text(
                    if (unlocked) "Unlocked" else "Locked",
                    color = if (unlocked) Color.Black else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}