package com.app.chhatrasal.zersey

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.*

class MainActivity : AppCompatActivity(), HelperInterface {
    override fun updateTitle(title: String) {
        toolbarTitleTextView.text = title
    }


    private lateinit var toolbarTitleTextView: TextView
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val toolbar: Toolbar = findViewById(R.id.custom_toolbar)
        toolbarTitleTextView = toolbar.findViewById(R.id.toolbar_title_text_view)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {}

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            drawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            when (menuItem.itemId) {
                R.id.questionnaire_menu_option -> {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container_layout, QuestionniareFragment())
                            .commitAllowingStateLoss()
                }
                R.id.add_question_menu_option -> {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container_layout, AddQuestionFragment(), null)
                            .addToBackStack("backStack")
                            .commitAllowingStateLoss()
                }
            }
            true
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_layout, QuestionniareFragment())
                .commitAllowingStateLoss()
    }


}
