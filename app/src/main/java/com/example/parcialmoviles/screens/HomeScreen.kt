package com.example.parcialmoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TodoItem(
    val id: Int,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val todoItems = remember {
        mutableStateListOf(
            TodoItem(
                id = 1,
                title = "Comprar Embutidos",
                description = "Comprar cosas como salchichas, jamon, queso, etc.",
                isCompleted = true
            ),
            TodoItem(
                id = 2,
                title = "Llamar a mi mama",
                description = null,
                isCompleted = false
            )
        )
    }

    val completedTasks = todoItems.filter { it.isCompleted }
    val pendingTasks = todoItems.filter { !it.isCompleted }
    val totalPendingTasks = pendingTasks.size

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir tarea"
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Header(name = "Luis Alejandro", pendingTasks = totalPendingTasks)

                SearchBar()

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
                        count = "05",
                        modifier = Modifier.weight(1f)
                    )
                    PriorityCard(
                        title = "Media",
                        count = "01",
                        modifier = Modifier.weight(1f)
                    )
                    PriorityCard(
                        title = "Baja",
                        count = "00",
                        modifier = Modifier.weight(1f)
                    )
                }

                TaskSection(
                    title = "Completadas",
                    tasks = completedTasks,
                    onToggleComplete = { },
                    onDelete = { }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TaskSection(
                    title = "Por hacer",
                    tasks = pendingTasks,
                    onToggleComplete = { },
                    onDelete = { }
                )
            }
        }
    )
}

@Composable
fun TaskSection(
    title: String,
    tasks: List<TodoItem>,
    onToggleComplete: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
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
                        todoItem = task,
                        onToggleComplete = { onToggleComplete(task) },
                        onDelete = { onDelete(task) }
                    )
                }
            }
        }
    }
}