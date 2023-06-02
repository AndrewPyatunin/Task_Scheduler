package com.example.taskscheduler.presentation

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R

class MainActivity : AppCompatActivity() {
//    val auth = Firebase.auth
//    private val navController by lazy {
//    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//    navHostFragment.navController
//}
    private var navController: NavController? = null
//    val args by navArgs<MainActivityArgs>()
//    val auth = Firebase.auth
    private val topLevelDestinations = setOf(getWelcomeDestination(), getLoginDestination())

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
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = getRootNavController()
        prepareRootNavController(isSignedIn(), navController)
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getLocalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(editText: EditText) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
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
                getWelcomeDestination()
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

    private fun getMainNavigationGraphId() = R.navigation.main_navigation

    private fun getTabsDestination() = R.id.tabsFragment

    private fun getLoginDestination() = R.id.loginFragment

    private fun getWelcomeDestination() = R.id.welcomeFragment


    override fun onBackPressed() {

        if (isStartDestination(navController?.currentDestination)) {
            super.getOnBackPressedDispatcher().onBackPressed()
        } else {
            navController?.popBackStack()
        }
    }
}