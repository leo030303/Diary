package com.example.mydiary.viewModels

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiary.Entry
import com.example.mydiary.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

const val CREATE_FILE = 1
const val PICK_CSV_FILE = 2
class  SettingsPageViewModel: ViewModel() {
    // Request code for creating a PDF document.
    private fun createFile(pickerInitialUri: Uri) {
        /*val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/csv"
            putExtra(Intent.EXTRA_TITLE, "diaryData.csv")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)*/
    }



    // Request code for selecting a PDF document.


    fun openFile(pickerInitialUri: Uri) {
        /*val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/csv"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, PICK_CSV_FILE)*/
    }


    fun importFromCSV(entryDao: EntryDao) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                entryDao.insert(Entry(eid = 0, content = "", dateCreated = 1, mood = 1))
            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }
    fun exportToCSV(entryDao: EntryDao) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                entryDao.getAll().distinctUntilChanged().collect { entriesValue ->

                }
            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }
}