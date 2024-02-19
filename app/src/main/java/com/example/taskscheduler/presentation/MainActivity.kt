package com.example.taskscheduler.presentation

import android.content.Context
import android.content.SharedPreferences
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
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val databaseUsersRef = Firebase.database.getReference("Users")
    private val topLevelDestinations = setOf(getWelcomeDestination(), getLoginDestination())
    private var navController: NavController? = null
    private var pref: SharedPreferences? = null
    private var user = auth.currentUser

    companion object {
        const val USER_ID_KEY = "user_id"
        const val BACKGROUND_IMAGES_KEY = "background_images"
    }

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppCompat_Main)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        pref = getSharedPreferences("Id", MODE_PRIVATE)
        MyDatabaseConnection.userId = pref?.getString(USER_ID_KEY, "")
        MyDatabaseConnection.backgroundImages = Converter.fromStringToList(pref?.getString(
            BACKGROUND_IMAGES_KEY, null))
        val navController = getRootNavController()
        prepareRootNavController(isSignedIn = user != null, navController = navController)
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

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        navController = null
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    }

    private fun hideKeyboard(editText: EditText) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun getRootNavController(): NavController {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
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

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        val graph = destination.parent ?: return false
        val startDestinations = topLevelDestinations + graph.startDestinationId
        return startDestinations.contains(destination.id)
    }

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, arguments ->
            supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))
            supportActionBar?.title = prepareTitle(destination.label, arguments)
        }


    private fun prepareTitle(label: CharSequence?, arguments: Bundle?): String {

        if (label == null) return ""
        val title = StringBuffer()
        val fillInPattern = Pattern.compile("\\{(.+?)\\}")
        val matcher = fillInPattern.matcher(label)
        while (matcher.find()) {
            val argName = matcher.group(1)
            if (arguments != null && arguments.containsKey(argName)) {
                matcher.appendReplacement(title, "")
                title.append(arguments[argName].toString())
            } else {
                throw IllegalArgumentException(
                    "Could not find $argName in $arguments to fill label $label"
                )
            }
        }
        matcher.appendTail(title)
        return title.toString()
    }

    override fun onPause() {
        super.onPause()
        setUserOnline(false)
    }

    override fun onResume() {
        super.onResume()
        setUserOnline(true)
    }

    private fun setUserOnline(isOnline: Boolean) {
        auth.currentUser?.let {
            databaseUsersRef.child(it.uid).child("onlineStatus").setValue(isOnline)
            val edit = pref?.edit()
            val convertedBackgroundImage = Converter.fromListToString(MyDatabaseConnection.backgroundImages)
            edit?.putString(BACKGROUND_IMAGES_KEY, convertedBackgroundImage)
            edit?.putString(USER_ID_KEY, it.uid)
            edit?.apply()
        }

    }

    private fun getMainNavigationGraphId() = R.navigation.main_navigation

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