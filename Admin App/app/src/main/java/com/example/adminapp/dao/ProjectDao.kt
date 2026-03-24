package com.example.adminapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.adminapp.models.ProjectModel

@Dao
interface ProjectDao {

    @Insert
    suspend fun insert(project : ProjectModel)

    @Update
    suspend fun update(project : ProjectModel)


    @Query("SELECT * FROM Projects")
    fun getAll() : List<ProjectModel>

    @Query("SELECT * FROM Projects WHERE id = :id")
    fun getById(id : String) : ProjectModel?

    @Query("DELETE FROM Projects WHERE id = :id")
    fun deleteById(id : String)


}