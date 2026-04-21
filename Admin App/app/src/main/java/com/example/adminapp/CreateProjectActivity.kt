package com.example.adminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
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
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.jvm.java

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
                            onBackClick = {
                                finish()
                            })
                    },
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ProjectForm(dao, onCreateDone = {
                            setResult(RESULT_OK)
                            finish()
                        })
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
    var mStatus by remember { mutableStateOf(ProjectStatus.ACTIVE) }

    var startDateShow by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDateShow by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    var specialRequirements = remember {
        mutableStateListOf(
            SpecialRequirementModel(name = "Projector", isSelected = false),
            SpecialRequirementModel(name = "Wi-Fi", isSelected = false),
            SpecialRequirementModel(name = "Catering", isSelected = false),
            SpecialRequirementModel(name = "Parking", isSelected = false),
            SpecialRequirementModel(name = "Meeting Room", isSelected = false),
            SpecialRequirementModel(name = "Others", isSelected = false)
        )
    }
    val othersList = remember { mutableStateListOf<String>() }
    var othersInput by remember { mutableStateOf("") }
    var othersSelected by remember { mutableStateOf(false) }

    var departmentInformation by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }


    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                isError = showError && name.isBlank()

            )
        }
        item {
            OutlinedTextField(
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                isError = showError && description.isBlank()
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = manager,
                onValueChange = { manager = it },
                label = { Text("Manager") },
                isError = showError && manager.isBlank()
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                isError = showError && budget.toDoubleOrNull() == null
            )
        }
        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        statusExpanded = !statusExpanded
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(text = mStatus.label)
            }

            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                ProjectStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.label) },
                        onClick = {
                            mStatus = status
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
                    .clickable {
                        startDateShow = true
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(text = startDate?.let { convertMillisToDate(it) } ?: "Start Date")
                if (startDateShow) {
                    DatePickerModal(
                        onDateSelected = {
                            startDate = it
                        },
                        onDismiss = {
                            startDateShow = false
                        })
                }

            }
            if (showError && startDate == null) {
                Text("Required", color = Color.Red)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        endDateShow = true
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(text = endDate?.let { convertMillisToDate(it) } ?: "End Date")
                if (endDateShow) {
                    DatePickerModal(
                        onDateSelected = {
                            endDate = it
                        },
                        onDismiss = {
                            endDateShow = false
                        })
                }
            }
            if (showError && endDate == null) {
                Text("Required", color = Color.Red)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp)
            ) {

                // Chip có sẵn
                specialRequirements.forEachIndexed { index, model ->
                    FilterChip(
                        selected = model.isSelected,
                        onClick = {
                            specialRequirements[index] =
                                model.copy(isSelected = !model.isSelected)
                        },
                        label = { Text(model.name) },
                        leadingIcon = if (model.isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilterChip(
                    selected = othersSelected,
                    onClick = { othersSelected = !othersSelected },
                    label = { Text("Others") }
                )

                if (othersSelected) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = othersInput,
                        onValueChange = {
                            othersInput = it
                            if (it.contains(",")) {
                                it.split(",").map { s -> s.trim() }.forEach { item ->
                                    if (item.isNotBlank() && !othersList.contains(item)) {
                                        othersList.add(item)
                                    }
                                }
                                othersInput = ""
                            }
                        },
                        label = { Text("Type and press Enter or ','") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (othersInput.isNotBlank()) {
                                    othersList.add(othersInput.trim())
                                    othersInput = ""
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        othersList.forEachIndexed { index, item ->
                            AssistChip(
                                onClick = {},
                                label = { Text(item) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.clickable {
                                            othersList.removeAt(index)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }


        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = departmentInformation,
                onValueChange = { departmentInformation = it },
                label = { Text("DepartmentInformation (optional)") },
            )
        }

        item {
            Button(onClick = {
                showError = name.isBlank() || description.isBlank() || manager.isBlank() ||
                        budget.toDoubleOrNull() == null || startDate == null || endDate == null

                val project = ProjectModel(
                    id = UUID.randomUUID(),
                    name = name,
                    description = description,
                    manager = manager,
                    budget = budget.toDoubleOrNull() ?: 0.0,
                    status = mStatus.label,
                    startDate = startDate?.let { Date(it) } ?: Date(),
                    endDate = endDate?.let { Date(it) } ?: Date(),
                    specialRequirements = specialRequirements
                        .filter { it.isSelected }
                        .joinToString(",") { it.name },

                    othersList = othersList.joinToString(","),
                    departmentInformation = departmentInformation
                )
                CoroutineScope(Dispatchers.IO).launch {
                    dao.insert(project)
                    ProjectRepo.upsert(project)
                }
                onCreateDone()
            }) {
                Text(text = "Create Project")
            }
            if (showError) {
                Text(
                    "Please fill all required fields",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
