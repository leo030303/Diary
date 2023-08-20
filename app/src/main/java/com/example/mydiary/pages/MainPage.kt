package com.example.mydiary.pages

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mydiary.*
import com.example.mydiary.util.Mood
import com.example.mydiary.viewModels.MainPageViewModel
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController, entryDao: EntryDao, viewModel: MainPageViewModel = viewModel()) {
    viewModel.getEntries(entryDao)
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
                    IconButton(onClick = { viewModel.toggleSearch(entryDao) }) {
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
            SearchView(viewModel = viewModel, entryDao = entryDao)
            MonthSelector(viewModel = viewModel, entryDao = entryDao)
            Charts(viewModel.uiState.collectAsState().value.entries)
            EntriesList(viewModel.uiState.collectAsState().value.entries, navController)
        }
    }
}


@Composable
private fun MonthSelector(viewModel: MainPageViewModel, entryDao: EntryDao){
    if(!viewModel.uiState.collectAsState().value.displaySearch){
        Row {
            IconButton(
                onClick = { viewModel.decrementSelectedMonth(entryDao) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Previous Month"
                )
            }
            Spacer(Modifier.weight(1f))
            Text(text = viewModel.uiState.collectAsState().value.selectedMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)!!+
                    " "+
                    viewModel.uiState.collectAsState().value.selectedMonth.get(Calendar.YEAR))
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { viewModel.incrementSelectedMonth(entryDao) }
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
    val mood = when (entry.mood) {
        Mood.TERRIBLE.value -> Mood.TERRIBLE
        Mood.BAD.value -> Mood.BAD
        Mood.OKAY.value -> Mood.OKAY
        Mood.GOOD.value -> Mood.GOOD
        Mood.AWESOME.value -> Mood.AWESOME
        else -> Mood.OKAY
    }
    Card (
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        onClick = { navController.navigate("newEntryPage/${entry.eid}") }
    ){
        Text(modifier = Modifier.padding(5.dp), text = dateText)
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
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    viewModel: MainPageViewModel,
    entryDao: EntryDao
) {
    if(viewModel.uiState.collectAsState().value.displaySearch){
        TextField(
            value = viewModel.uiState.collectAsState().value.searchBarText,
            onValueChange = { value ->
                viewModel.updateSearchBar(value, entryDao)
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
                if (viewModel.uiState.collectAsState().value.searchBarText != "") {
                    IconButton(
                        onClick = {
                            viewModel.updateSearchBar("", entryDao) // Remove text from TextField when you press the 'X' icon
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
private fun Charts(entries: List<Entry>){
    MoodFlowChart(entries = entries)
}



@Composable
fun MoodFlowChart(
    entries: List<Entry>
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "string.mood_flow",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val entriesGrouped = entries
                .groupBy { it.mood }
                val max = try{entriesGrouped.maxOf { it.value.size }}catch (exception: NoSuchElementException){ 1 }
                val mostFrequentMoodValue = entriesGrouped
                    .filter { it.value.size == max }
                    .maxByOrNull {
                        it.key
                    }?.key ?: Mood.OKAY.value
                val mostFrequentMood = when (mostFrequentMoodValue) {
                    Mood.TERRIBLE.value -> Mood.TERRIBLE
                    Mood.BAD.value -> Mood.BAD
                    Mood.OKAY.value -> Mood.OKAY
                    Mood.GOOD.value -> Mood.GOOD
                    Mood.AWESOME.value -> Mood.AWESOME
                    else -> Mood.OKAY
                }

                val moods = listOf(Mood.AWESOME, Mood.GOOD, Mood.OKAY, Mood.BAD, Mood.TERRIBLE)
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    moods.forEach { mood ->
                        Icon(
                            imageVector = mood.icon,
                            contentDescription = mood.title,
                            tint = mood.color,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                if (entries.isNotEmpty())
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        val max = Mood.AWESOME.value
                        val count = entries.size

                        val offsets = entries.mapIndexed { index, entry ->
                            Offset(
                                w * ((if (index == 0) index.toFloat() else index + 1f) / count),
                                h * (1 - entry.mood.toFloat() / max.toFloat())
                            )
                        }
                        val path = Path().apply {
                            moveTo(offsets.first().x, offsets.first().y)
                            offsets.forEachIndexed { index, offset ->
                                if (index == 0) return@forEachIndexed
                                quadTo(offsets[index - 1], offset)
                            }
                        }
                        // workaround to copy compose path by using android path
                        val fillPath = android.graphics.Path(path.asAndroidPath())
                            .asComposePath()
                            .apply {
                                lineTo(
                                    if (offsets.size > 1)
                                        (offsets[offsets.size - 2].x + offsets.last().x) / 2
                                    else offsets.last().x, h
                                )
                                lineTo(0f, h)
                                close()
                            }
                        drawPath(
                            fillPath,
                            brush = Brush.verticalGradient(
                                listOf(
                                    mostFrequentMood.color,
                                    Color.Transparent
                                ),
                                endY = h
                            )
                        )
                        drawPath(
                            path,
                            color = mostFrequentMood.color,
                            style = Stroke(8f, cap = StrokeCap.Round)
                        )
                    } else {
                    Text(
                        text = "string.no_data_yet",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                text = "string.mood_during_month",
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

fun Path.quadTo(point1: Offset, point2: Offset) {
    quadraticBezierTo(
        point1.x,
        point1.y,
        (point1.x + point2.x) / 2f,
        (point1.y + point2.y) / 2f,
    )
}

