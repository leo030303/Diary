@file:Suppress("FunctionName")

package com.example.mydiary.pages

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController, filePickerCallback: ActivityResultLauncher<Intent>, fileWriterCallback: ActivityResultLauncher<Intent>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings", color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("aboutPage") }){
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "About Page"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column (modifier = Modifier.padding(contentPadding).fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Manage Data")
            Button(onClick = {val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                filePickerCallback.launch(intent)}){
                Text("Import from CSV")
            }
            Button(onClick = {
                val todayCal = Calendar.getInstance()
                val monthName:String = todayCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH)!!
                val fileName:String = "diary_entries_"+todayCal.get(Calendar.DAY_OF_MONTH)+"_"+monthName+"_"+todayCal.get(Calendar.YEAR)+".csv"
                val intent = Intent()
                .setType("text/csv")
                .setAction(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, fileName)
                fileWriterCallback.launch(intent)
            }){
                Text("Export to CSV")
            }
        }
    }
}
