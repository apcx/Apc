package apc.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import apc.android.log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log(Intent())
    }
}