package com.example.parcialmoviles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcialmoviles.model.Task

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = when (task.priority) {
        3 -> MaterialTheme.colorScheme.errorContainer // Alta - Rojo
        2 -> Color(0xFFFFF3CD) // Media - Amarillo
        1 -> MaterialTheme.colorScheme.primaryContainer // Baja - Verde
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when (task.priority) {
        3 -> MaterialTheme.colorScheme.error // Alta - Rojo
        2 -> Color(0xFFFFD60A) // Media - Amarillo
        1 -> MaterialTheme.colorScheme.primary // Baja - Verde
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                )
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                // Indicador de prioridad con colores
                val (priorityText, priorityColor) = when (task.priority) {
                    3 -> "Alta" to MaterialTheme.colorScheme.error
                    2 -> "Media" to Color(0xFFFFB000)
                    1 -> "Baja" to MaterialTheme.colorScheme.primary
                    else -> "Normal" to MaterialTheme.colorScheme.onSurfaceVariant
                }

                Text(
                    text = "Prioridad: $priorityText",
                    fontSize = 12.sp,
                    color = priorityColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onToggleComplete() }
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}