package kg.autojuuguch.automoikakg.utils

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class PermissionUtils(private val context : Fragment) {

    private var onRequest : (granted : Boolean) -> Unit = {}


    private val launchedPermissions =
        context.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onRequest.invoke(isGranted)
        }

    fun checkNotificationsPermission(block : (value : Boolean) -> Unit) {
        onRequest = block
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launchedPermissions.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else onRequest.invoke(true)
    }

    fun checkGalleryPermission(block : (value : Boolean) -> Unit) {
        onRequest = block
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            launchedPermissions.launch(Manifest.permission.READ_MEDIA_IMAGES)
        else launchedPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    fun setRequestCallback(block : (value : Boolean) -> Unit){

    }

    fun unregister(){
        launchedPermissions.unregister()
    }
}