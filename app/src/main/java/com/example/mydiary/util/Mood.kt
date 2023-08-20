package com.example.mydiary.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Mood(val icon: ImageVector, val color: Color, val title: String, val value: Int) {
    AWESOME(Icons.Filled.Face, Color.Cyan, "Awesome", 5),
    GOOD(Icons.Filled.Face, Color.Green, "Good", 4),
    OKAY(Icons.Filled.Face, Color.Blue, "Okay", 3),
    BAD(Icons.Filled.Face, Color.Yellow, "Bad", 2),
    TERRIBLE(Icons.Filled.Face, Color.Red, "Awful", 1)
}