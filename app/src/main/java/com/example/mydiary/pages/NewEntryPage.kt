@file:Suppress("FunctionName")

package com.example.mydiary.pages

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.mydiary.EntryDao
import com.example.mydiary.viewModels.NewEntryPageViewModel
import com.example.mydiary.util.Mood
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryPage(navController: NavController, entryDao: EntryDao, entryID:Int?) {
    if(entryID != null && entryID != 0){
        NewEntryPageViewModel.getInstance().updateID(entryID, entryDao)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "New Entry", color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(onClick = { NewEntryPageViewModel.getInstance().exitPage(navController) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                actions = {
                    if(entryID != null && entryID != 0){
                        IconButton(onClick = { NewEntryPageViewModel.getInstance().toggleConfirmation() }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        Column (modifier = Modifier.padding(contentPadding).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            DiaryDatePicker()
            MoodSelector()
            PopupWindowDialog( entryDao = entryDao, navController = navController)
            DiaryTextInput()
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    NewEntryPageViewModel.getInstance().saveToDatabase(entryDao)
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
private fun MoodSelector(){
    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Mood.values().reversed().forEach { mood ->
            MoodButton(mood)
        }
    }
}

@Composable
private fun MoodButton(mood: Mood){
    if(NewEntryPageViewModel.getInstance().uiState.collectAsState().value.mood == mood.value){
        IconButton(
            onClick = { NewEntryPageViewModel.getInstance().selectMood(mood.value) }
        ){
            Icon(
                imageVector = ImageVector.vectorResource(mood.selectedIconResourceID),
                contentDescription = mood.title,
                tint = mood.color,
                modifier = Modifier.size(80.dp)
            )
        }
    } else{
        IconButton(
            onClick = { NewEntryPageViewModel.getInstance().selectMood(mood.value) }
        ){
            Icon(
                imageVector = ImageVector.vectorResource(mood.iconResourceID),
                contentDescription = mood.title,
                tint = mood.color,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryTextInput(){
    OutlinedTextField(
        modifier = Modifier.padding(10.dp).fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = 50.dp, max = 300.dp),
        value = NewEntryPageViewModel.getInstance().uiState.collectAsState().value.content,
        onValueChange = { NewEntryPageViewModel.getInstance().updateContent(it) },
        label = { Text("What's Up?") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = true,
            imeAction = ImeAction.Done
            )
    )
}

@Composable
private fun DiaryDatePicker(){
    val mContext = LocalContext.current

// Declaring integer values
// for year, month and day
    val m1Year: Int
    val m1Month: Int
    val m1Day: Int

// Initializing a Calendar
    //val mCalendar = NewEntryPageViewModel.getInstance().uiState.collectAsState().value.selectedDate
    val mCalendar = Calendar.getInstance()
    if (mCalendar.get(Calendar.HOUR_OF_DAY) <= 6){
        mCalendar.add(Calendar.DAY_OF_MONTH, -1)
    }

// Fetching current year, month and day
    m1Year = mCalendar.get(Calendar.YEAR)
    m1Month = mCalendar.get(Calendar.MONTH)
    m1Day = mCalendar.get(Calendar.DAY_OF_MONTH)

    if(NewEntryPageViewModel.getInstance().uiState.collectAsState().value.dateString == ""){
        NewEntryPageViewModel.getInstance().updateDate(m1Day, m1Month, m1Year)
    }

// Declaring a string value to
// store date in string format

// Declaring DatePickerDialog and setting
// initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            NewEntryPageViewModel.getInstance().updateDate(mDayOfMonth, mMonth, mYear)
        }, m1Year, m1Month, m1Day
    )

    Column {
        Button(onClick = {
            mDatePickerDialog.show()
        }) {
            Text(text = NewEntryPageViewModel.getInstance().uiState.collectAsState().value.dateString)
        }
    }
}


private fun deleteEntry(entryDao: EntryDao, navController: NavController){
    NewEntryPageViewModel.getInstance().deleteEntry(entryDao)
    NewEntryPageViewModel.getInstance().toggleConfirmation()
    NewEntryPageViewModel.getInstance().exitPage(navController)
}






@Composable
fun PopupWindowDialog(entryDao: EntryDao, navController: NavController) {
    if(NewEntryPageViewModel.getInstance().uiState.collectAsState().value.showConfirmationPopup){
        Box {
            val popupWidth = 300.dp
            val popupHeight = 100.dp
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties()
            ) {
                Box(
                    Modifier
                        .size(popupWidth, popupHeight)
                        .padding(top = 5.dp)
                        .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text="Are you sure you want to delete this entry?")
                        Row {
                            Button(onClick = { deleteEntry(entryDao, navController) }){Text(text="Yes")}
                            Spacer(modifier = Modifier.weight(1F))
                            Button(onClick = { NewEntryPageViewModel.getInstance().toggleConfirmation() }){Text(text="No")}
                        }
                    }
                }
            }
        }
    }

}