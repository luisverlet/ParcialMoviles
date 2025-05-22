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
        2 -> Color(0xFFFFF3CD) // Amarillo claro para prioridad media
        3 -> MaterialTheme.colorScheme.errorContainer // Rojo claro para prioridad alta
        else -> MaterialTheme.colorScheme.surface // Color normal para prioridad baja
    }

    val borderColor = when (task.priority) {
        2 -> Color(0xFFFFD60A) // Amarillo más fuerte para el borde
        3 -> MaterialTheme.colorScheme.error // Rojo para el borde
        else -> MaterialTheme.colorScheme.outline // Color normal para el borde
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Indicador de prioridad
                val priorityText = when (task.priority) {
                    3 -> "Alta"
                    2 -> "Media"
                    1 -> "Baja"
                    else -> "Normal"
                }

                Text(
                    text = "Prioridad: $priorityText",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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