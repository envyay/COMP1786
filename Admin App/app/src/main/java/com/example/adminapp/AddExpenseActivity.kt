package com.example.adminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.adminapp.common.ExpenseStatus
import com.example.adminapp.common.PaymentMethods
import com.example.adminapp.common.TypeOfExpenses
import com.example.adminapp.dao.ExpenseDao
import com.example.adminapp.database.AppDatabase
import com.example.adminapp.helper.firestore.ExpenseRepo
import com.example.adminapp.models.ExpenseModel
import com.example.adminapp.ui.components.DatePickerModal
import com.example.adminapp.ui.components.PrimaryTopBar
import com.example.adminapp.ui.theme.AdminAppTheme
import kotlinx.coroutines.*
import java.sql.Date
import java.util.UUID

class AddExpenseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "project-expense"
        )
            .build()

        val dao = db.expenseDao()

        val projectIdString = intent.getStringExtra("project_id") ?: ""
        val expenseIdString = intent.getStringExtra("expense_id") ?: ""
        val mtitle = if (expenseIdString.isNotEmpty()) "Edit Expense" else "Add Expense"
        setContent {
            AdminAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PrimaryTopBar(
                            title = mtitle,
                            showNavigationIcon = true,
                            onBackClick = {
                                finish()
                            })
                    },
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        AddExpenseForm(projectIdString, expenseIdString, dao, onCreateDone = {
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
fun AddExpenseForm(
    projectIdString: String?,
    expenseIdString: String,
    dao: ExpenseDao,
    onCreateDone: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }

    var typeOfExpenseExpanded by remember { mutableStateOf(false) }
    var typeOfExpense by remember { mutableStateOf(TypeOfExpenses.MISCELLANEOUS) }

    var paymentStatusExpanded by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf(ExpenseStatus.PENDING) }

    var dateShow by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf<Long?>(null) }

    var paymentMethodExpanded by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf(PaymentMethods.CASH) }

    var claimant by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var isEditMode by remember { mutableStateOf(false) }


    val expenseId = expenseIdString.isNotEmpty().let {
        if (it) UUID.fromString(expenseIdString) else null
    }
    val projectId = UUID.fromString(projectIdString)

    LaunchedEffect(expenseId) {
        if (expenseId != null) {
            isEditMode = true

            CoroutineScope(Dispatchers.IO).launch {
                val expense = dao.getById(expenseId)

                expense?.let { e ->
                    amount = e.amount.toString()
                    currency = e.currency
                    typeOfExpense = TypeOfExpenses.entries.find { it.label == e.type }
                        ?: TypeOfExpenses.MISCELLANEOUS

                    paymentStatus = ExpenseStatus.entries.find { it.value == e.paymentStatus }
                        ?: ExpenseStatus.PENDING

                    paymentMethod = PaymentMethods.entries.find { it.value == e.paymentMethod }
                        ?: PaymentMethods.CASH
                    claimant = e.claimant
                    description = e.description ?: ""
                    location = e.location ?: ""
                    date = e.date.time
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        item {
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency") }
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        typeOfExpenseExpanded = !typeOfExpenseExpanded
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Type of Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = typeOfExpense.label)
            }

            DropdownMenu(
                expanded = typeOfExpenseExpanded,
                onDismissRequest = { typeOfExpenseExpanded = false }
            ) {
                TypeOfExpenses.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.label) },
                        onClick = {
                            typeOfExpense = status
                            typeOfExpenseExpanded = false
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
                        paymentStatusExpanded = !paymentStatusExpanded
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Payment Status",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = paymentStatus.label)
            }

            DropdownMenu(
                expanded = paymentStatusExpanded,
                onDismissRequest = { paymentStatusExpanded = false }
            ) {
                ExpenseStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.label) },
                        onClick = {
                            paymentStatus = status
                            paymentStatusExpanded = false
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
                        dateShow = true
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Type of Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = date?.let { convertMillisToDate(it) } ?: "Date")
                if (dateShow) {
                    DatePickerModal(
                        onDateSelected = {
                            date = it
                        },
                        onDismiss = {
                            dateShow = false
                        })
                }

            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        paymentMethodExpanded = !paymentMethodExpanded
                    }
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = paymentMethod.label)
            }

            DropdownMenu(
                expanded = paymentMethodExpanded,
                onDismissRequest = { paymentMethodExpanded = false }
            ) {
                PaymentMethods.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.label) },
                        onClick = {
                            paymentMethod = status
                            paymentMethodExpanded = false
                        }
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = claimant,
                onValueChange = { claimant = it },
                label = { Text("Claimant") }
            )
        }
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
        }
        item {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") }
            )
        }
        item {
            Button(
                onClick = {

                    CoroutineScope(Dispatchers.IO).launch {
                        var expense: ExpenseModel?
                        if (isEditMode) {
                            expense = ExpenseModel(
                                id = expenseId!!,
                                projectId = projectId,
                                amount = amount.toDouble(),
                                currency = currency,
                                type = typeOfExpense.label,
                                date = Date(date!!),
                                paymentMethod = paymentMethod.value,
                                claimant = claimant,
                                paymentStatus = paymentStatus.value,
                                description = description,
                                location = location
                            )
                            dao.update(expense)
                        } else {
                            expense = ExpenseModel(
                                id = UUID.randomUUID(),
                                projectId = projectId,
                                amount = amount.toDouble(),
                                currency = currency,
                                type = typeOfExpense.label,
                                date = Date(date!!),
                                paymentMethod = paymentMethod.value,
                                claimant = claimant,
                                paymentStatus = paymentStatus.value,
                                description = description,
                                location = location
                            )
                            dao.insert(expense)
                        }
                        ExpenseRepo.upsert(expense)
                        withContext(Dispatchers.Main) {
                            onCreateDone()
                        }
                    }
                }
            ) {
                Text(if (isEditMode) "Update" else "Add")
            }
        }
    }
}