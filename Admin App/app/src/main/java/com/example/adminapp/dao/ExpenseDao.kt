package com.example.adminapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.adminapp.models.ExpenseModel
import java.util.UUID

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: ExpenseModel)

    @Update
    suspend fun update(expense: ExpenseModel)

    @Delete
    suspend fun delete(expense: ExpenseModel)

    @Query("SELECT * FROM Expenses WHERE projectId = :projectId")
    fun getByProject(projectId: String): List<ExpenseModel>

    @Query("SELECT * FROM Expenses WHERE id = :id")
    fun getById(id: UUID): ExpenseModel?
}