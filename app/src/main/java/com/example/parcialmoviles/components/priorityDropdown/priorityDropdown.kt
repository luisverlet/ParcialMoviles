package com.example.parcialmoviles.components.priorityDropdown

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parcialmoviles.R

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
        PriorityOption(stringResource(R.string.low_priority), 1),
        PriorityOption(stringResource(R.string.medium_priority), 2),
        PriorityOption(stringResource(R.string.high_priority), 3)
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
            label = { Text(stringResource(R.string.priority)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.priority)
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