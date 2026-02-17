package io.sonicwave.common.utils

import android.annotation.SuppressLint
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("DefaultLocale")
fun Long.formatAsDuration(): String {
    return this.milliseconds.toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}