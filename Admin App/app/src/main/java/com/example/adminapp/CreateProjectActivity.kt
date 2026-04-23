package com.example.adminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.adminapp.common.ProjectStatus
import com.example.adminapp.dao.ProjectDao
import com.example.adminapp.database.AppDatabase
import com.example.adminapp.helper.firestore.ProjectRepo
import com.example.adminapp.models.ProjectModel
import com.example.adminapp.models.SpecialRequirementModel
import com.example.adminapp.ui.components.DatePickerModal
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateProjectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "project-expense"
        ).build()

        val dao = db.projectDao()

        enableEdgeToEdge()

        setContent {
            AdminAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PrimaryTopBar(
                            title = "Create Project",
                            showNavigationIcon = true,
                            onBackClick = { finish() }
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ProjectForm(dao) {
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectForm(dao: ProjectDao, onCreateDone: () -> Unit = {}) {

    val listState = rememberLazyListState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var manager by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    var statusExpanded by remember { mutableStateOf(false) }
    var mStatus by remember { mutableStateOf<ProjectStatus?>(null) }

    var startDateShow by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Long?>(null) }

    var endDateShow by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    var departmentInformation by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    val specialRequirements = remember {
        mutableStateListOf(
            SpecialRequirementModel("Projector", false),
            SpecialRequirementModel("Wi-Fi", false),
            SpecialRequirementModel("Catering", false),
            SpecialRequirementModel("Parking", false),
            SpecialRequirementModel("Meeting Room", false),
            SpecialRequirementModel("Others", false)
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                isError = showError && name.isBlank(),
                supportingText = {
                    if (showError && name.isBlank()) Text("Required")
                }
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                maxLines = 4,
                isError = showError && description.isBlank(),
                supportingText = {
                    if (showError && description.isBlank()) Text("Required")
                }
            )
        }

        item {
            OutlinedTextField(
                value = manager,
                onValueChange = { manager = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Manager") },
                isError = showError && manager.isBlank(),
                supportingText = {
                    if (showError && manager.isBlank()) Text("Required")
                }
            )
        }

        item {
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Budget") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = showError && budget.toDoubleOrNull() == null,
                supportingText = {
                    if (showError && budget.toDoubleOrNull() == null) {
                        Text("Invalid number")
                    }
                }
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { statusExpanded = true }
                    .border(
                        1.dp,
                        if (showError && mStatus == null) Color.Red else Color.Gray,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    "Status",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(mStatus?.label ?: "Select Status")
            }

            if (showError && mStatus == null) {
                Text("Required", color = MaterialTheme.colorScheme.error)
            }

            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                ProjectStatus.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.label) },
                        onClick = {
                            mStatus = it
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { startDateShow = true }
                    .border(
                        1.dp,
                        if (showError && startDate == null) Color.Red else Color.Gray,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    "Start Date",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(startDate?.let { convertMillisToDate(it) } ?: "Select Date")
            }

            if (showError && startDate == null) {
                Text("Required", color = MaterialTheme.colorScheme.error)
            }

            if (startDateShow) {
                DatePickerModal(
                    onDateSelected = { startDate = it },
                    onDismiss = { startDateShow = false }
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { endDateShow = true }
                    .border(
                        1.dp,
                        if (showError && endDate == null) Color.Red else Color.Gray,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    "End Date",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(endDate?.let { convertMillisToDate(it) } ?: "Select Date")
            }

            if (showError && endDate == null) {
                Text("Required", color = MaterialTheme.colorScheme.error)
            }

            if (endDateShow) {
                DatePickerModal(
                    onDateSelected = { endDate = it },
                    onDismiss = { endDateShow = false }
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Special Requirements",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                specialRequirements.forEachIndexed { index, item ->
                    FilterChip(
                        selected = item.isSelected,
                        onClick = {
                            specialRequirements[index] =
                                item.copy(isSelected = !item.isSelected)
                        },
                        label = { Text(item.name) },
                        leadingIcon = if (item.isSelected) {
                            {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = departmentInformation,
                onValueChange = { departmentInformation = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Department Information (optional)"
                    )
                }
            )
        }

        item {
            Button(onClick = {

                val hasError =
                    name.isBlank() ||
                            description.isBlank() ||
                            manager.isBlank() ||
                            budget.toDoubleOrNull() == null ||
                            startDate == null ||
                            endDate == null ||
                            mStatus == null

                showError = hasError

                if (hasError) return@Button

                val project = ProjectModel(
                    id = UUID.randomUUID(),
                    name = name,
                    description = description,
                    manager = manager,
                    budget = budget.toDoubleOrNull() ?: 0.0,
                    status = mStatus!!.label,
                    startDate = Date(startDate!!),
                    endDate = Date(endDate!!),
                    specialRequirements = specialRequirements
                        .filter { it.isSelected }
                        .joinToString(",") { it.name },
                    departmentInformation = departmentInformation
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dao.insert(project)
                    ProjectRepo.upsert(project)
                }

                onCreateDone()

            }) {
                Text("Create Project")
            }

            if (showError) {
                Text(
                    "Please fill all required fields",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}