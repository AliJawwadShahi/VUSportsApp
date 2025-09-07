package com.example.vusportsapp.Participant

import androidx.compose.runtime.Immutable

@Immutable
data class Achievement(
    val id: String = "",
    val title: String = "",
    val emoji: String = "🔒",
    val description: String = "",
    val target: Int = 0,
    val type: String = "general"
)

@Immutable
data class UserAchievementState(
    val progress: Int = 0,    // current progress (0..target)
    val unlocked: Boolean = false
)
fun commonAchievementsFor(sport: String): List<Achievement> = listOf(
    Achievement(
        id = "${sport}_first_step",
        title = "First Step",
        emoji = "🏆",
        description = "Log your first activity in $sport",
        target = 1,
        type = "first_step"
    ),
    Achievement(
        id = "${sport}_streak_7",
        title = "7-Day Streak",
        emoji = "🔥",
        description = "Be active 7 days in a row for $sport",
        target = 7,
        type = "streak"
    ),
    Achievement(
        id = "${sport}_target_weekly",
        title = "Target Reached",
        emoji = "🎯",
        description = "Complete your weekly goal in $sport",
        target = 1,
        type = "target"
    ),
    Achievement(
        id = "${sport}_milestone",
        title = "Milestone",
        emoji = "💪",
        description = "Hit a key milestone in $sport (e.g., 100 reps / 10km)",
        target = 100,
        type = "milestone"
    ),
    Achievement(
        id = "${sport}_streak_30",
        title = "30-Day Streak",
        emoji = "🕒",
        description = "Stay consistent for 30 days in $sport",
        target = 30,
        type = "streak"
    )
)