package com.example.mydiary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.mydiary.pages.MainPage
import com.example.mydiary.pages.NewEntryPage
import com.example.mydiary.pages.SettingsPage
import com.example.mydiary.ui.theme.MyDiaryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).build()
        val entryDao = db.entryDao()
        val previewRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val selectedFile = it.data
                    val fileAddress = selectedFile?.data
                    if(fileAddress!=null){
                        val myFile = contentResolver.openInputStream(fileAddress)
                        if(myFile!=null){
                            val entries = myFile
                                .bufferedReader()
                                .lineSequence()
                                .filter { it.isNotBlank() }
                                .map {
                                    val (date, mood, content) = it.split(',', ignoreCase = false, limit = 3)
                                    var newContent = content
                                    if (content!= ""){
                                        if (content[0] == '"'){
                                            newContent = content.substring(1, content.length - 1)
                                        }
                                    }
                                    Entry(eid = 0, content = newContent, dateCreated = date.toFloat().toLong(), mood = mood.toInt())
                                }
                                .toList()
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    entries.forEach {item ->
                                        Log.d("mema", item.dateCreated.toString())
                                        entryDao.insert(item)
                                    }
                                } catch (e: Exception) {
                                    println("The flow has thrown an exception: $e")
                                }
                            }
                            myFile.close()
                        }
                    }

                }
            }
        setContent {
            val navController = rememberNavController()
            MyDiaryTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "mainPage") {
                        composable("mainPage") { MainPage(navController, entryDao) }
                        composable(
                            "newEntryPage/{entryID}",
                            arguments = listOf(navArgument("entryID") { type = NavType.IntType })
                        ) {backStackEntry ->
                            NewEntryPage(navController, entryDao, entryID = backStackEntry.arguments?.getInt("entryID"))
                        }

                        composable("settingsPage") { SettingsPage(navController, previewRequest) }
                    }
                }
            }
        }



    }
}
