package com.example.adminapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.adminapp.dao.ProjectDao
import com.example.adminapp.database.AppDatabase
import com.example.adminapp.models.ProjectModel
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "project-expense"
        ).build()

        val dao = db.projectDao()
        enableEdgeToEdge()
        setContent {
            ProjectsScreen(dao)
        }
    }
}

@Composable
fun ProjectsScreen(dao: ProjectDao) {
    var projects by remember { mutableStateOf(listOf<ProjectModel>()) }

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    var statusFilter by remember { mutableStateOf<String?>(null) }
    var managerFilter by remember { mutableStateOf("") }
    var startDateFilter by remember { mutableStateOf<Long?>(null) }
    var endDateFilter by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch(Dispatchers.IO) {
                val updatedProjects = dao.getAll()
                withContext(Dispatchers.Main) {
                    projects = updatedProjects
                }
            }
        }

    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        projects = withContext(Dispatchers.IO) {
            dao.getAll()
        }
    }

    LaunchedEffect(query, statusFilter, managerFilter, startDateFilter, endDateFilter) {
        loadProjects(
            dao,
            query,
            statusFilter,
            managerFilter,
            startDateFilter,
            endDateFilter
        ) {
            projects = it
        }
    }

    AdminAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    PrimaryTopBar(
                        title = if (isSearching) "" else "Projects",
                        showNavigationIcon = isSearching,
                        showSearchIcon = true,
                        onSearchClick = {
                            isSearching = true
                        },
                        onBackClick = {
                            if (isSearching) {
                                isSearching = false
                                query = ""
                            }
                        }
                    )

                    if (isSearching) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = { Text("Search...") },
                            singleLine = true
                        )
                    }
                }
            },
            floatingActionButton = {
                CreateButton(onClick = {
                    val intent = Intent(context, CreateProjectActivity::class.java)
                    launcher.launch(intent)
                })
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projects) { project ->
                    ProjectCard(
                        project = project,
                        onClick = {
                            val intent = Intent(context, ProjectDetailsActivity::class.java)
                            intent.putExtra("project_id", project.id.toString())
                            launcher.launch(intent)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButton(onClick: () -> Unit = {}) {
    FloatingActionButton(
        onClick = {
            onClick()

        }
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    }
}

@Composable
fun ProjectCard(
    project: ProjectModel,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Project name: ${project.name}")

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Manager: ${project.manager}")

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Budget: ${project.budget}")

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Status: ${project.status}")

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${project.startDate} - ${project.endDate}")

        }
    }
}


fun loadProjects(
    dao: ProjectDao,
    query: String,
    status: String?,
    manager: String?,
    startDate: Long?,
    endDate: Long?,
    onResult: (List<ProjectModel>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = dao.searchAdvanced(
            query = if (query.isBlank()) null else query,
            status = status,
            manager = manager,
            startDate = startDate,
            endDate = endDate
        )

        withContext(Dispatchers.Main) {
            onResult(result)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdminAppTheme {
    }
}