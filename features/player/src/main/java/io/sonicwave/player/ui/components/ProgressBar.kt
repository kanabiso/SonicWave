package io.sonicwave.player.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.sonicwave.common.utils.formatTime

@Composable
fun ProgressBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit
) {
    var draggingPosition by remember { mutableStateOf<Float?>(null) }

    var lastSeekPosition by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(currentPositionMs) {
        if (lastSeekPosition != null && Math.abs(currentPositionMs - lastSeekPosition!!) < 1000) {
            lastSeekPosition = null
        }
    }

    val sliderValue = draggingPosition
        ?: lastSeekPosition?.toFloat()
        ?: currentPositionMs.toFloat()

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = sliderValue.coerceIn(0f, durationMs.toFloat().coerceAtLeast(1f)),
            onValueChange = { draggingPosition = it },
            onValueChangeFinished = {
                draggingPosition?.let {
                    val target = it.toLong()
                    lastSeekPosition = target //
                    onSeek(target)
                    draggingPosition = null
                }
            },
            valueRange = 0f..durationMs.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPositionMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}