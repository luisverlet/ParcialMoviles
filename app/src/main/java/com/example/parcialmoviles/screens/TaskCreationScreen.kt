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
import com.example.parcialmoviles.R
import com.example.parcialmoviles.components.CustomTextField.CustomTextField
import com.example.parcialmoviles.components.customButton.CustomButton
import com.example.parcialmoviles.components.priorityDropdown.PriorityDropdown
import com.example.parcialmoviles.components.priorityDropdown.PriorityOption
import com.example.parcialmoviles.components.confirmationDialog.ConfirmationDialog
import com.example.parcialmoviles.repository.TaskRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    navController: NavController,
    taskId: Int? = null
) {
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
    var isLoadingTask by remember { mutableStateOf(taskId != null) }

    val isEditMode = taskId != null

    LaunchedEffect(taskId) {
        if (taskId != null) {
            isLoadingTask = true
            try {
                val result = taskRepository.getTask(taskId)
                result.fold(
                    onSuccess = { task ->
                        title = task.name
                        description = task.description ?: ""
                        selectedPriority = when (task.priority) {
                            1 -> PriorityOption(context.getString(R.string.low_priority), 1)
                            2 -> PriorityOption(context.getString(R.string.medium_priority), 2)
                            3 -> PriorityOption(context.getString(R.string.high_priority), 3)
                            else -> null
                        }
                    },
                    onFailure = { exception ->
                        errorMessage = context.getString(R.string.load_task_error, exception.message)
                    }
                )
            } finally {
                isLoadingTask = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) context.getString(R.string.edit_task) else context.getString(R.string.new_task),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = context.getString(R.string.back)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            if (isLoadingTask) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
                                text = context.getString(R.string.task_information),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            CustomTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = context.getString(R.string.title),
                                icon = Icons.Default.Edit
                            )

                            CustomTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = context.getString(R.string.description),
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
                                text = if (isEditMode) context.getString(R.string.task_updated_success)
                                else context.getString(R.string.task_created_success),
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
                            text = if (isEditMode) context.getString(R.string.update)
                            else context.getString(R.string.save),
                            onClick = {
                                if (title.isBlank()) {
                                    errorMessage = context.getString(R.string.title_required)
                                    return@CustomButton
                                }
                                if (selectedPriority == null) {
                                    errorMessage = context.getString(R.string.priority_required)
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
        }
    )

    ConfirmationDialog(
        isVisible = showConfirmDialog,
        title = context.getString(R.string.confirm),
        message = if (isEditMode)
            context.getString(R.string.confirm_update)
        else
            context.getString(R.string.confirm_save),
        confirmButtonText = if (isEditMode) context.getString(R.string.update)
        else context.getString(R.string.save),
        cancelButtonText = context.getString(R.string.cancel),
        onConfirm = {
            showConfirmDialog = false
            isLoading = true

            coroutineScope.launch {
                val result = if (isEditMode && taskId != null) {
                    taskRepository.updateTask(
                        taskId = taskId,
                        name = title,
                        description = description.ifBlank { null },
                        priority = selectedPriority!!.value
                    )
                } else {
                    taskRepository.createTask(
                        name = title,
                        description = description.ifBlank { null },
                        priority = selectedPriority!!.value
                    )
                }

                result.fold(
                    onSuccess = {
                        showSuccess = true
                        if (!isEditMode) {
                            title = ""
                            description = ""
                            selectedPriority = null
                        }

                        kotlinx.coroutines.delay(2000)
                        showSuccess = false

                        if (isEditMode) {
                            navController.popBackStack()
                        }
                    },
                    onFailure = { exception ->
                        errorMessage = if (isEditMode)
                            context.getString(R.string.update_task_error_creation, exception.message)
                        else
                            context.getString(R.string.create_task_error, exception.message)
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