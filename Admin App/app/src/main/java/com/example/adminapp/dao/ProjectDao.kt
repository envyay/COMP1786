package com.example.adminapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.adminapp.models.ProjectModel

@Dao
interface ProjectDao {

    @Insert
    suspend fun insert(project : ProjectModel)

    @Query("SELECT * FROM Projects")
    fun getAll() : List<ProjectModel>

}