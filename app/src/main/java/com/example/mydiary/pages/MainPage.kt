@file:Suppress("FunctionName")

package com.example.mydiary.pages

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydiary.*
import com.example.mydiary.util.Mood
import com.example.mydiary.viewModels.MainPageViewModel
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController, entryDao: EntryDao) {
    MainPageViewModel.getInstance().getEntries(entryDao)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Diary", color = MaterialTheme.colorScheme.primary)
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settingsPage") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    IconButton(onClick = { MainPageViewModel.getInstance().toggleSearch(entryDao) }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("newEntryPage/0") }) {
                Icon(imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Entry",
                    modifier = Modifier.padding(2.dp),
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
    ) { contentPadding ->
        Column (modifier = Modifier.padding(contentPadding)) {
            SearchView(entryDao = entryDao)
            MonthSelector(entryDao = entryDao)
            if(!MainPageViewModel.getInstance().uiState.collectAsState().value.displaySearch){
                MoodBars(MainPageViewModel.getInstance().uiState.collectAsState().value.entries, MainPageViewModel.getInstance().uiState.collectAsState().value.selectedMonth)
            }
            EntriesList(MainPageViewModel.getInstance().uiState.collectAsState().value.entries, navController)
        }
    }
}


@Composable
private fun MonthSelector(entryDao: EntryDao){
    if(!MainPageViewModel.getInstance().uiState.collectAsState().value.displaySearch){
        Row {
            IconButton(
                onClick = { MainPageViewModel.getInstance().decrementSelectedMonth(entryDao) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Previous Month"
                )
            }
            Spacer(Modifier.weight(1f))
            MainPageDatePicker(entryDao)
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { MainPageViewModel.getInstance().incrementSelectedMonth(entryDao) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next Month"
                )
            }
    }

    }


}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }



@Composable
private fun EntriesList(entries: List<Entry>, navController: NavController) {
    LazyColumn {
        items(entries) { entry ->
            EntryItem(entry, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryItem(entry: Entry, navController: NavController){
    val mCal:Calendar = Calendar.getInstance()
    mCal.timeInMillis = entry.dateCreated
    val dayName:String = mCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH)!!
    val monthName:String = mCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH)!!
    val dateText:String = dayName+", "+mCal.get(Calendar.DAY_OF_MONTH)+" "+monthName
    val mood = Mood.values()[entry.mood]
    val contrastColour = MaterialTheme.colorScheme.secondary.copy(alpha = .3F)
    Card (
        modifier = Modifier.padding(10.dp).fillMaxWidth().background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(12.dp)),
        onClick = { navController.navigate("newEntryPage/${entry.eid}") }
    ){
        Row(
            modifier = Modifier.background(color = contrastColour)
        ){
            Text(modifier = Modifier.padding(5.dp), text = dateText, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.weight(1F))
            Text(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), text = mood.title,
                color = mood.color, fontWeight = FontWeight.Bold, fontSize = 25.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        ){
            Icon(
                imageVector = mood.icon,
                contentDescription = mood.title,
                tint = mood.color,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = entry.content,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    entryDao: EntryDao
) {
    if(MainPageViewModel.getInstance().uiState.collectAsState().value.displaySearch){
        TextField(
            value = MainPageViewModel.getInstance().uiState.collectAsState().value.searchBarText,
            onValueChange = { value ->
                MainPageViewModel.getInstance().updateSearchBar(value, entryDao)
            },
            modifier = modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(24.dp)
                )
            },
            trailingIcon = {
                if (MainPageViewModel.getInstance().uiState.collectAsState().value.searchBarText != "") {
                    IconButton(
                        onClick = {
                            MainPageViewModel.getInstance().updateSearchBar("", entryDao) // Remove text from TextField when you press the 'X' icon
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RectangleShape
        )
    }

}

@Composable
private fun MoodBars(initEntries: List<Entry>, currentMonth: Calendar){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {if (initEntries.isNotEmpty()){
        val entries = initEntries.reversed()
        Column {
            Canvas(
                modifier = Modifier
                    .height(50.dp)
                    .padding(top = 5.dp, start = 5.dp, end = 5.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                val w = size.width
                val baseBarHeight = size.height

                val count = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                val barRects: List<List<Pair<Color, Float>>> = entries
                    .groupBy { it.dateCreated }
                    .map {
                        var percentTotal = 0
                        var itemsCount = 0
                        val moodCountsArray = intArrayOf(0,0,0,0,0)
                        it.value.forEach {entry ->
                            moodCountsArray[entry.mood]+=1
                            percentTotal += Mood.values()[entry.mood].barPercentage
                            itemsCount += 1
                        }
                        val totalBarHeight = ((percentTotal.toFloat()/itemsCount.toFloat())/100)*baseBarHeight
                        moodCountsArray.mapIndexed {index, moodCount  ->
                            val moodColor = Mood.values()[index].color
                            val partialBarHeight = totalBarHeight*(moodCount.toFloat()/itemsCount.toFloat())
                            Pair(moodColor, partialBarHeight)
                        }
                    }
                barRects.forEachIndexed{index, barRectMiniList ->
                    var currentTotalOffset = 0F
                    barRectMiniList.forEach { barRect ->
                        drawRect(
                            color = barRect.first,
                            topLeft = Offset(x = index * (w/count), y = baseBarHeight-barRect.second-currentTotalOffset),
                            size = Size(30f, barRect.second) //fix width
                        )
                        currentTotalOffset+=barRect.second
                    }
                }
            }
            Text(
                text= "1   4   7   10  13  16  19  22  25  28",
                modifier = Modifier
                    .height(20.dp)
                    .padding(top = 0.dp, start = 5.dp, end = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                fontFamily = FontFamily.Monospace,
                fontSize = dpToSp(14.dp)
            )
        }

    } else {
        Text(
            text = "Add entries to get stats",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
    }
}

@Composable
private fun MainPageDatePicker(entryDao: EntryDao){
    val mContext = LocalContext.current

// Declaring integer values
// for year, month and day
    val m1Year: Int
    val m1Month: Int
    val m1Day: Int

// Initializing a Calendar
    //val mCalendar = MainPageViewModel.getInstance().uiState.collectAsState().value.selectedDate
    val mCalendar = Calendar.getInstance()
    if (mCalendar.get(Calendar.HOUR_OF_DAY) <= 6){
        mCalendar.add(Calendar.DAY_OF_MONTH, -1)
    }

// Fetching current year, month and day
    m1Year = mCalendar.get(Calendar.YEAR)
    m1Month = mCalendar.get(Calendar.MONTH)
    m1Day = mCalendar.get(Calendar.DAY_OF_MONTH)


// Declaring a string value to
// store date in string format

// Declaring DatePickerDialog and setting
// initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, _: Int ->
            MainPageViewModel.getInstance().setSelectedMonth(mMonth, mYear, entryDao)
        }, m1Year, m1Month, m1Day
    )
    Button(
        modifier = Modifier.padding(10.dp),
        onClick = {
            mDatePickerDialog.show()
        }
    ){
        Text(text = "${
            MainPageViewModel.getInstance().uiState.collectAsState().value.selectedMonth.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                Locale.ENGLISH
            )!!
        } ${MainPageViewModel.getInstance().uiState.collectAsState().value.selectedMonth.get(Calendar.YEAR)}")
    }
}