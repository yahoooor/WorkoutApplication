package com.example.workoutnotebook

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlin.properties.Delegates
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import kotlin.collections.ArrayList

//test
class AddSetActivity : BaseActivity() {
    val TAG = "ADD_SET_ACT"

    var mReps: Int = 0
    var mWeight: Float = 0.0f
    var mSelectedWeightUnit: String = ""

    var mExerciseId: Long by Delegates.notNull<Long>()
    var mWorkoutId: Long by Delegates.notNull<Long>()

    var mDbHelper: DatabaseHandler by Delegates.notNull<DatabaseHandler>()

    var mTextViewPreviousSets: TextView by Delegates.notNull<TextView>()
    var mTextViewCurrentSets: TextView by Delegates.notNull<TextView>()
    var mTextViewReps: TextView by Delegates.notNull<TextView>()
    var mTextViewWeight: TextView by Delegates.notNull<TextView>()
    var mSpinnerWeightUnit: Spinner by Delegates.notNull<Spinner>()
    var mChoicesWeightUnit: Array<String> by Delegates.notNull<Array<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_set)

        mDbHelper = DatabaseHandler(this)

        mExerciseId = intent.getLongExtra(EXTRA_KEYS.EXERCISE_ID, -1L)
        mWorkoutId = intent.getLongExtra(EXTRA_KEYS.WORKOUT_ID, -1L)

        // Change activity title to exercise name
        // So user knows which exercise they are adding sets to
        val exercise = mDbHelper.getExercise(mExerciseId)
        title = exercise!!.name.toString()

        //Previous and current logs textview
        mTextViewPreviousSets = findViewById(R.id.text_previous_sets) as TextView
        mTextViewCurrentSets = findViewById(R.id.text_current_sets) as TextView

        // Reps textview and buttons
        mTextViewReps = findViewById(R.id.text_num_reps) as TextView

        mTextViewReps.text = mReps.toString()
        mTextViewReps.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isNotEmpty()) mReps = s.toString().toInt()
                else mReps = 0
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        val buttonAddRep = findViewById(R.id.btn_add_rep) as ImageButton
        buttonAddRep.setOnClickListener {
            mReps++
            mTextViewReps.text = mReps.toString()
        }
        val buttonMinusRep = findViewById(R.id.btn_minus_rep) as ImageButton
        buttonMinusRep.setOnClickListener {
            if(mReps > 0){
                mReps--
                mTextViewReps.text = mReps.toString()
            }
        }

        // Weight textview and buttons
        val res = resources
        mChoicesWeightUnit = res.getStringArray(R.array.weight_units)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val preferredWeightUnit = sharedPreferences.getString(
                getString(R.string.key_weight_units),
                getString(R.string.weight_unit_default)
        )
        val weightIncrement = sharedPreferences.getString(
                getString(R.string.key_increment_weight_amount),
                getString(R.string.default_increment_weight_amount)
        ).toFloat()
        mSelectedWeightUnit = preferredWeightUnit
        mSpinnerWeightUnit = this.findViewById(R.id.spinner_weight_unit) as Spinner
        val weightUnitDataAdapter = ArrayAdapter(this, R.layout.spinner_weight_unit_item, mChoicesWeightUnit )
        mSpinnerWeightUnit.adapter = weightUnitDataAdapter
        mSpinnerWeightUnit.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                mSelectedWeightUnit = parentView.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
        mTextViewWeight = findViewById(R.id.text_num_weight) as TextView

        mTextViewWeight.text = mWeight.toString()
        mTextViewWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isNotEmpty()) mWeight = s.toString().toFloat()
                else mWeight = 0f
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        val buttonAddWeight = findViewById(R.id.btn_add_weight) as ImageButton
        buttonAddWeight.setOnClickListener {
            mWeight += weightIncrement
            mTextViewWeight.text = mWeight.toString()
        }
        val buttonMinusWeight = findViewById(R.id.btn_minus_weight) as ImageButton
        buttonMinusWeight.setOnClickListener {
            if(mWeight > 0){
                mWeight -= weightIncrement
                mTextViewWeight.text = mWeight.toString()
            }
        }

        // Pre-populates text views for first time
        updateTextViews(true)

        // Save button
        val buttonSaveSet = findViewById(R.id.btn_save_set) as Button
        buttonSaveSet.setOnClickListener {
            if(mReps == 0) mTextViewReps.error = "Can't have 0 reps"
            else{
                if (mExerciseId == -1L || mWorkoutId == -1L){
                    // TODO: Log error...?
                }
                else{
                    mDbHelper.insertSet(
                            SetEntry(
                                    reps = mReps,
                                    weight = mWeight,
                                    exerciseId = mExerciseId,
                                    workoutId = mWorkoutId,
                                    weightUnit = mSelectedWeightUnit
                            )
                    )
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

        // Undo Button
        val buttonUndoSet = findViewById(R.id.btn_undo_set) as Button
        buttonUndoSet.setOnClickListener {
            mDbHelper.deleteSets(workoutId = mWorkoutId, exerciseId = mExerciseId, mostRecentOnly = true)
            updateTextViews()
        }
    }

    /**
     * Helper function that updates 5 textviews
     * -mTextViewPreviousSets which displays the users sets from the last time they did the exercise
     * -mTextViewCurrentSets which displays the current sets of this exercise
     * -mTextViewReps which displays the current reps of the set
     * -mTextViewWeight which displays the current weight of the set
     * -mSpinnerWeightUnit which displays the weight unit used
     *
     * Note: Last 3 TextViews will only be updated if the "firstRun" flag is set to true
     * They will pre-populated with the weights and reps from the sets from the last workout for convenience6a
     */
    fun updateTextViews(firstRun: Boolean = false) : Unit{
        val currentSetList: ArrayList<SetEntry> = mDbHelper.getSets(workoutId = mWorkoutId, exerciseId = mExerciseId)
        val previousWorkoutId: Long = mDbHelper.getPreviousWorkoutIdWithSets(exerciseId = mExerciseId, workoutId = mWorkoutId)
        var previousSetList: ArrayList<SetEntry>? = null

        if(currentSetList.count() == 0){
            mTextViewCurrentSets.text = ""
            if(previousWorkoutId != -1L) {
                previousSetList = mDbHelper.getSets(workoutId = previousWorkoutId, exerciseId = mExerciseId)
                mTextViewPreviousSets.text = setListToString(previousSetList)
            }
        }
        else {
            mTextViewCurrentSets.text = setListToString(currentSetList)
            if(previousWorkoutId != -1L){
                previousSetList = mDbHelper.getSets(workoutId = previousWorkoutId, exerciseId = mExerciseId)
                mTextViewPreviousSets.text = setListToString(previousSetList)
            }
        }

        if (firstRun && previousSetList != null){
            if(previousSetList.count() > currentSetList.count()){
                val i = currentSetList.count()
                val previousSet = previousSetList[i]
                mReps = previousSet.reps
                mTextViewReps.text = mReps.toString()

                mWeight = previousSet.weight
                mTextViewWeight.text = mWeight.toString()

                val weightUnitIndex = mChoicesWeightUnit.indexOf(previousSet.weightUnit)
                mSpinnerWeightUnit.setSelection(weightUnitIndex)
            }
        }

    }
}
