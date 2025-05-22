package com.example.parcialmoviles.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parcialmoviles.repository.AuthRepository
import kotlinx.coroutines.launch
import com.example.parcialmoviles.components.customButton.CustomButton
import com.example.parcialmoviles.components.CustomTextField.CustomTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { AuthRepository(context) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("A tu app ToDoList", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            email,
            { email = it },
            "Email",
            icon = Icons.Filled.Email,
            isPassword = false
        )

        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            password,
            { password = it },
            "Contraseña",
            isPassword = true,
            icon = Icons.Filled.Lock
        )
        Spacer(modifier = Modifier.height(24.dp))

        CustomButton("Next", {
            scope.launch {
                val result = repo.login(email, password)
                result.onSuccess {
                    navController.navigate("home")
                }.onFailure {
                    Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }, modifier = Modifier.fillMaxWidth())
    }
}
