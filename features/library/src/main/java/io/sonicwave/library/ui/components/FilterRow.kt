package io.sonicwave.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.sonicwave.library.R

@Composable
fun FilterRow(
    isListLayout: Boolean,
    onFilterClick: () -> Unit,
    onLayoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onFilterClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.filter_list),
                contentDescription = null
            )
        }
        IconButton(
            onClick = { onLayoutClick() }
        ) {
            Icon(
                painter = painterResource(id =
                    if (isListLayout) R.drawable.border_all_24 else R.drawable.density_small_24),
                contentDescription = null
            )
        }
    }
}