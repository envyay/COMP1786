package com.example.adminapp.helper.firestore

import com.example.adminapp.models.ProjectModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object ProjectRepo {
    private val db = Firebase.firestore
    private const val COLLECTION = "projects"

    fun upsert(project: ProjectModel) {
        db.collection(COLLECTION)
            .document(project.id.toString())
            .set(project)
    }

    fun delete(projectId: String) {
        db.collection(COLLECTION)
            .document(projectId)
            .delete()
    }
}