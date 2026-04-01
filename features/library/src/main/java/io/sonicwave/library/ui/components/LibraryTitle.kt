package io.sonicwave.library.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.sonicwave.library.R

@Composable
fun LibraryTitle(
    text: String = stringResource(R.string.your_library),
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontSize = MaterialTheme.typography.displayMedium.fontSize,
        modifier = modifier
            .padding(top = 5.dp, bottom = 15.dp)
    )
}