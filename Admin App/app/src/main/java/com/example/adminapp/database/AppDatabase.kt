package com.example.adminapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.adminapp.converter.Converters
import com.example.adminapp.dao.ExpenseDao
import com.example.adminapp.dao.ProjectDao
import com.example.adminapp.models.ExpenseModel
import com.example.adminapp.models.ProjectModel

@Database(
    entities = [ProjectModel::class,
        ExpenseModel::class], version = 4
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun expenseDao(): ExpenseDao
}