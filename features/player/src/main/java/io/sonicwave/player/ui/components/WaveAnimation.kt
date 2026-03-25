package io.sonicwave.player.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun WaveAnimation(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 12,
    color: Color = MaterialTheme.colorScheme.primary
) {

    val barSpecs = remember(barCount) {
        List(barCount) {
            val targetHeight = Random.nextFloat().coerceIn(0.4f, 1f)
            val duration = Random.nextInt(300, 700)
            targetHeight to duration
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")

    val animatedHeights = barSpecs.mapIndexed { index, (targetHeight, duration) ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = targetHeight,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "BarHeight_$index"
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width / (barCount * 2)
        val cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)

        animatedHeights.forEachIndexed { index, animState ->
            val targetValue = if (isPlaying) animState.value else 0.1f
            val barHeight = size.height * targetValue

            val x = (index * (barWidth * 2)) + (barWidth / 2)
            val y = (size.height - barHeight) / 2

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}