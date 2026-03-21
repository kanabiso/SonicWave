package io.sonicwave.library.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.sonicwave.library.R
import io.sonicwave.permissions.RequireMultiplePermissions

@Composable
fun RequireAudioPermission(
    content: @Composable () -> Unit
) {
    val permissionsToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    RequireMultiplePermissions(
        permissions = permissionsToRequest,
        onGrantedContent = content,
        onDeniedContent = { isPermanentlyDenied, requestPermissions, openSettings ->
            PermissionDeniedView(
                isPermanentlyDenied = isPermanentlyDenied,
                onRequestPermission = requestPermissions,
                onOpenSettings = openSettings
            )
        }
    )
}

@Composable
private fun PermissionDeniedView(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.permission_neded_info), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        if (isPermanentlyDenied) {
            Button(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.open_settings))
            }
        } else {
            Button(onClick = onRequestPermission) {
                Text(text = stringResource(R.string.grant_permission))
            }
        }
    }
}