package com.example.adminapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.adminapp.common.Constants
import com.example.adminapp.database.AppDatabase
import com.example.adminapp.models.ProjectModel
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "project-expense"
        ).build()
        val dao = db.projectDao()
        val projectId = intent.getStringExtra("project_id")
        enableEdgeToEdge()
        setContent {
            var project by remember { mutableStateOf<ProjectModel?>(null) }
            val context = LocalContext.current
            val editProjectLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Reload project từ DB
                    CoroutineScope(Dispatchers.IO).launch {
                        val updated = dao.getById(id = projectId ?: "")
                        withContext(Dispatchers.Main) {
                            project = updated
                        }
                    }
                }
            }
            AdminAppTheme {
                LaunchedEffect(projectId) {
                    project = withContext(Dispatchers.IO) {
                        dao.getById(id = projectId ?: "")
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PrimaryTopBar(
                            title = project?.name ?: "Project Name",
                            showNavigationIcon = true,
                            onBackClick = {
                                finish()
                            })
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        DetailItem("Project Name", project?.name ?: "")
                        DetailItem("Description", project?.description ?: "")
                        DetailItem("Manager", project?.manager ?: "")
                        DetailItem("Budget", "$${project?.budget}")
                        DetailItem("Start Date", project?.startDate.toString())
                        DetailItem("End Date", project?.endDate.toString())
//                        DetailItem("Special Requirements", project?.specialRequirements ?: "")
                        DetailItem(
                            "Department Information",
                            project?.departmentInformation ?: ""
                        )
                        DetailItem(
                            "Status",
                            Constants.PROJECT_STATUS_MAP[project?.status]?.label ?: ""
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Special Requirements",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            project?.specialRequirements?.split(",")?.forEach {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(text = it) }
                                )
                            }
                        }

                        ActionButton(
                            onEdit = {
                                val intent = Intent(context, EditProjectActivity::class.java)
                                intent.putExtra("project_id", project?.id.toString())
                                editProjectLauncher.launch(intent)
                            },
                            onDelete = {
                                dao.deleteById(id = project?.id.toString())
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        LazyColumn() {
            item {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ActionButton(onEdit: () -> Unit = {}, onDelete: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {

        Button(onClick = onEdit) {
            Text(text = "Edit")
        }
        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = onDelete) {
            Text(text = "Delete")
        }
    }
}