package com.example.taskscheduler.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskscheduler.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
//    private val navController by lazy {
//    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//    navHostFragment.navController
//}
    private var navController: NavController? = null
//    val args by navArgs<MainActivityArgs>()
//    val auth = Firebase.auth
    private val topLevelDestinations = setOf(getTabsDestination(), getLoginDestination())

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = getRootNavController()
        prepareRootNavController(isSignedIn(), navController)
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.boardListFragment,
//            R.id.loginFragment,
//            R.id.myInvitesFragment
//        ))
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.loginFragment, R.id.registrationFragment, R.id.forgotPasswordFragment -> navView.visibility = View.GONE
//                else -> navView.visibility = View.VISIBLE
//            }
//
//        }
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        NavigationUI.setupWithNavController(navView, navController)
//        navView.setupWithNavController(navController)


        if (savedInstanceState == null) {
//            val fragment = LoginFragment.newInstance()
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container, fragment)
//                .addToBackStack(null)
//                .commit()
        }

    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        navController = null
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    }

    private fun getRootNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        return navHost.navController
    }

    private fun onNavControllerActivated(navController: NavController) {
        if (this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener)
        navController.addOnDestinationChangedListener(destinationListener)
        this.navController = navController
    }

    private fun prepareRootNavController(isSignedIn: Boolean, navController: NavController) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination(
            if (isSignedIn) {
                getTabsDestination()
            } else
                getLoginDestination()
        )
        navController.graph = graph
    }

    private fun isSignedIn(): Boolean {
        val bundle = intent.extras ?: throw IllegalStateException("No required arguments")
        val args = MainActivityArgs.fromBundle(bundle)
        return args.isSignedIn
    }

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        val graph = destination.parent ?: return false
        val startDestinations = topLevelDestinations + graph.startDestinationId
        return startDestinations.contains(destination.id)
    }

    private val destinationListener = NavController.OnDestinationChangedListener {_, destination, arguments ->
        supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))

    }

    private fun getMainNavigationGraphId() = R.navigation.my_navigation

    private fun getTabsDestination() = R.id.tabsFragment

    private fun getLoginDestination() = R.id.loginFragment


    override fun onBackPressed() {

        if (isStartDestination(navController?.currentDestination)) {
            super.getOnBackPressedDispatcher().onBackPressed()
        } else {
            navController?.popBackStack()
        }
    }
}