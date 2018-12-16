package apc.android

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.preference.PreferenceManager
import android.view.LayoutInflater
import androidx.core.app.ComponentActivity
import androidx.databinding.ViewDataBinding

val Context.activity
    get(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

val Context.sp get() = PreferenceManager.getDefaultSharedPreferences(this)!!

inline fun <reified T : ViewDataBinding> ComponentActivity.bind() =
        (T::class.java.getDeclaredMethod("inflate", LayoutInflater::class.java)(null, layoutInflater) as T).also {
            it.setLifecycleOwner(this)
            setContentView(it.root)
        }