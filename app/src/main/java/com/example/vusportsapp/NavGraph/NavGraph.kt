package com.example.vusportsapp.NavGraph

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vusportsapp.Admin.AdminDashboardScreen
import com.example.vusportsapp.Admin.AdminLoginScreen

import com.example.vusportsapp.Coach.CoachDashboardScreen
import com.example.vusportsapp.Coach.CoachLoginScreen
import com.example.vusportsapp.Coach.CoachScreen
import com.example.vusportsapp.Participant.AchievementsScreen
import com.example.vusportsapp.Registration.CoachRegisterScreen
import com.example.vusportsapp.Registration.ParticipantReg
import com.example.vusportsapp.RoleSel.RoleSelectionScreen
import com.example.vusportsapp.StartScreen.SplashScreen
import com.example.vusportsapp.Participant.ParticipantLoginScreen
import com.example.vusportsapp.Participant.PastParticipationScreen
import com.example.vusportsapp.Participant.SportsPreferenceScreen
import com.example.vusportsapp.Participant.StudentScreen
import com.example.vusportsapp.ViewModel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    NavHost(navController , startDestination = "start_screen"){
        composable("start_screen"){
            SplashScreen(navController)
        }
        composable("participant_reg"){
            ParticipantReg(navController)
        }
        composable("ach_screen"){ backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            AchievementsScreen(
                db = FirebaseFirestore.getInstance(),
                userId = userId,
                navController
            )
        }
        composable("past_screen"){ backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            PastParticipationScreen(
                db = FirebaseFirestore.getInstance(),
                userId = userId
            )
        }
        composable("student_screen"){
            StudentScreen(navController)
        }
        composable("sport_pref") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SportsPreferenceScreen(
                db = FirebaseFirestore.getInstance(),
                userId = userId,
                navController
            )
        }
        composable("coach_home"){
            CoachScreen()
        }
        composable("stlogin_screen"){
            ParticipantLoginScreen(navController)
        }
        composable("admin_reg"){
            AdminLoginScreen(navController)
        }
        composable("admin_dashboard"){
            AdminDashboardScreen()
        }
        composable("coach_dashboard"){
            CoachDashboardScreen(navController)
        }

        composable("coach_login"){
            CoachLoginScreen(navController)
        }
        composable("coach_reg"){
            CoachRegisterScreen( viewModel = authViewModel,
                onRegister = { navController.navigate("home") },
                navController)
        }
        composable("role_sel"){
            RoleSelectionScreen(onRoleSelected = { role ->
                when (role) {
                    "participant" -> navController.navigate("participant_reg")
                    "coach" -> navController.navigate("coach_reg")
                    "admin" -> navController.navigate("admin_reg")
                }
            })
        }
    }
}