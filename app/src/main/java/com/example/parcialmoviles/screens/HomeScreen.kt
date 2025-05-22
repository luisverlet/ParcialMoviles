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
import com.example.parcialmoviles.components.header.Header
import com.example.parcialmoviles.model.Task
import com.example.parcialmoviles.repository.TaskRepository
import kotlinx.coroutines.launch
import com.example.parcialmoviles.components.confirmationDialog.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val taskRepository = remember { TaskRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var completedTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var pendingTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var nextPageUrl by remember { mutableStateOf<String?>(null) }
    var previousPageUrl by remember { mutableStateOf<String?>(null) }
    var currentPageNumber by remember { mutableStateOf(1) }
    var priorityCounts by remember { mutableStateOf(mapOf("low" to 0, "medium" to 0, "high" to 0)) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun loadPriorityCounts() {
        // Calcular conteos localmente desde las tareas actuales
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
                    taskRepository.getTasks(page).collect { result ->
                        result.onSuccess { response ->
                            taskResponse = response
                        }.onFailure { exception ->
                            error = exception.message
                        }
                    }
                    if (taskResponse != null) Result.success(taskResponse!!) else Result.failure(Exception("No se pudieron cargar las tareas"))
                }

                result.onSuccess { taskResponse ->
                    tasks = taskResponse.results
                    completedTasks = tasks.filter { it.completed }
                    pendingTasks = tasks.filter { !it.completed }

                    // Extraer número de página de las URLs
                    nextPageUrl = taskResponse.next
                    previousPageUrl = taskResponse.previous

                    // Actualizar número de página actual
                    if (page != null) {
                        currentPageNumber = page
                    } else if (url != null) {
                        // Extraer número de página de la URL
                        val pageParam = url.substringAfter("page=").substringBefore("&")
                        currentPageNumber = pageParam.toIntOrNull() ?: currentPageNumber
                    }

                    // Cargar conteos de prioridad
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

    LaunchedEffect(Unit) {
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
                // Header fijo
                Header(name = "Luis Alejandro", pendingTasks = pendingTasks.size)

                // SearchBar fijo
                SearchBar()

                // Sección de Prioridad fija
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

                // Contenido principal que ocupa el espacio restante
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (error != null) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        // LazyColumn que ocupa todo el espacio disponible
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Sección de tareas completadas
                            if (completedTasks.isNotEmpty()) {
                                item {
                                    TaskSectionHeader(title = "Completadas")
                                }
                                items(completedTasks) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggleComplete = { /* TODO */ },
                                        onDelete = { /* TODO */ }
                                    )
                                }
                            }

                            // Sección de tareas pendientes
                            if (pendingTasks.isNotEmpty()) {
                                item {
                                    TaskSectionHeader(title = "Por hacer")
                                }
                                items(pendingTasks) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggleComplete = { /* TODO */ },
                                        onDelete = { /* TODO */ }
                                    )
                                }
                            }

                            // Espaciador para asegurar que la paginación no se superponga con el FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }

                // Controles de paginación fijos en la parte inferior
                if (previousPageUrl != null || nextPageUrl != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                previousPageUrl?.let { url ->
                                    loadTasks(url = url)
                                }
                            },
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
                            onClick = {
                                nextPageUrl?.let { url ->
                                    loadTasks(url = url)
                                }
                            },
                            enabled = nextPageUrl != null
                        ) {
                            Text("Siguiente →")
                        }
                    }
                }
            }
        }
    )
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

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onToggleComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    Column {
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

        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (tasks.isEmpty()) 0.dp else 100.dp, max = 200.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = { onToggleComplete(task) },
                        onDelete = { onDelete(task) }
                    )
                }
            }
        }
    }
}