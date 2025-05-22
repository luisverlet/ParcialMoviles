package com.example.parcialmoviles.components.confirmationDialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ConfirmationDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    confirmButtonText: String = "Confirmar",
    cancelButtonText: String = "Cancelar",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text(cancelButtonText)
                }
            }
        )
    }
}