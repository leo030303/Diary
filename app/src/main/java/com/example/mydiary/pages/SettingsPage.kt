package com.example.mydiary.pages

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.mydiary.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter


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
        Column (modifier = Modifier.padding(contentPadding)) {
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
