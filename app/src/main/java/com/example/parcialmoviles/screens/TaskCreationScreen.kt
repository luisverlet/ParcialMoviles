package com.example.parcialmoviles.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcialmoviles.components.CustomTextField.CustomTextField
import com.example.parcialmoviles.components.customButton.CustomButton
import com.example.parcialmoviles.components.priorityDropdown.PriorityDropdown
import com.example.parcialmoviles.components.priorityDropdown.PriorityOption
import com.example.parcialmoviles.components.confirmationDialog.ConfirmationDialog
import com.example.parcialmoviles.repository.TaskRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(navController: NavController) {
    val context = LocalContext.current
    val taskRepository = remember { TaskRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf<PriorityOption?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva Tarea",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información de la Tarea",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        CustomTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = "Título",
                            icon = Icons.Default.Edit
                        )

                        CustomTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "Descripción",
                            icon = Icons.Default.Info
                        )

                        PriorityDropdown(
                            selectedPriority = selectedPriority,
                            onPrioritySelected = { selectedPriority = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                errorMessage?.let { message ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                if (showSuccess) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "¡Tarea creada exitosamente!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    CustomButton(
                        text = "Guardar",
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "El título es obligatorio"
                                return@CustomButton
                            }
                            if (selectedPriority == null) {
                                errorMessage = "Selecciona una prioridad"
                                return@CustomButton
                            }

                            errorMessage = null
                            showConfirmDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )

    ConfirmationDialog(
        isVisible = showConfirmDialog,
        title = "Confirmar",
        message = "¿Estás seguro de que quieres guardar esta tarea?",
        confirmButtonText = "Guardar",
        cancelButtonText = "Cancelar",
        onConfirm = {
            showConfirmDialog = false
            isLoading = true

            coroutineScope.launch {
                val result = taskRepository.createTask(
                    name = title,
                    description = description.ifBlank { null },
                    priority = selectedPriority!!.value
                )

                result.fold(
                    onSuccess = {
                        showSuccess = true
                        title = ""
                        description = ""
                        selectedPriority = null

                        // Ocultar mensaje de éxito después de 2 segundos
                        kotlinx.coroutines.delay(2000)
                        showSuccess = false
                    },
                    onFailure = { exception ->
                        errorMessage = "Error al crear la tarea: ${exception.message}"
                    }
                )

                isLoading = false
            }
        },
        onCancel = {
            showConfirmDialog = false
        }
    )
}