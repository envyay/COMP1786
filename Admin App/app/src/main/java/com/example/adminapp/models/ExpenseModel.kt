package com.example.adminapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "Expenses")
data class ExpenseModel constructor(
    @PrimaryKey
    val id: UUID,

    val projectId: UUID,
    val date: Date,
    val amount: Double,
    val currency: String,

    val type: String,
    val paymentMethod: Int,
    val claimant: String,
    val paymentStatus: Int,

    val description: String?,
    val location: String?

)