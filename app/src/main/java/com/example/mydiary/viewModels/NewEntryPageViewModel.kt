package com.example.mydiary.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mydiary.Entry
import com.example.mydiary.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class NewEntryPageUIState(
    val content: String = "",
    val mood: Int = 2,
    val dateString: String = "",
    val entryID: Int = 0,
    val showConfirmationPopup: Boolean = false,
    val selectedDate:Calendar = Calendar.getInstance(),
)

class NewEntryPageViewModel : ViewModel() {
    // Expose screen UI state
    private val _uiState = MutableStateFlow(NewEntryPageUIState())
    val uiState: StateFlow<NewEntryPageUIState> = _uiState.asStateFlow()

    fun updateContent(newContent: String) {
        _uiState.update { currentState ->
            currentState.copy(
                content = newContent
            )
        }
    }

    fun toggleConfirmation(){
        _uiState.update { currentState ->
            currentState.copy(
                showConfirmationPopup = !currentState.showConfirmationPopup
            )
        }
    }

    fun resetData(){
        _uiState.update { currentState ->
            currentState.copy(
                content = "",
                mood = 2,
                dateString = "",
                entryID = 0,
                showConfirmationPopup = false,
                selectedDate = Calendar.getInstance(),
            )
        }
    }

    fun exitPage(navController: NavController){
        navController.navigateUp()
        resetData()
    }

    fun updateID(newID: Int, entryDao: EntryDao) {
        viewModelScope.launch(Dispatchers.IO) {
            val entryItem = entryDao.getEntryByID(newID)
            if(entryItem!=null){
                val newDate = Calendar.Builder().setInstant(entryItem.dateCreated).build()
                _uiState.update { currentState ->
                    currentState.copy(
                        entryID = newID,
                        content = entryItem.content,
                        mood = entryItem.mood,
                        selectedDate = newDate,
                        dateString = "${newDate.get(Calendar.DAY_OF_MONTH)}/${newDate.get(Calendar.MONTH)+1}/${newDate.get(Calendar.YEAR)}",
                    )
                }
            }
        }
    }

    fun updateDate(newDay: Int, newMonth: Int, newYear: Int) {
        val newDate = Calendar.Builder()
            .set(Calendar.YEAR, newYear)
            .set(Calendar.MONTH, newMonth)
            .set(Calendar.DAY_OF_MONTH, newDay)
            .build()
        _uiState.update { currentState ->
            currentState.copy(
                selectedDate = newDate,
                dateString = "${newDate.get(Calendar.DAY_OF_MONTH)}/${newDate.get(Calendar.MONTH)+1}/${newDate.get(Calendar.YEAR)}"
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

    fun deleteEntry(entryDao: EntryDao){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(uiState.value.entryID != 0){
                    entryDao.delete(
                        Entry(
                            eid= uiState.value.entryID,
                            content = uiState.value.content,
                            dateCreated = uiState.value.selectedDate.toInstant().toEpochMilli(),
                            mood = uiState.value.mood
                        )
                    )
                }
                resetData()
            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }

    fun saveToDatabase(entryDao: EntryDao){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(uiState.value.entryID != 0){
                    entryDao.update(Entry(eid = uiState.value.entryID, content = uiState.value.content, dateCreated = uiState.value.selectedDate.toInstant().toEpochMilli(), mood = uiState.value.mood))
                } else{
                    entryDao.insert(Entry(eid = 0, content = uiState.value.content, dateCreated = uiState.value.selectedDate.toInstant().toEpochMilli(), mood = uiState.value.mood))
                }
            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }
}


