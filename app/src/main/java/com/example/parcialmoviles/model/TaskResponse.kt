package com.example.parcialmoviles.model

data class TaskResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Task>
)

data class Task(
    val id: Int,
    val name: String,
    val description: String?,
    val priority: Int,
    val completed: Boolean
)

data class UpdateTaskRequest(
    val completed: Boolean? = null,
    val name: String? = null,
    val description: String? = null,
    val priority: Int? = null
)