@file:Suppress("FunctionName")

package com.example.mydiary.pages

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

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
                }
            )
        }
    ) { contentPadding ->
        Column (modifier = Modifier.padding(contentPadding).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                filePickerCallback.launch(intent)}){
                Text("Import from CSV")
            }
            Button(onClick = { val intent = Intent()
                .setType("text/csv")
                .setAction(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_TITLE, "diary_entries.csv")
                fileWriterCallback.launch(intent)}){
                Text("Export to CSV")
            }
        }
    }
}
