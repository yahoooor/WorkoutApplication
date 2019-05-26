package com.example.workoutnotebook

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import kotlin.properties.Delegates
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.widget.*
import android.widget.Toast


class WorkoutActivity : BaseActivity() {

    val TAG: String = "WORKOUT_ACT"
    var mCurWorkoutId: Long = -1L

    var mDbHelper: DatabaseHandler by Delegates.notNull()
    var mCursor: Cursor by Delegates.notNull()
    var mListView: ListView by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        mDbHelper = DatabaseHandler(this)

        val actionWorkout: String? = intent.getStringExtra(EXTRA_KEYS.WORKOUT_STARTUP_ACTION)
        val latestWorkoutId: Long = mDbHelper.getLastWorkoutId()

        //1. No workout logs - must make new workout
        //2. Use an existing log
        //3. Create a new log - user asked for this by pressing next on last workout
        //4. Default to latest log
        if(latestWorkoutId == -1L){
            mCurWorkoutId = mDbHelper.insertWorkout(workout = Workout())
        }
        else if(actionWorkout == WORKOUT_ACTION.EXISTING_WORKOUT.name){
            mCurWorkoutId = intent.getLongExtra(EXTRA_KEYS.WORKOUT_ID, -1L)
        }
        else if(actionWorkout == WORKOUT_ACTION.NEW_WORKOUT.name){
            mCurWorkoutId = mDbHelper.insertWorkout(workout = Workout())
        }
        else mCurWorkoutId = latestWorkoutId

        val buttonAddExercise = findViewById(R.id.btnAddExercise) as Button
        buttonAddExercise.setOnClickListener{
            val intentAddExercise: Intent = Intent(this, SelectExerciseActivity::class.java)
            intentAddExercise.putExtra(EXTRA_KEYS.WORKOUT_ID, mCurWorkoutId)
            startActivityForResult(intentAddExercise, REQUEST_CODE.ADD_EXERCISE.ordinal)
        }



        val buttonPrevWorkout = findViewById(R.id.btn_prev_workout) as ImageButton
        buttonPrevWorkout.setOnClickListener {
            // TODO: Make this a while loop until id < 1, to correctly handle case when workout deleted
            if(mDbHelper.isValidWorkout(mCurWorkoutId-1)){
                mCurWorkoutId--
                val intentPrevWorkout = Intent(this, WorkoutActivity::class.java)
                intentPrevWorkout.putExtra(EXTRA_KEYS.WORKOUT_STARTUP_ACTION, WORKOUT_ACTION.EXISTING_WORKOUT.name)
                intentPrevWorkout.putExtra(EXTRA_KEYS.WORKOUT_ID, mCurWorkoutId)
                startActivity(intentPrevWorkout)
                finish()
            }
            else{
                Toast.makeText(this, "No more workouts", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonNextWorkout = findViewById(R.id.btn_next_workout) as ImageButton
        buttonNextWorkout.setOnClickListener {
            // TODO: Use a while loop to jump over deleted workouts
            //If next workout exists go to it, otherwise create new empty workout
            val intentNextWorkout = Intent(this, WorkoutActivity::class.java)
            if(mDbHelper.isValidWorkout(mCurWorkoutId+1)){
                mCurWorkoutId++
                intentNextWorkout.putExtra(EXTRA_KEYS.WORKOUT_STARTUP_ACTION, WORKOUT_ACTION.EXISTING_WORKOUT.name)
                intentNextWorkout.putExtra(EXTRA_KEYS.WORKOUT_ID, mCurWorkoutId)
            }
            else{
                intentNextWorkout.putExtra(EXTRA_KEYS.WORKOUT_STARTUP_ACTION, WORKOUT_ACTION.NEW_WORKOUT.name)
                Toast.makeText(this, "New Workout created", Toast.LENGTH_SHORT).show()
            }
            startActivity(intentNextWorkout)
            finish()
        }

        // TODO: Allow user to pick workout from calender
        val buttonPickWorkout = findViewById(R.id.btn_pick_workouts) as Button
        buttonPickWorkout.text = mDbHelper.getWorkout(workoutId = mCurWorkoutId)?.date
    }

    override fun onStart() {
        super.onStart()

        mListView = findViewById(R.id.listViewWorkoutExercises) as ListView
        mCursor = mDbHelper.getCursorExercisesFromWorkout(workoutId = mCurWorkoutId)
        mListView.adapter = WorkoutCursorAdapter(this, mCursor, mDbHelper, mCurWorkoutId)

        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // selected item
            val viewHolder = view.tag as ExerciseCursorAdapter.ExerciseViewHolder
            val intentAddSet: Intent = Intent(this, AddSetActivity::class.java)
            intentAddSet.putExtra(EXTRA_KEYS.WORKOUT_ID, mCurWorkoutId)
            intentAddSet.putExtra(EXTRA_KEYS.EXERCISE_ID, viewHolder.mExercise.id)
            startActivityForResult(intentAddSet, REQUEST_CODE.ADD_SET.ordinal)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE.ADD_EXERCISE.ordinal){
            if(resultCode == Activity.RESULT_OK ) {
                //TODO: Do I need to do anything here?
            }
        }
        if (requestCode == REQUEST_CODE.ADD_SET.ordinal){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Set added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mCursor.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDbHelper.close()
    }
}
