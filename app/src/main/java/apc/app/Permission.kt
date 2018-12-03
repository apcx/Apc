@file:Suppress("SpellCheckingInspection")

package apc.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

val overlayIntent
    @SuppressLint("InlinedApi")
    get(): Intent {
        val packageName = BuildConfig.APPLICATION_ID
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri())
        when (Build.MANUFACTURER.toLowerCase()) {
            "meizu" -> intent.putExtra("packageName", packageName)
        }
        return intent
    }

fun Context.requestRomOverlay(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val packageName = BuildConfig.APPLICATION_ID
        when (Build.MANUFACTURER.toLowerCase()) {
            "vivo" -> Intent().setClassName("com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity")
                    .putExtra("packagename", packageName)
            "smartisan" -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:$packageName".toUri())
            else -> null
        }?.let {
            try {
                startActivity(it)
                return true
            } catch (e: RuntimeException) {
            }
        }
    }
    return false
}

@SuppressLint("StaticFieldLeak")
private var alertWindow: View? = null

@Suppress("unused")
@RequiresApi(Build.VERSION_CODES.M)
fun Context.testOverlay() {
    val tag = "canDrawOverlays"
    Log.i(tag, "${Settings.canDrawOverlays(this)}")
    if (alertWindow == null) {
        val manager = getSystemService(WindowManager::class.java)
        alertWindow = Button(this).apply {
            text = tag
            isAllCaps = false
            manager.addView(this, WindowManager.LayoutParams().apply {
                @Suppress("DEPRECATION")
                type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                format = PixelFormat.TRANSPARENT
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            })
            setOnClickListener {
                manager.removeView(it)
                alertWindow = null
            }
        }
    }
}