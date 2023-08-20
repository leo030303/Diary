package com.example.mydiary.pages

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mydiary.EntryDao
import com.example.mydiary.viewModels.NewEntryPageViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryPage(navController: NavController, entryDao: EntryDao, viewModel: NewEntryPageViewModel = viewModel(), entryID:Int?) {
    if(entryID != null && entryID != 0){
        viewModel.updateID(entryID, entryDao)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "New Entry", color = MaterialTheme.colorScheme.primary)
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
            DiaryDatePicker(viewModel = viewModel)
            MoodSelector(viewModel = viewModel)
            DiaryTextInput(viewModel = viewModel)
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    viewModel.saveToDatabase(entryDao)
                    navController.navigateUp()
                }
            )
            {
                Text(text = "Save")
            }
        }
    }
}


@Composable
private fun MoodSelector(viewModel: NewEntryPageViewModel){
    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        MoodButton(viewModel, 1, Color.Cyan, "Very Happy")
        MoodButton(viewModel, 2, Color.Green, "Happy")
        MoodButton(viewModel, 3, Color.Blue, "Neutral")
        MoodButton(viewModel, 4, Color.Yellow, "Sad")
        MoodButton(viewModel, 5, Color.Red, "Very Sad")
    }
}

@Composable
private fun MoodButton(viewModel: NewEntryPageViewModel, moodInt:Int, moodTint:Color, contentDescription:String){
    IconButton(
        onClick = { viewModel.selectMood(moodInt) }
    ){
        Icon(
            imageVector = Icons.Filled.Face,
            contentDescription = contentDescription,
            tint = moodTint,
            modifier = Modifier.size(80.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryTextInput(viewModel: NewEntryPageViewModel){
    OutlinedTextField(
        modifier = Modifier.padding(10.dp).fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = 50.dp, max = 300.dp),
        value = viewModel.uiState.collectAsState().value.content,
        onValueChange = { viewModel.updateContent(it) },
        label = { Text("What's Up?") }
    )
}

@Composable
private fun DiaryDatePicker(viewModel: NewEntryPageViewModel){
    val mContext = LocalContext.current

// Declaring integer values
// for year, month and day
    val m1Year: Int
    val m1Month: Int
    val m1Day: Int

// Initializing a Calendar
    //val mCalendar = viewModel.uiState.collectAsState().value.selectedDate
    val mCalendar = Calendar.getInstance()
    if (mCalendar.get(Calendar.HOUR_OF_DAY) <= 6){
        mCalendar.add(Calendar.DAY_OF_MONTH, -1)
    }

// Fetching current year, month and day
    m1Year = mCalendar.get(Calendar.YEAR)
    m1Month = mCalendar.get(Calendar.MONTH)
    m1Day = mCalendar.get(Calendar.DAY_OF_MONTH)

    if(viewModel.uiState.collectAsState().value.dateString == ""){
        viewModel.updateDate(m1Day, m1Month, m1Year)
    }

// Declaring a string value to
// store date in string format

// Declaring DatePickerDialog and setting
// initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            viewModel.updateDate(mDayOfMonth, mMonth, mYear)
        }, m1Year, m1Month, m1Day
    )

    Column {
        Button(onClick = {
            mDatePickerDialog.show()
        }) {
            Text(text = viewModel.uiState.collectAsState().value.dateString)
        }
    }
}


