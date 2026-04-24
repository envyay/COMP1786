package com.example.adminapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.adminapp.dao.ExpenseDao
import com.example.adminapp.database.AppDatabase
import com.example.adminapp.helper.firestore.ExpenseRepo
import com.example.adminapp.models.ExpenseModel
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.*

class ExpenseListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "project-expense"
        ).build()

        val dao = db.expenseDao()
        val projectId = intent.getStringExtra("project_id") ?: ""

        enableEdgeToEdge()

        setContent {
            var expenseList by remember { mutableStateOf(listOf<ExpenseModel>()) }

            val context = LocalContext.current

            val addLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    loadExpenses(dao, projectId) {
                        expenseList = it
                    }
                }
            }

            LaunchedEffect(projectId) {
                loadExpenses(dao, projectId) {
                    expenseList = it
                }
            }

            AdminAppTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PrimaryTopBar(
                            title =  "Expenses",
                            showNavigationIcon = true,
                            onBackClick = {
                                finish()
                            })
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                val intent = Intent(context, AddExpenseActivity::class.java)
                                intent.putExtra("project_id", projectId)
                                addLauncher.launch(intent)
                            }
                        ) {
                            Text("+")
                        }
                    }
                ) { innerPadding ->

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(expenseList) { expense ->

                            ExpenseItem(
                                expense = expense,
                                onClick = {
                                    val intent = Intent(context, AddExpenseActivity::class.java)
                                    intent.putExtra("expense_id", expense.id.toString())
                                    intent.putExtra("project_id", projectId)
                                    addLauncher.launch(intent)
                                },
                                onDelete = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        dao.delete(expense)
                                        ExpenseRepo.delete(expense.id.toString())


                                        val updated = dao.getByProject(projectId)

                                        withContext(Dispatchers.Main) {
                                            expenseList = updated
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

fun loadExpenses(
    dao: ExpenseDao,
    projectId: String,
    onResult: (List<ExpenseModel>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val data = dao.getByProject(projectId)
        withContext(Dispatchers.Main) {
            onResult(data)
        }
    }
}

@Composable
fun ExpenseItem(
    expense: ExpenseModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {


            Spacer(modifier = Modifier.height(4.dp))


            Text("${expense.type}")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "${expense.amount} ${expense.currency}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(expense.date.toString())

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}