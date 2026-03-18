package com.example.adminapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.adminapp.models.ProjectModel
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import java.util.Date
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectsScreen()
        }
    }
}

@Composable
fun ProjectsScreen() {
    var projects by remember {
        mutableStateOf(listOf<ProjectModel>())
    }
    val context = LocalContext.current

    AdminAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { PrimaryTopBar(title = "Projects") },
            floatingActionButton = {
                CreateButton(onClick = {
                    val intent = Intent(context, CreateProjectActivity::class.java)
                    context.startActivity(intent)

                    /// di chuyển đến màn hình tạo project

//                    projects = projects + ProjectModel(
//                        id = UUID.randomUUID(),
//                        name = "",
//                        description = "",
//                        manager = "",
//                        budget = 5000.0,
//                        status = 1,
//                        startDate = Date(),
//                        endDate = Date(),
//                        specialRequirements = null,
//                        departmentInformation = null
//                    )
                })
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                LazyColumn() {
                    items(projects) { project ->
                        ProjectCard(
                            project = project,
                            onClick = {}
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
    onClick: () -> Unit = {}
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