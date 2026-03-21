package io.sonicwave.permissions


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun RequireMultiplePermissions(
    permissions: List<String>,
    onGrantedContent: @Composable () -> Unit,
    onDeniedContent: @Composable (
        isPermanentlyDenied: Boolean,
        requestPermissions: () -> Unit,
        openSettings: () -> Unit
    ) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    // check permissions
    var allPermissionsGranted by remember(permissions) {
        mutableStateOf(permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }

    var isPermanentlyDenied by remember { mutableStateOf(false) }

    // contract for requesting multi permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val allGranted = result.values.all { it }
            allPermissionsGranted = allGranted

            if (!allGranted && activity != null) {
                // if any permission is not granted, check if it was permanently denied
                val anyPermanentlyDenied = result.entries
                    .filter { !it.value } // take only denied permissions
                    .any { !ActivityCompat.shouldShowRequestPermissionRationale(activity, it.key) }
                isPermanentlyDenied = anyPermanentlyDenied
            }
        }
    )

    LaunchedEffect(key1 = permissions) {
        if (!allPermissionsGranted) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    DisposableEffect(lifecycleOwner, permissions) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val allGrantedNow = permissions.all {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                allPermissionsGranted = allGrantedNow
                if (allGrantedNow) isPermanentlyDenied = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (allPermissionsGranted) {
        onGrantedContent()
    } else {
        onDeniedContent(
            isPermanentlyDenied,
            { permissionLauncher.launch(permissions.toTypedArray()) },
            {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
}