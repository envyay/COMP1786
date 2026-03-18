package com.example.adminapp.models

import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

data class ProjectModel constructor(
    var id: UUID,
    var name: String,
    var description: String,
    var manager: String,
    var budget: Double,
    var status: Int,
    var startDate: Date,
    var endDate: Date,
    var specialRequirements: List<SpecialRequirementModel>?,
    var departmentInformation: String?
)
