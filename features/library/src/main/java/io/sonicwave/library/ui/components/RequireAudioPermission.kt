package io.sonicwave.library.ui.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.sonicwave.library.R

@Composable
fun RequireAudioPermission(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (!isGranted && activity != null) {
                isPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionToRequest)
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(permissionToRequest)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGrantedNow = ContextCompat.checkSelfPermission(
                    context,
                    permissionToRequest
                ) == PackageManager.PERMISSION_GRANTED
                hasPermission = isGrantedNow
                if (isGrantedNow) isPermanentlyDenied = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (hasPermission) {
        content()
    } else {
        PermissionDeniedView(
            isPermanentlyDenied = isPermanentlyDenied,
            onRequestPermission = { permissionLauncher.launch(permissionToRequest) },
            onOpenSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
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