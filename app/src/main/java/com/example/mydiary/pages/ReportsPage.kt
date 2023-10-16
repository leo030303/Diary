@file:Suppress("FunctionName")

package com.example.mydiary.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydiary.EntryDao
import com.example.mydiary.viewModels.ReportsPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsPage(viewModel: ReportsPageViewModel = ReportsPageViewModel(), navController: NavController, entryDao: EntryDao) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Reports", color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
            )
        }
    ) { contentPadding ->
        Column (modifier = Modifier.padding(contentPadding).fillMaxWidth()) {
            Card (
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ){
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Reports",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
        }
    }
}
