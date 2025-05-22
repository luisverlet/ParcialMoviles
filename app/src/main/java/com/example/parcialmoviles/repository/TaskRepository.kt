package com.example.parcialmoviles.repository

import android.content.Context
import android.util.Log
import com.example.parcialmoviles.api.ApiClient
import com.example.parcialmoviles.model.CreateTaskRequest
import com.example.parcialmoviles.model.TaskResponse
import com.example.parcialmoviles.model.UpdateTaskRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TaskRepository(private val context: Context) {
    private val authRepository = AuthRepository(context)

    suspend fun createTask(name: String, description: String?, priority: Int): Result<Unit> {
        return try {
            val token = authRepository.getToken() ?: return Result.failure(Exception("No se encontró el token"))
            val request = CreateTaskRequest(name, description, priority)
            val response = ApiClient.apiService.createTask("Token $token", request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al crear tarea: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error al crear tarea", e)
            Result.failure(e)
        }
    }

    fun getTasks(page: Int? = null, completed: Boolean? = null, priority: Int? = null): Flow<Result<TaskResponse>> = flow {
        try {
            val token = authRepository.getToken() ?: throw Exception("No se encontró el token")
            val response = ApiClient.apiService.getTasks("Token $token", page, completed, priority)
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error al obtener tareas", e)
            emit(Result.failure(e))
        }
    }

    suspend fun getTasksByUrl(url: String): Result<TaskResponse> {
        return try {
            val token = authRepository.getToken() ?: return Result.failure(Exception("No se encontró el token"))

            // Extraer los parámetros de la URL
            val uri = android.net.Uri.parse(url)
            val page = uri.getQueryParameter("page")?.toIntOrNull()
            val completed = uri.getQueryParameter("completed")?.toBoolean()
            val priority = uri.getQueryParameter("priority")?.toIntOrNull()

            // Usar el método getTasks existente con los parámetros extraídos
            val response = ApiClient.apiService.getTasks("Token $token", page, completed, priority)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error al obtener tareas por URL", e)
            Result.failure(e)
        }
    }

    suspend fun getTaskCounts(): Map<String, Int> {
        return try {
            val token = authRepository.getToken() ?: return emptyMap()
            mapOf(
                "high" to ApiClient.apiService.getTasks("Token $token", priority = 3).count,
                "medium" to ApiClient.apiService.getTasks("Token $token", priority = 2).count,
                "low" to ApiClient.apiService.getTasks("Token $token", priority = 1).count
            )
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error al obtener conteos de prioridad", e)
            emptyMap()
        }
    }

    suspend fun toggleTaskCompletion(taskId: Int, completed: Boolean): Result<Unit> {
        return try {
            val token = authRepository.getToken() ?: return Result.failure(Exception("No se encontró el token"))
            val completionData = mapOf("completed" to completed)
            val response = ApiClient.apiService.updateTaskCompletion("Token $token", taskId, completionData)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar tarea: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error al actualizar estado de tarea", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: Int): Result<Unit> {
        return try {
            val token = authRepository.getToken() ?: return Result.failure(Exception("No se encontró el token"))
            val response = ApiClient.apiService.deleteTask("Token $token", taskId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar tarea: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error al eliminar tarea", e)
            Result.failure(e)
        }
    }
}