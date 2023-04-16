import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun LocationPermission(permissionCallback: (Boolean) -> Unit) {
    val context = LocalContext.current

    val isPermissionGranted = checkIfPermissionGranted(context, ACCESS_FINE_LOCATION)

    if (isPermissionGranted) {
        permissionCallback.invoke(true)
    } else {

        val launchPermission =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
                if (isPermissionGranted) {
                    permissionCallback.invoke(true)
                } else {
                    permissionCallback.invoke(false)
                }
            }

        val showPermissionRational = shouldShowPermissionRationale(context, ACCESS_FINE_LOCATION)

        if (showPermissionRational) {
            LaunchedEffect(showPermissionRational) {
                launchPermission.launch(ACCESS_FINE_LOCATION)
            }
        } else {
            SideEffect {
                launchPermission.launch(ACCESS_FINE_LOCATION)
            }
        }
    }
}

fun checkIfPermissionGranted(context: Context, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED)
}

fun shouldShowPermissionRationale(context: Context, permission: String): Boolean {
    val activity = context as Activity?
    return ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)
}
