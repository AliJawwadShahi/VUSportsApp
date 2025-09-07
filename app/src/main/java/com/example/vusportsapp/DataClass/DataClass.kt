package com.example.vusportsapp.DataClass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object Fontsizes{
    @Composable
    fun title1(): TextUnit {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when{
            screenWidth < 360 -> 20.sp
            screenWidth < 440 -> 30.sp
            screenWidth < 500 -> 40.sp
            else  -> 50.sp
        }
    }
    @Composable
    fun title2(): TextUnit {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when{
            screenWidth < 360 -> 14.sp
            screenWidth < 440 -> 16.sp
            screenWidth < 500 -> 18.sp
            else  -> 22.sp
        }
    }
    @Composable
    fun title3(): TextUnit {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when{
            screenWidth < 360 -> 15.sp
            screenWidth < 440 -> 25.sp
            screenWidth < 500 -> 30.sp
            else  -> 35.sp
        }
    }
    @Composable
    fun title4(): TextUnit {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when{
            screenWidth < 360 -> 12.sp
            screenWidth < 440 -> 14.sp
            screenWidth < 500 -> 16.sp
            else  -> 20.sp
        }
    }
    @Composable
    fun title5(): TextUnit {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when{
            screenWidth < 360 -> 20.sp
            screenWidth < 440 -> 22.sp
            screenWidth < 500 -> 26.sp
            else  -> 30.sp
        }
    }
}
