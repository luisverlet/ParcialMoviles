package com.example.parcialmoviles.components.priorityDropdown

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class PriorityOption(
    val label: String,
    val value: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityDropdown(
    selectedPriority: PriorityOption?,
    onPrioritySelected: (PriorityOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val priorities = listOf(
        PriorityOption("Baja", 1),
        PriorityOption("Media", 2),
        PriorityOption("Alta", 3)
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedPriority?.label ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text("Prioridad") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.label) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}