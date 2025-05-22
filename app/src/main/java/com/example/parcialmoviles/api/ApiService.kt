package com.example.parcialmoviles.api

import com.example.parcialmoviles.model.AuthRequest
import com.example.parcialmoviles.model.AuthResponse
import com.example.parcialmoviles.model.CreateTaskRequest
import com.example.parcialmoviles.model.Task
import com.example.parcialmoviles.model.TaskResponse
import com.example.parcialmoviles.model.UpdateTaskRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("tokens/")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("tasks/")
    suspend fun createTask(
        @Header("Authorization") authorization: String,
        @Body request: CreateTaskRequest
    ): Response<Void>

    @GET("tasks/")
    suspend fun getTasks(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("completed") completed: Boolean? = null,
        @Query("priority") priority: Int? = null,
        @Query("search") search: String? = null,
        @Query("ordering") ordering: String? = null
    ): TaskResponse

    @GET("tasks/{id}/")
    suspend fun getTask(
        @Header("Authorization") authorization: String,
        @Path("id") taskId: Int
    ): Task

    @PATCH("tasks/{id}/")
    suspend fun updateTask(
        @Header("Authorization") authorization: String,
        @Path("id") taskId: Int,
        @Body request: UpdateTaskRequest
    ): Response<Void>

    @PATCH("tasks/{id}/")
    suspend fun updateTaskCompletion(
        @Header("Authorization") authorization: String,
        @Path("id") taskId: Int,
        @Body request: Map<String, Boolean>
    ): Response<Void>

    @DELETE("tasks/{id}/")
    suspend fun deleteTask(
        @Header("Authorization") authorization: String,
        @Path("id") taskId: Int
    ): Response<Void>
}