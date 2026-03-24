package com.example.adminapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import com.example.adminapp.dao.ProjectDao
import com.example.adminapp.database.AppDatabase
import com.example.adminapp.models.ProjectModel
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

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
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch(Dispatchers.IO) {
                val updatedProjects = dao.getAll() // gọi DB
                withContext(Dispatchers.Main) {
                    projects = updatedProjects // cập nhật State
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
    AdminAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { PrimaryTopBar(title = "Projects") },
            floatingActionButton = {
                CreateButton(onClick = {
                    val intent = Intent(context, CreateProjectActivity::class.java)
                    launcher.launch(intent)
                })
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                LazyColumn() {
                    items(projects) { project ->
                        ProjectCard(
                            project = project,
                            onClick = {
                                val intent = Intent(context, ProjectDetailsActivity::class.java)
                                intent.putExtra("project_id", project.id.toString())
                                context.startActivity(intent)
                            }
                        )
                    }
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable {
                onClick()
            }
            .border(
                width = 1.dp,
                color = Color.Blue,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(16.dp)

    ) {
        Text(text = "Project name: ${project.name}")
        Text(text = "Manager: ${project.manager}")
        Text(text = "Budget: ${project.budget}")
        Text(text = "Status: ${project.status}")
        Text(text = "Date: ${project.startDate} - ${project.endDate}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdminAppTheme {
    }
}