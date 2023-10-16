@file:Suppress("FunctionName")

package com.example.mydiary.pages

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.mydiary.EntryDao
import com.example.mydiary.viewModels.NewEntryPageViewModel
import com.example.mydiary.util.Mood
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryPage(viewModel: NewEntryPageViewModel = NewEntryPageViewModel(), navController: NavController, entryDao: EntryDao, entryID:Int?) {
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
                    IconButton(onClick = { viewModel.exitPage(navController) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                actions = {
                    if(entryID != null && entryID != 0){
                        IconButton(onClick = { viewModel.toggleConfirmation() }) {
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
            BackHandler(
                enabled = (entryID == 0 && viewModel.uiState.collectAsState().value.content != "")
            ) {
                viewModel.saveToDatabase(entryDao)
                viewModel.exitPage(navController)
            }
            DiaryDatePicker(viewModel)
            MoodSelector(viewModel)
            PopupWindowDialog(viewModel = viewModel, entryDao = entryDao, navController = navController)
            DiaryTextInput(viewModel)
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    viewModel.saveToDatabase(entryDao)
                    viewModel.exitPage(navController)
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
        Mood.values().reversed().forEach { mood ->
            MoodButton(viewModel, mood)
        }
    }
}

@Composable
private fun MoodButton(viewModel: NewEntryPageViewModel, mood: Mood){
    if(viewModel.uiState.collectAsState().value.mood == mood.value){
        IconButton(
            onClick = { viewModel.selectMood(mood.value) }
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
            onClick = { viewModel.selectMood(mood.value) }
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
private fun DiaryTextInput(viewModel: NewEntryPageViewModel){
    OutlinedTextField(
        modifier = Modifier.padding(10.dp).fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = 50.dp, max = 300.dp),
        value = viewModel.uiState.collectAsState().value.content,
        onValueChange = { viewModel.updateContent(it) },
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


private fun deleteEntry(viewModel: NewEntryPageViewModel, entryDao: EntryDao, navController: NavController){
    viewModel.deleteEntry(entryDao)
    viewModel.toggleConfirmation()
    viewModel.exitPage(navController)
}






@Composable
fun PopupWindowDialog(viewModel: NewEntryPageViewModel, entryDao: EntryDao, navController: NavController) {
    if(viewModel.uiState.collectAsState().value.showConfirmationPopup){
        Box {
            val popupWidth = 300.dp
            val popupHeight = 150.dp
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties()
            ) {
                Box(
                    Modifier.size(popupWidth, popupHeight)
                ) {
                    Card (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp)),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Text(
                            text = "Are you sure you want to delete this entry?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(15.dp)
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        Row {
                            Button(
                                onClick = { deleteEntry(viewModel, entryDao, navController) },
                                modifier = Modifier.padding(10.dp)
                            ){
                                Text(text="Yes")
                            }
                            Spacer(modifier = Modifier.weight(1F))
                            Button(
                                onClick = { viewModel.toggleConfirmation() },
                                modifier = Modifier.padding(10.dp)
                            ){
                                Text(text="No")
                            }
                        }
                    }
                }
            }
        }
    }

}