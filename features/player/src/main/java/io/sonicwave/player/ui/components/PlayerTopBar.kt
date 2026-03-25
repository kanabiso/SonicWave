package io.sonicwave.player.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.sonicwave.player.R
import io.sonicwave.player.ui.model.PlayerOptions

// Top row with minimize and more options buttons
@Composable
fun PlayerTopBar(
    onMinimizeClick: () -> Unit,
    onMoreOptionsClick: (PlayerOptions) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Minimize
        IconButton(onClick = { onMinimizeClick()} ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.minimize),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        // More Options
        var showMenu by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                PlayerOptions.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(stringResource(option.label)) },
                        onClick = {
                            onMoreOptionsClick(option)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}
