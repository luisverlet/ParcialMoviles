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
import androidx.compose.ui.res.stringResource
import com.example.parcialmoviles.R

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
        Text(stringResource(R.string.welcome), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.app_subtitle), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            email,
            { email = it },
            stringResource(R.string.email),
            icon = Icons.Filled.Email,
            isPassword = false
        )

        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            password,
            { password = it },
            stringResource(R.string.password),
            isPassword = true,
            icon = Icons.Filled.Lock
        )
        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(stringResource(R.string.next), {
            scope.launch {
                val result = repo.login(email, password)
                result.onSuccess {
                    navController.navigate("home")
                }.onFailure {
                    Toast.makeText(context, "Login Error", Toast.LENGTH_SHORT).show()
                }
            }
        }, modifier = Modifier.fillMaxWidth())
    }
}
