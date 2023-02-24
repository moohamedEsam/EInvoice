package com.example.database.models

interface DataEntity {
    val id: String
    val isSynced: Boolean
    val syncError: String?
    val isCreated: Boolean
    val isUpdated: Boolean
    val isDeleted: Boolean
}