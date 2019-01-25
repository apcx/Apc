package apc.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction

@Suppress("unused")
inline fun <reified T : Fragment> Context.startFragment(arguments: Bundle? = null) {
    val intent = Intent(T::class.java.name, null, this, FragActivity::class.java)
    arguments?.let(intent::putExtras)
    startActivity(intent)
}

class FragActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = Class.forName(intent.action!!).newInstance() as Fragment
        fragment.arguments = intent.extras
        supportFragmentManager.transaction(true, true) { add(android.R.id.content, fragment) }
    }
}