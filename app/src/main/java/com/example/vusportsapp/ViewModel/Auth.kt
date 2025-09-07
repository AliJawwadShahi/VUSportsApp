package com.example.vusportsapp.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vusportsapp.Participant.Achievement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun registerParticipant(
        name: String,
        vuId: String,
        email: String,
        password: String,
        sports: String,
        achievements: String,
        selectedCategory: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // Step 1: Check if any approved coach exists for this category
        db.collection("coaches")
            .whereEqualTo("category", selectedCategory)
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult(false, "No coach available for $selectedCategory")
                } else {
                    // Step 2: Firebase Authentication mai user create karo
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = task.result?.user?.uid
                                if (uid != null) {
                                    // Step 3: Firestore mai participant data save karo
                                    val participant = hashMapOf(
                                        "name" to name,
                                        "vuId" to vuId,
                                        "email" to email,
                                        "sports" to sports,
                                        "achievements" to achievements,
                                        "category" to selectedCategory,
                                        "status" to "pending", // coach approval required
                                        "role" to "student"
                                    )

                                    db.collection("users")
                                        .document(uid) // ✅ userId = Firebase Auth UID
                                        .set(participant)
                                        .addOnSuccessListener {
                                            onResult(true, "Registration request sent to Coach")
                                        }
                                        .addOnFailureListener { e ->
                                            onResult(false, "Error saving user: ${e.message}")
                                        }
                                }
                            } else {
                                onResult(false, "Auth Error: ${task.exception?.message}")
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, "Error: ${e.message}")
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                    val status = doc.getString("status")
                    if (status == "approved") {
                        onResult(true, "Login Successful")
                    } else {
                        auth.signOut()
                        onResult(false, "Account pending approval by Coach")
                    }
                }
            } else {
                onResult(false, "Login Failed: ${task.exception?.message}")
            }
        }
    }

    fun registerCoach(
        name: String,
        email: String,
        password: String,
        category: String,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // ✅ Step 1: Create auth user (so that login details ban jaen)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: ""

                // ✅ Step 2: Save data in Firestore
                val coach = hashMapOf(
                    "id" to userId,
                    "name" to name,
                    "email" to email,
                    "category" to category,
                    "status" to "pending"
                )

                firestore.collection("coaches")
                    .document(userId) // userId se save karenge taake unique ho
                    .set(coach)
                    .addOnSuccessListener {
                        // ✅ Step 3: Signout user
                        auth.signOut()

                        // ✅ Step 4: Success message
                        onResult(
                            true,
                            "Your registration request has been sent to Admin. Please wait for approval."
                        )
                    }
                    .addOnFailureListener {
                        onResult(false, "Failed to save coach data: ${it.message}")
                    }
            }
            .addOnFailureListener {
                onResult(false, "Failed to register: ${it.message}")
            }
    }

    fun loginParticipant(
        email: String,
        password: String,
        onResult: (Boolean, Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                val status = doc.getString("status") ?: "pending"
                                if (status == "approved") {
                                    onResult(true, true, "Login success")
                                } else {
                                    onResult(false, false, "Your account is not approved yet")
                                }
                            } else {
                                onResult(false, false, "Student data not found")
                            }
                        }
                        .addOnFailureListener {
                            onResult(false, false, "Error: ${it.message}")
                        }
                } else {
                    onResult(false, false, "UID not found")
                }
            } else {
                onResult(false, false, task.exception?.message ?: "Login failed")
            }
        }
    }


    fun loginAdmin(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onResult(true, user?.email)  // login success
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
    fun loginCoach(
        email: String,
        password: String,
        onResult: (Boolean, Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    db.collection("coaches").document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                val approved = doc.getBoolean("approved") ?: false
                                onResult(true, approved, "Login success")
                            } else {
                                onResult(false, false, "Coach data not found")
                            }
                        }
                        .addOnFailureListener {
                            onResult(false, false, "Error: ${it.message}")
                        }
                } else {
                    onResult(false, false, "UID not found")
                }
            } else {
                onResult(false, false, task.exception?.message ?: "Login failed")
            }
        }
    }
    fun fetchAdminData(
        onResult: (List<Student>, List<Coach>) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        // Student list
        db.collection("users")
            .get()
            .addOnSuccessListener { studentResult ->
                val students = studentResult.documents.mapNotNull { doc ->
                    doc.toObject(Student::class.java)?.copy(id = doc.id)
                }

                // Coach list
                db.collection("coaches")
                    .get()
                    .addOnSuccessListener { coachResult ->
                        val coaches = coachResult.documents.mapNotNull { doc ->
                            doc.toObject(Coach::class.java)?.copy(id = doc.id)
                        }

                        // ✅ Dono lists return kardo
                        onResult(students, coaches)
                    }
            }
    }
    fun updateParticipantProfile(
        db: FirebaseFirestore,
        participantId: String,
        sportsPreferences: String,
        pastParticipation: String,
        achievements: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val updates = mapOf(
            "sportsPreferences" to sportsPreferences,
            "pastParticipation" to pastParticipation,
            "achievements" to achievements
        )

        db.collection("users")
            .document(participantId)
            .update(updates)
            .addOnSuccessListener {
                onResult(true, "Profile updated successfully")
            }
            .addOnFailureListener { e ->
                onResult(false, "Error: ${e.message}")
            }
    }

}
data class Student(
    val id: String = "",          // Firebase document id (UID)
    val name: String = "",
    val vuId: String = "",
    val email: String = "",
    val sports: String = "",
    val achievements: String = "",
    val approved: Boolean = false,
    val coachId: String = "", // Kis coach k under assign hua
    val status: String = "pending"
)
data class Coach(
    val id: String = "",
    val name : String = "",
    val email: String = "",
    val sports: String = "",
    val status: String = "pending",
    val category:String = ""
)
