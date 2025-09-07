package com.example.vusportsapp.StartScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vusportsapp.DataClass.Fontsizes
import com.example.vusportsapp.R

@Composable
fun SplashScreen(navController: NavHostController){
    Box(modifier = Modifier.fillMaxSize()
        .background(brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF353A40) , Color(0xFF121416)
            )
        ))){
        Column (modifier = Modifier.fillMaxSize()
            .padding(12.dp)){
            Column  (modifier = Modifier.fillMaxWidth()
                .padding(top = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center){
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("VU Sports App",
                    fontSize = Fontsizes.title1(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Here To Compete.",
                    fontSize = Fontsizes.title2(),
                    color = Color(0xFFC0C0C0)
                )
            }

        }
        Box (modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter){
            Image(
                painter = painterResource(id = R.drawable.started),
                contentDescription = null,
                modifier = Modifier.offset(y  =  -46.dp)
                    .height(50.dp).width(500.dp)
                    .clickable {
                        navController.navigate("role_sel")
                    }
            )
        }
    }
}