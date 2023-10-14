@file:Suppress("FunctionName")

package com.example.mydiary.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "About", color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
            )
        }
    ) { contentPadding ->
        Column (modifier = Modifier.padding(contentPadding).fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Card(
                modifier = Modifier.padding(15.dp).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                        append("Version: \n")
                    }
                    append("1.0.0 \n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                        append("Author: \n")
                    }
                    append("Leo Ring \n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                        append("Project Github: \n")
                    }
                    append("https://github.com/leo030303/Diary \n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                        append("License: \n")
                    }
                    append("GNU General Public License \n")
                }
                )
            }
        }
    }
}
