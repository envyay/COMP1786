package com.example.adminapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.adminapp.converter.Converters
import com.example.adminapp.dao.ProjectDao
import com.example.adminapp.models.ProjectModel

@Database(entities = [ProjectModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun projectDao(): ProjectDao
}