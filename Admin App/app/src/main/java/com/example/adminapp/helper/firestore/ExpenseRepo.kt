package com.example.adminapp.helper.firestore

import com.example.adminapp.models.ExpenseModel
import com.example.adminapp.models.ProjectModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.UUID

object ExpenseRepo {
    private val db = Firebase.firestore
    private const val COLLECTION = "expenses"

    fun upsert(expense: ExpenseModel) {
        db.collection(COLLECTION)
            .document(expense.id.toString())
            .set(expense)

    }

    fun delete(expenseId: String) {
        db.collection(COLLECTION)
            .document(expenseId)
            .delete()
    }
}