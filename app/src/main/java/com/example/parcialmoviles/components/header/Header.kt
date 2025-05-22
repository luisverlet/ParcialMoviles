package com.example.parcialmoviles.components.header

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header(name: String, pendingTasks: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hola $name",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$pendingTasks Tareas Pendientes",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}