package apc.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apc.android.log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log(Intent())
    }
}