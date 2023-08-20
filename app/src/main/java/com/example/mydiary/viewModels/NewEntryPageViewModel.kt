package com.example.mydiary.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiary.Entry
import com.example.mydiary.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar

data class NewEntryPageUIState(
    val content: String = "",
    val mood: Int = 0,
    val dateString: String = "",
    val entryID: Int = 0
)

class NewEntryPageViewModel : ViewModel() {
    // Expose screen UI state
    private val _uiState = MutableStateFlow(NewEntryPageUIState())
    val uiState: StateFlow<NewEntryPageUIState> = _uiState.asStateFlow()
    private var selectedDate:Calendar = Calendar.getInstance()

    fun updateContent(newContent: String) {
        _uiState.update { currentState ->
            currentState.copy(
                content = newContent
            )
        }
    }

    fun updateID(newID: Int, entryDao: EntryDao) {
        viewModelScope.launch(Dispatchers.IO) {
            val entryItem = entryDao.getEntryByID(newID)
            selectedDate.timeInMillis = entryItem.dateCreated
            _uiState.update { currentState ->
                currentState.copy(
                    entryID = newID,
                    content = entryItem.content,
                    mood = entryItem.mood,
                    dateString = "${selectedDate.get(Calendar.DAY_OF_MONTH)}/${selectedDate.get(Calendar.MONTH)+1}/${selectedDate.get(Calendar.YEAR)}"
                )
            }
        }

    }

    fun updateDate(newDay: Int, newMonth: Int, newYear: Int) {
        selectedDate = Calendar.Builder()
            .set(Calendar.YEAR, newYear)
            .set(Calendar.MONTH, newMonth)
            .set(Calendar.DAY_OF_MONTH, newDay)
            .build()
        _uiState.update { currentState ->
            currentState.copy(
                dateString = "${selectedDate.get(Calendar.DAY_OF_MONTH)}/${selectedDate.get(Calendar.MONTH)+1}/${selectedDate.get(Calendar.YEAR)}"
            )
        }
    }

    // Handle business logic
    fun selectMood(mood: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                mood = mood,
            )
        }
    }

    fun saveToDatabase(entryDao: EntryDao){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(uiState.value.entryID != 0){
                    entryDao.update(Entry(eid = uiState.value.entryID, content = uiState.value.content, dateCreated = selectedDate.toInstant().toEpochMilli(), mood = uiState.value.mood))
                } else{
                    entryDao.insert(Entry(eid = 0, content = uiState.value.content, dateCreated = selectedDate.toInstant().toEpochMilli(), mood = uiState.value.mood))
                }
            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }
}


