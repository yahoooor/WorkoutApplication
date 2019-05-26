package com.example.workoutnotebook

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import kotlin.properties.Delegates
import android.widget.AdapterView.OnItemClickListener



class SelectExerciseActivity : BaseActivity() {

    val TAG: String = "SELECT_EXERCISE_ACT"
    var mDbHelper: DatabaseHandler by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exercise)

        setResult(Activity.RESULT_CANCELED)
        mDbHelper = DatabaseHandler(this)

        val createButton = findViewById(R.id.createExerciseButton) as Button
        createButton.setOnClickListener{
            val createExerciseIntent: Intent = Intent(this, CreateExerciseActivity::class.java)
            startActivityForResult(createExerciseIntent, REQUEST_CODE.CREATE_EXERCISE.ordinal)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE.CREATE_EXERCISE.ordinal){
            if(resultCode == Activity.RESULT_OK ) {
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val workoutId = intent.extras["WORKOUT_ID"] as Long
        val exerciseListView = findViewById(R.id.exerciseListView) as ListView
        val c: Cursor = mDbHelper.getCursorExercisesNotInWorkout(workoutId)
        exerciseListView.adapter = ExerciseCursorAdapter(this, c)
        //TODO: Eventually do multiple selections
        exerciseListView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            // selected item
            //TODO: Determine if this part necessary since id is given
            val viewHolder: ExerciseCursorAdapter.ExerciseViewHolder = view.tag as ExerciseCursorAdapter.ExerciseViewHolder
            val exerciseId: Long? = viewHolder.mExercise?.id
            if(exerciseId != null){
                mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
