package com.example.parcialmoviles.components.filter

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parcialmoviles.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    completedFilter: Boolean?,
    onCompletedFilterChange: (Boolean?) -> Unit,
    ordering: String?,
    onOrderingChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = {},
            active = false,
            onActiveChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            },
            placeholder = { Text(stringResource(R.string.search_tasks)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {}

        if (showFilters) {
            FilterOptions(
                completedFilter = completedFilter,
                onCompletedFilterChange = onCompletedFilterChange,
                ordering = ordering,
                onOrderingChange = onOrderingChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        FilterButton(
            showFilters = showFilters,
            onToggleFilters = { showFilters = !showFilters },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun FilterOptions(
    completedFilter: Boolean?,
    onCompletedFilterChange: (Boolean?) -> Unit,
    ordering: String?,
    onOrderingChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.status),
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = completedFilter == null,
                    onClick = { onCompletedFilterChange(null) },
                    label = { Text(stringResource(R.string.all)) }
                )
                FilterChip(
                    selected = completedFilter == true,
                    onClick = { onCompletedFilterChange(true) },
                    label = { Text(stringResource(R.string.completed)) }
                )
                FilterChip(
                    selected = completedFilter == false,
                    onClick = { onCompletedFilterChange(false) },
                    label = { Text(stringResource(R.string.pending)) }
                )
            }

            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = ordering == null,
                    onClick = { onOrderingChange(null) },
                    label = { Text(stringResource(R.string.default_sort)) }
                )
                FilterChip(
                    selected = ordering == "-priority",
                    onClick = { onOrderingChange("-priority") },
                    label = { Text(stringResource(R.string.high_to_low)) }
                )
                FilterChip(
                    selected = ordering == "priority",
                    onClick = { onOrderingChange("priority") },
                    label = { Text(stringResource(R.string.low_to_high)) }
                )
            }
        }
    }
}

@Composable
private fun FilterButton(
    showFilters: Boolean,
    onToggleFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onToggleFilters,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = Icons.Default.Sort,
            contentDescription = if (showFilters) stringResource(R.string.hide_filters)
            else stringResource(R.string.show_filters),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (showFilters) stringResource(R.string.hide_filters)
        else stringResource(R.string.filter))
    }
}