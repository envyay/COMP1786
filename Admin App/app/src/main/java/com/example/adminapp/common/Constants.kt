package com.example.adminapp.common

import com.example.adminapp.models.SpecialRequirementModel

enum class ProjectStatus(val value: Int, val label: String) {
    ACTIVE(value = 1, label = "Active"),
    COMPLETED(value = 2, label = "Completed"),
    ONHOLD(value = 3, label = "On Hold")
}

class Constants {
    companion object {
        val SPECIAL_REQUIREMENTS = listOf<SpecialRequirementModel>(
            SpecialRequirementModel(name = "Projector", isSelected = false),
            SpecialRequirementModel(name = "Wi-Fi", isSelected = false),
            SpecialRequirementModel(name = "Catering", isSelected = false),
            SpecialRequirementModel(name = "Parking", isSelected = false),
            SpecialRequirementModel(name = "Meeting Room", isSelected = false),
        );

        val PROJECT_STATUS_MAP: HashMap<Int, ProjectStatus> = hashMapOf(
            1 to ProjectStatus.ACTIVE,
            2 to ProjectStatus.COMPLETED,
            3 to ProjectStatus.ONHOLD
        );
    }
}