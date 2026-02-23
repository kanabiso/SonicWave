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
fun RequirePermission(
    permission: String,
    onGrantedContent: @Composable () -> Unit,
    onDeniedContent: @Composable (
        isPermanentlyDenied: Boolean,
        requestPermission: () -> Unit,
        openSettings: () -> Unit
    ) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (!isGranted && activity != null) {
                isPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
        }
    )

    LaunchedEffect(key1 = permission) {
        if (!hasPermission) {
            permissionLauncher.launch(permission)
        }
    }

    DisposableEffect(lifecycleOwner, permission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGrantedNow = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
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
        onGrantedContent()
    } else {
        onDeniedContent(
            isPermanentlyDenied,
            { permissionLauncher.launch(permission) },
            {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
}