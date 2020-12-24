package com.commonsware.todo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.commonsware.todo.R
import com.commonsware.todo.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.aboutToolbar)

        binding.aboutToolbar.title = getString(R.string.app_name)
        binding.aboutWv.loadUrl("file:///android_asset/about.html")



    }
}