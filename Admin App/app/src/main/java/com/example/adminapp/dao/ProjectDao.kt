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
    suspend fun deleteById(id : String)

    @Query("""
    SELECT * FROM Projects
    WHERE
        (:query IS NULL OR
            name LIKE '%' || :query || '%' OR
            description LIKE '%' || :query || '%' OR
            manager LIKE '%' || :query || '%' OR
            status LIKE '%' || :query || '%' OR
            startDate LIKE '%' || :query || '%' OR
            endDate LIKE '%' || :query || '%'
            
        )
    AND (:status IS NULL OR status = :status)
    AND (:manager IS NULL OR manager LIKE '%' || :manager || '%')
    AND (:startDate IS NULL OR startDate >= :startDate)
    AND (:endDate IS NULL OR endDate <= :endDate)
""")
    fun searchAdvanced(
        query: String?,
        status: String?,
        manager: String?,
        startDate: Long?,
        endDate: Long?
    ): List<ProjectModel>
}