package com.example.workoutnotebook

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment

class SettingsFragment : PreferenceFragment() {

    val TAG = "SETTINGS_FRAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mDbHelper: DatabaseHandler = DatabaseHandler(context = activity.applicationContext)

        addPreferencesFromResource(R.xml.settings)

        val buttonDbReset = findPreference(getString(R.string.key_db_reset))
        buttonDbReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mDbHelper.dropTables()
            mDbHelper.createTables()
            true
        }
    }
}