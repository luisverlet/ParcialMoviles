package com.example.parcialmoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parcialmoviles.components.confirmationDialog.ConfirmationDialog
import com.example.parcialmoviles.components.filter.FilterBar
import com.example.parcialmoviles.components.header.Header
import com.example.parcialmoviles.model.Task
import com.example.parcialmoviles.repository.TaskRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val taskRepository = remember { TaskRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    // Estados principales
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var completedTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var pendingTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var nextPageUrl by remember { mutableStateOf<String?>(null) }
    var previousPageUrl by remember { mutableStateOf<String?>(null) }
    var currentPageNumber by remember { mutableStateOf(1) }
    var priorityCounts by remember { mutableStateOf(mapOf("low" to 0, "medium" to 0, "high" to 0)) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Estados para filtros
    var searchQuery by remember { mutableStateOf("") }
    var completedFilter by remember { mutableStateOf<Boolean?>(null) }
    var ordering by remember { mutableStateOf<String?>(null) }

    // Estados para diálogos
    var showToggleConfirmationDialog by remember { mutableStateOf(false) }
    var taskToToggle by remember { mutableStateOf<Task?>(null) }
    var isUpdatingTask by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var isDeletingTask by remember { mutableStateOf(false) }

    fun loadPriorityCounts() {
        priorityCounts = mapOf(
            "high" to tasks.count { it.priority == 3 },
            "medium" to tasks.count { it.priority == 2 },
            "low" to tasks.count { it.priority == 1 }
        )
    }

    fun loadTasks(page: Int? = null, url: String? = null) {
        coroutineScope.launch {
            isLoading = true
            try {
                val result = if (url != null) {
                    taskRepository.getTasksByUrl(url)
                } else {
                    var taskResponse: com.example.parcialmoviles.model.TaskResponse? = null
                    taskRepository.getTasks(
                        page = page,
                        completed = completedFilter,
                        search = if (searchQuery.isNotEmpty()) searchQuery else null,
                        ordering = ordering
                    ).collect { result ->
                        result.onSuccess { response ->
                            taskResponse = response
                        }.onFailure { exception ->
                            error = exception.message
                        }
                    }
                    taskResponse?.let { Result.success(it) } ?: Result.failure(Exception("No se pudieron cargar las tareas"))
                }

                result.onSuccess { taskResponse ->
                    tasks = taskResponse.results
                    completedTasks = tasks.filter { it.completed }
                    pendingTasks = tasks.filter { !it.completed }
                    nextPageUrl = taskResponse.next
                    previousPageUrl = taskResponse.previous

                    if (page != null) {
                        currentPageNumber = page
                    } else if (url != null) {
                        val pageParam = url.substringAfter("page=").substringBefore("&")
                        currentPageNumber = pageParam.toIntOrNull() ?: currentPageNumber
                    }

                    loadPriorityCounts()
                }.onFailure { exception ->
                    error = exception.message
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // Funciones para manejar tareas (toggle y delete)
    fun handleToggleTaskCompletion(task: Task) {
        taskToToggle = task
        showToggleConfirmationDialog = true
    }

    fun confirmToggleTaskCompletion() {
        taskToToggle?.let { task ->
            coroutineScope.launch {
                isUpdatingTask = true
                try {
                    val newCompletedState = !task.completed
                    val result = taskRepository.toggleTaskCompletion(task.id, newCompletedState)

                    result.onSuccess {
                        tasks = tasks.map { if (it.id == task.id) it.copy(completed = newCompletedState) else it }
                        completedTasks = tasks.filter { it.completed }
                        pendingTasks = tasks.filter { !it.completed }
                        loadPriorityCounts()
                    }.onFailure { exception ->
                        error = "Error al actualizar la tarea: ${exception.message}"
                    }
                } catch (e: Exception) {
                    error = "Error al actualizar la tarea: ${e.message}"
                } finally {
                    isUpdatingTask = false
                    showToggleConfirmationDialog = false
                    taskToToggle = null
                }
            }
        }
    }

    fun handleDeleteTask(task: Task) {
        taskToDelete = task
        showDeleteConfirmationDialog = true
    }

    fun confirmDeleteTask() {
        taskToDelete?.let { task ->
            coroutineScope.launch {
                isDeletingTask = true
                try {
                    taskRepository.deleteTask(task.id).onSuccess {
                        tasks = tasks.filter { it.id != task.id }
                        completedTasks = tasks.filter { it.completed }
                        pendingTasks = tasks.filter { !it.completed }
                        loadPriorityCounts()
                    }.onFailure { e ->
                        error = "Error al eliminar: ${e.message}"
                    }
                } catch (e: Exception) {
                    error = "Error al eliminar: ${e.message}"
                } finally {
                    isDeletingTask = false
                    showDeleteConfirmationDialog = false
                    taskToDelete = null
                }
            }
        }
    }

    // Cargar tareas iniciales y cuando cambian los filtros
    LaunchedEffect(Unit) {
        loadTasks(1)
    }

    LaunchedEffect(searchQuery, completedFilter, ordering) {
        loadTasks(1)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("task_creation") }
            ) {
                Icon(Icons.Default.Add, "Añadir tarea")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Header(name = "Luis Alejandro", pendingTasks = pendingTasks.size)

                // Componente de filtros
                FilterBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    completedFilter = completedFilter,
                    onCompletedFilterChange = { completedFilter = it },
                    ordering = ordering,
                    onOrderingChange = { ordering = it },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Sección de Prioridad
                Text(
                    text = "Prioridad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriorityCard(
                        title = "Alta",
                        count = priorityCounts["high"]?.toString() ?: "0",
                        modifier = Modifier.weight(1f)
                    )
                    PriorityCard(
                        title = "Media",
                        count = priorityCounts["medium"]?.toString() ?: "0",
                        modifier = Modifier.weight(1f)
                    )
                    PriorityCard(
                        title = "Baja",
                        count = priorityCounts["low"]?.toString() ?: "0",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Contenido principal
                Box(modifier = Modifier.weight(1f)) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (error != null) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (completedTasks.isNotEmpty()) {
                                item { TaskSectionHeader(title = "Completadas") }
                                items(completedTasks) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggleComplete = { handleToggleTaskCompletion(task) },
                                        onDelete = { handleDeleteTask(task) },
                                        onEdit = {
                                            navController.navigate("task_creation/${task.id}")
                                        }
                                    )
                                }
                            }

                            if (pendingTasks.isNotEmpty()) {
                                item { TaskSectionHeader(title = "Por hacer") }
                                items(pendingTasks) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggleComplete = { handleToggleTaskCompletion(task) },
                                        onDelete = { handleDeleteTask(task) },
                                        onEdit = {

                                            navController.navigate("task_creation/${task.id}")
                                        }
                                    )
                                }
                            }

                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }

                    if (isUpdatingTask) {
                        LoadingIndicator(message = "Actualizando tarea...")
                    }

                    if (isDeletingTask) {
                        LoadingIndicator(message = "Eliminando tarea...")
                    }
                }

                // Paginación
                if (previousPageUrl != null || nextPageUrl != null) {
                    PaginationControls(
                        previousPageUrl = previousPageUrl,
                        nextPageUrl = nextPageUrl,
                        currentPageNumber = currentPageNumber,
                        onPreviousPage = { previousPageUrl?.let { loadTasks(url = it) } },
                        onNextPage = { nextPageUrl?.let { loadTasks(url = it) } }
                    )
                }
            }
        }
    )

    // Diálogos de confirmación
    ConfirmationDialog(
        isVisible = showToggleConfirmationDialog,
        title = "Cambiar estado de tarea",
        message = taskToToggle?.let { task ->
            if (task.completed) "¿Marcar '${task.name}' como pendiente?"
            else "¿Marcar '${task.name}' como completada?"
        } ?: "¿Cambiar estado de esta tarea?",
        confirmButtonText = "Confirmar",
        cancelButtonText = "Cancelar",
        onConfirm = { confirmToggleTaskCompletion() },
        onCancel = { showToggleConfirmationDialog = false; taskToToggle = null }
    )

    ConfirmationDialog(
        isVisible = showDeleteConfirmationDialog,
        title = "Eliminar tarea",
        message = taskToDelete?.let { "¿Eliminar '${it.name}'?" } ?: "¿Eliminar esta tarea?",
        confirmButtonText = "Eliminar",
        cancelButtonText = "Cancelar",
        onConfirm = { confirmDeleteTask() },
        onCancel = { showDeleteConfirmationDialog = false; taskToDelete = null }
    )
}

@Composable
private fun LoadingIndicator(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(message)
            }
        }
    }
}

@Composable
private fun PaginationControls(
    previousPageUrl: String?,
    nextPageUrl: String?,
    currentPageNumber: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onPreviousPage,
            enabled = previousPageUrl != null
        ) {
            Text("← Anterior")
        }

        Text(
            text = "Página $currentPageNumber",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        TextButton(
            onClick = onNextPage,
            enabled = nextPageUrl != null
        ) {
            Text("Siguiente →")
        }
    }
}

@Composable
fun TaskSectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ver todas",
            fontSize = 12.sp
        )
    }
}