

import AchievementRepository.getAchievements
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vusportsapp.Participant.Achievement
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AchievementRepository {

    // Fetch Achievements
    suspend fun getAchievements(userId: String): List<Achievement> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("achievements")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            Achievement(
                id = doc.getString("id") ?: "",
                title = doc.getString("title") ?: "",
                emoji = doc.getString("emoji") ?: "🔒",
                description = doc.getString("description") ?: "",
                target = (doc.getLong("target") ?: 0L).toInt(),
                type = doc.getString("type") ?: "general"
            )
        }
    }

    // Unlock Achievement
    suspend fun unlockAchievement(userId: String, achievement: Achievement) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("achievements")
            .document(achievement.id)
            .set(achievement.copy(emoji = "✅")) // mark unlocked
            .await()
    }
}

class AchievementViewModel : ViewModel() {

    var achievements by mutableStateOf<List<Achievement>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun loadAchievements(userId: String) {
        viewModelScope.launch {
            isLoading = true
            achievements = getAchievements(userId)
            isLoading = false
        }
    }
}