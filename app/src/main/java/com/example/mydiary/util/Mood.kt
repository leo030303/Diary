package com.example.mydiary.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Face
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Mood(val icon: ImageVector, val selectedIcon: ImageVector, val color: Color, val title: String, val value: Int) {
    TERRIBLE(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 255, green = 150, blue = 0), "Awful", 0),
    BAD(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 255, green = 220, blue = 0), "Bad", 1),
    OKAY(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 180, green = 255, blue = 0), "Okay", 2),
    GOOD(Icons.Outlined.Face, Icons.Filled.Face, Color(red = 30, green = 255, blue = 80), "Good", 3),
    AWESOME(Icons.Outlined.Face, Icons.Filled.Face, Color.Cyan, "Awesome", 4)
}
