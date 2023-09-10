package com.example.mydiary.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Face
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Mood(val icon: ImageVector, val selectedIcon: ImageVector, val color: Color, val title: String, val barPercentage: Int, val value: Int) {
    TERRIBLE(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 217, green = 23, blue = 14), "Awful", 20, 0),
    BAD(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 246, green = 138, blue = 35), "Bad", 40, 1),
    OKAY(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 248, green = 219, blue = 1), "Okay", 60, 2),
    GOOD(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 52, green = 178, blue = 107), "Good", 80, 3),
    AWESOME(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 0, green = 89, blue = 190), "Awesome", 100, 4)
}
