package com.example.mydiary

import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
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
import java.io.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).build()
        val entryDao = db.entryDao()
        val readFileRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedFile = result.data
                    val fileAddress = selectedFile?.data
                    if(fileAddress!=null){
                        val myFile = contentResolver.openInputStream(fileAddress)
                        if(myFile!=null){
                            val entries = myFile
                                .bufferedReader()
                                .lineSequence()
                                .filter { it.isNotBlank() }
                                .map {line ->
                                    val (date, mood, content) = line.split(',', ignoreCase = false, limit = 3)
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
        val writeFileRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                try{
                    val pfd:ParcelFileDescriptor = contentResolver.openFileDescriptor(it.data?.data!!, "w")!!
                    val fileOutputStream = FileOutputStream(pfd.fileDescriptor)
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            entryDao.getAll().collect{entries ->
                                entries.forEach {entry->
                                    val toOutput = "${entry.dateCreated},${entry.mood},${'"'+entry.content+'"'}\n"
                                    fileOutputStream.write(toOutput.toByteArray())
                                }
                            }
                            fileOutputStream.close()
                            pfd.close()
                        } catch (e: Exception) {
                            println("The flow has thrown an exception: $e")
                        }
                    }
                } catch (e:FileNotFoundException) {
                    e.printStackTrace()
                } catch (e:IOException) {
                    e.printStackTrace()
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

                        composable("settingsPage") { SettingsPage(navController, readFileRequest, writeFileRequest) }
                    }
                }
            }
        }



    }
}
