package com.example.parcialmoviles.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.parcialmoviles.api.ApiClient
import com.example.parcialmoviles.model.AuthRequest

class TokenDBHelper(context: Context) :
    SQLiteOpenHelper(context, "TokenDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE token_table (id INTEGER PRIMARY KEY, token TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS token_table")
        onCreate(db)
    }

    fun saveToken(token: String) {
        val db = writableDatabase
        db.execSQL("DELETE FROM token_table")
        db.execSQL("INSERT INTO token_table (token) VALUES ('$token')")
        db.close()
    }

    fun getToken(): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT token FROM token_table LIMIT 1", null)
        val token = if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            null
        }
        cursor.close()
        db.close()
        return token
    }
}

class AuthRepository(private val context: Context) {
    private val dbHelper = TokenDBHelper(context)

    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val request = AuthRequest(username, password)
            val response = ApiClient.apiService.login(request)

            dbHelper.saveToken(response.token)
            Result.success(response.token)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    fun getToken(): String? = dbHelper.getToken()
}