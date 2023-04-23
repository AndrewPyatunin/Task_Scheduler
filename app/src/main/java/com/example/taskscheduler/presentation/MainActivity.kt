package com.example.taskscheduler.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taskscheduler.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val fragment = LoginFragment.newInstance(
                "s22",
                "email@gmail.com"
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

    }
}