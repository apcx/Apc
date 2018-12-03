package apc.android

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import androidx.core.app.ComponentActivity
import androidx.databinding.ViewDataBinding

val Context.sp get() = PreferenceManager.getDefaultSharedPreferences(this)!!

inline fun <reified T : ViewDataBinding> ComponentActivity.bind() =
        (T::class.java.getDeclaredMethod("inflate", LayoutInflater::class.java)(null, layoutInflater) as T).also {
            it.setLifecycleOwner(this)
            setContentView(it.root)
        }