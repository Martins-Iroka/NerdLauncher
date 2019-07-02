package com.martdev.android.nerdlauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class SingleFragmentActivity : AppCompatActivity() {

    internal abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = supportFragmentManager
        var fragment = manager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = createFragment()
            manager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }
}
