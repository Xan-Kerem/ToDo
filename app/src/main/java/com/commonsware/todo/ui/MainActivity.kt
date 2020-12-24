package com.commonsware.todo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.commonsware.todo.R
import com.commonsware.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // set up app bar up button
        supportFragmentManager.findFragmentById(binding.navHost.id)?.findNavController()
            ?.let { nav ->
                appBarConfiguration = AppBarConfiguration(nav.graph)
                setupActionBarWithNavController(nav, appBarConfiguration)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
                false
            }
        }

    }

    override fun onSupportNavigateUp() =
        navigateUp(findNavController(binding.navHost.id), appBarConfiguration)
}
