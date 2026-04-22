package com.example.adminapp.common

import com.example.adminapp.models.SpecialRequirementModel

enum class ProjectStatus(val value: Int, val label: String) {
    ACTIVE(value = 1, label = "Active"),
    COMPLETED(value = 2, label = "Completed"),
    ONHOLD(value = 3, label = "On Hold")
}

enum class ExpenseStatus(val value: Int, val label: String) {
    PENDING(value = 1, label = "Pending"),
    APPROVED(value = 2, label = "Paid"),
    REJECTED(value = 3, label = "Reimbursed")
}

enum class TypeOfExpenses(val value: Int, val label: String) {
    TRAVEL(value = 1, label = "Travel"),
    EQUIPMENT(value = 2, label = "Equipment"),
    MATERIALS(value = 3, label = "Materials"),
    SERVICES(value = 4, label = "Services"),
    SOFTWARE_LICENSES(value = 5, label = "Software/Licenses"),
    LABOUR_COST(value = 6, label = "Labour_costs"),
    UTILITIES(value = 7, label = "Utilities"),
    MISCELLANEOUS(value = 8, label = "Miscellaneous")
}

enum class PaymentMethods(val value: Int, val label: String) {
    CASH(value = 1, label = "Cash"),
    CREDIT_CARD(value = 2, label = "Credit card"),
    BANK_TRANSFER(value = 3, label = "Bank Transfer"),
    CHEQUE(value = 4, label = "Cheque"),
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

        val PROJECT_STATUS_MAP = mapOf(
            ProjectStatus.ACTIVE.label to ProjectStatus.ACTIVE,
            ProjectStatus.COMPLETED.label to ProjectStatus.COMPLETED,
            ProjectStatus.ONHOLD.label to ProjectStatus.ONHOLD
        );

        val EXPENSE_STATUS_MAP: HashMap<Int, ExpenseStatus> = hashMapOf(
            1 to ExpenseStatus.PENDING,
            2 to ExpenseStatus.APPROVED,
            3 to ExpenseStatus.REJECTED
        );
    }
}
