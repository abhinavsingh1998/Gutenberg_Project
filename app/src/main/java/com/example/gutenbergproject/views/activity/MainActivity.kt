package com.example.gutenbergproject.views.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.gutenbergproject.R
import com.example.gutenbergproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityMainBinding

    // Double-back press variables
    private var doubleBackToExitPressedOnce = false
    private val backPressHandler = Handler(Looper.getMainLooper())
    private val backPressRunnable = Runnable {
        doubleBackToExitPressedOnce = false
    }

    // NavController using lazy init (better for performance)
    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate and set the root view using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Handle system back press.
     * - If user is on HomeFragment, require double press to exit.
     * - Otherwise, navigate up in the back stack normally.
     */
    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        val currentDestination = navController.currentDestination?.id

        if (currentDestination == R.id.homeFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed() // Exit the app
            } else {
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

                // Reset flag after 2 seconds
                backPressHandler.postDelayed(backPressRunnable, 2000)
            }
        } else {
            super.onBackPressed() // Pop back stack normally
        }
    }

    /**
     * Clean up any pending back press reset callbacks
     */
    override fun onDestroy() {
        super.onDestroy()
        backPressHandler.removeCallbacks(backPressRunnable)
    }
}