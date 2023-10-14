package com.example.mydiary.util


import androidx.compose.ui.graphics.Color
import com.example.mydiary.R

enum class Mood(val iconResourceID: Int, val selectedIconResourceID: Int, val color: Color, val title: String, val barPercentage: Int, val value: Int) {
    TERRIBLE(R.drawable.outline_sentiment_very_dissatisfied_24, R.drawable.twotone_sentiment_very_dissatisfied_24, Color(red = 217, green = 23, blue = 14), "Awful", 20, 0),
    BAD(R.drawable.outline_sentiment_dissatisfied_24, R.drawable.twotone_sentiment_dissatisfied_24, Color(red = 246, green = 138, blue = 35), "Bad", 40, 1),
    OKAY(R.drawable.outline_sentiment_neutral_24, R.drawable.twotone_sentiment_neutral_24, Color(red = 248, green = 219, blue = 1), "Okay", 60, 2),
    GOOD(R.drawable.outline_sentiment_satisfied_alt_24, R.drawable.twotone_sentiment_satisfied_24, Color(red = 120, green = 190, blue = 50), "Good", 80, 3),
    AWESOME(R.drawable.outline_sentiment_very_satisfied_24, R.drawable.twotone_sentiment_very_satisfied_24, Color(red = 52, green = 178, blue = 150), "Awesome", 100, 4)
}
