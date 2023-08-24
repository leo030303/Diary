package com.example.mydiary.viewModels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiary.Entry
import com.example.mydiary.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class MainPageUIState(
    val entries: List<Entry> = emptyList(),
    val searchBarText: String = "",
    var selectedMonth: Calendar = Calendar.getInstance(),
    var displaySearch: Boolean = false
)

class MainPageViewModel : ViewModel() {
    companion object {

        @Volatile
        private var instance: MainPageViewModel? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MainPageViewModel().also { instance = it }
            }
    }
    private val _uiState = MutableStateFlow(MainPageUIState())
    val uiState: StateFlow<MainPageUIState> = _uiState.asStateFlow()


    fun updateSearchBar(newText: String, entryDao: EntryDao){
        _uiState.update { currentState ->
            currentState.copy(
                searchBarText = newText,
            )
        }
        searchEntries(entryDao = entryDao)
    }

    fun toggleSearch(entryDao: EntryDao){
        _uiState.update { currentState ->
            if(currentState.displaySearch){
                getEntries(entryDao)
            }
            currentState.copy(
                displaySearch = !currentState.displaySearch
            )
        }
    }

    fun incrementSelectedMonth(entryDao: EntryDao){
        _uiState.update { currentState ->
            currentState.copy(
                selectedMonth = Calendar.Builder()
                    .set(Calendar.YEAR, currentState.selectedMonth.get(Calendar.YEAR))
                    .set(Calendar.MONTH, currentState.selectedMonth.get(Calendar.MONTH)+1)
                    .build()
            )
        }
        getEntries(entryDao)
    }
    fun setSelectedMonth(newMonth: Int, newYear: Int, entryDao: EntryDao){
        _uiState.update { currentState ->
            currentState.copy(
                selectedMonth = Calendar.Builder()
                    .set(Calendar.YEAR, newYear)
                    .set(Calendar.MONTH, newMonth)
                    .build()
            )
        }
        getEntries(entryDao)
    }

    fun decrementSelectedMonth(entryDao: EntryDao){
        _uiState.update { currentState ->
            currentState.copy(
                selectedMonth = Calendar.Builder()
                    .set(Calendar.YEAR, currentState.selectedMonth.get(Calendar.YEAR))
                    .set(Calendar.MONTH, currentState.selectedMonth.get(Calendar.MONTH)-1)
                    .build()
            )
        }
        getEntries(entryDao)
    }

    fun getEntries(entryDao: EntryDao){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val testMonthMillis : Long = Calendar.Builder()
                    .set(Calendar.YEAR, uiState.value.selectedMonth.get(Calendar.YEAR))
                    .set(Calendar.MONTH, uiState.value.selectedMonth.get(Calendar.MONTH))
                    .set(Calendar.DAY_OF_MONTH, 1)
                    .build().timeInMillis
                val nextMonthMillis : Long = Calendar.Builder()
                    .set(Calendar.YEAR, uiState.value.selectedMonth.get(Calendar.YEAR))
                    .set(Calendar.MONTH, uiState.value.selectedMonth.get(Calendar.MONTH)+1)
                    .set(Calendar.DAY_OF_MONTH, 1)
                    .build().timeInMillis
                entryDao.loadByMonthAndYear(testMonthMillis, nextMonthMillis).distinctUntilChanged().collect { entriesValue ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            entries = entriesValue,
                        )
                    }
                }
            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }
    private fun searchEntries(entryDao: EntryDao){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (uiState.value.searchBarText == ""){
                    getEntries(entryDao)
                }else{
                    entryDao.search("%${uiState.value.searchBarText}%").distinctUntilChanged().collect { entriesValue ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                entries = entriesValue,
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                println("The flow has thrown an exception: $e")
            }
        }
    }
}


