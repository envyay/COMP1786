package com.example.adminapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "Projects")
data class ProjectModel constructor(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val description: String,
    val manager: String,
    val budget: Double,
    val status: Int,
    val startDate: Date,
    val endDate: Date,
    val specialRequirements: String?,
    val departmentInformation: String?
)
