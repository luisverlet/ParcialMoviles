package com.example.parcialmoviles.model

data class CreateTaskRequest(
    val name: String,
    val description: String? = null,
    val priority: Int
)