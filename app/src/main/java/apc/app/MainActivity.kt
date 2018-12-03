package apc.app

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import apc.android.bind
import apc.app.databinding.ActivityMainBinding
import org.jetbrains.anko.startActivity

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(this)[MainVm::class.java]
        bind<ActivityMainBinding>().vm = vm
    }

    fun toBuild(v: View) {
        startActivity<BuildActivity>()
    }

    fun requestOverlay(v: View) {
        startActivity(overlayIntent)
    }

    override fun onResume() {
        super.onResume()
        vm.canDrawOverlays.value = Build.VERSION.SDK_INT <= 22 || Settings.canDrawOverlays(this)
    }
}

class MainVm : ViewModel() {
    val canDrawOverlays = MutableLiveData<Boolean>()
}