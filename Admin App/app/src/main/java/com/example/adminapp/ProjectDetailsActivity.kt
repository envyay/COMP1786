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
import com.example.adminapp.helper.FirebaseRepo
import com.example.adminapp.models.ProjectModel
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { DetailItem("Project Name", project?.name ?: "") }
                        item { DetailItem("Description", project?.description ?: "") }
                        item { DetailItem("Manager", project?.manager ?: "") }
                        item { DetailItem("Budget", "$${project?.budget}") }
                        item { DetailItem("Start Date", project?.startDate.toString()) }
                        item { DetailItem("End Date", project?.endDate.toString()) }
//                        DetailItem("Special Requirements", project?.specialRequirements ?: "")
                        item {
                            DetailItem(
                                "Department Information",
                                project?.departmentInformation ?: ""
                            )
                        }
                        item {
                            DetailItem(
                                "Status",
                                Constants.PROJECT_STATUS_MAP[project?.status]?.label ?: ""
                            )
                        }
                        item { Spacer(modifier = Modifier.height(12.dp)) }
                        item {
                            Text(
                                text = "Special Requirements",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        item {
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
                        }
                        item { Spacer(modifier = Modifier.height(12.dp)) }

                        item {
                            ActionButton(
                                onEdit = {
                                    val intent = Intent(context, EditProjectActivity::class.java)
                                    intent.putExtra("project_id", project?.id.toString())
                                    editProjectLauncher.launch(intent)
                                },
                                onViewExpense = {
                                    val intent = Intent(context, ExpenseListActivity::class.java)
                                    intent.putExtra("project_id", project?.id.toString())
                                    context.startActivity(intent)
                                },
                                onDelete = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        project?.let {
                                            dao.deleteById(id = it.id.toString())
                                            FirebaseRepo.delete(it.id.toString())
                                        }

                                        withContext(Dispatchers.Main) {
                                            setResult(RESULT_OK)
                                            finish()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
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

@Composable
fun ActionButton(onEdit: () -> Unit = {}, onViewExpense: () -> Unit = {}, onDelete: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {

        Button(onClick = onEdit) {
            Text(text = "Edit")
        }

        Button(onClick = onViewExpense) {
            Text(text = "View Expense")
        }
        Button(onClick = onDelete) {
            Text(text = "Delete")
        }
    }
}