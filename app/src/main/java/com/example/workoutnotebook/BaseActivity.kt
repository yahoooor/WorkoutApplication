package com.example.workoutnotebook

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

/**
 * A class that implements the ActionBar
 */

open class BaseActivity : AppCompatActivity(){
    override fun onStart() {
        super.onStart()
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(settingsIntent, REQUEST_CODE.SETTINGS.ordinal)
        return true
    }
}