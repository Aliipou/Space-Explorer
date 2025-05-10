package com.example.spaceexplorer.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.ActivityMainBinding

/**
 * Main activity hosting the navigation components
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        
        // Set up navigation
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        
        navController = navHostFragment.navController
        
        // Configure the action bar
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.spaceImageListFragment)
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}