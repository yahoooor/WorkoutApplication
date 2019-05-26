package com.example.workoutnotebook

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.properties.Delegates

/**
 * This cursor adapter is for the Workout Activity.
 * It displays the exercises and there corresponding sets together
 */
class WorkoutCursorAdapter(context: Context, cursor: Cursor, dbHandler: DatabaseHandler, workoutId: Long)
    : CursorAdapter(context, cursor, 0) {

    var mDbHelper: DatabaseHandler by Delegates.notNull()
    var mWorkoutId: Long by Delegates.notNull()
    var mInflater: LayoutInflater by Delegates.notNull()

    init {
        mDbHelper = dbHandler
        mWorkoutId = workoutId
        mInflater = LayoutInflater.from(context)
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val view: View = mInflater.inflate(R.layout.item_exercise_with_sets, parent, false)

        val viewHolder: ExerciseCursorAdapter.ExerciseViewHolder = ExerciseCursorAdapter.ExerciseViewHolder(view)
        view.tag = viewHolder

        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder: ExerciseCursorAdapter.ExerciseViewHolder = view.tag as ExerciseCursorAdapter.ExerciseViewHolder
        val exercise: Exercise = Exercise(
                name = cursor.getString(ExerciseModel.INDEX_NAME),
                description = cursor.getString(ExerciseModel.INDEX_DESCRIPTION),
                category = cursor.getString(ExerciseModel.INDEX_CATEGORY),
                id = cursor.getLong(ExerciseModel.INDEX_ID)
        )
        viewHolder.mExercise = exercise
        viewHolder.mExerciseTextView.text = exercise.name

        val setList: ArrayList<SetEntry> = mDbHelper.getSets(workoutId = mWorkoutId, exerciseId = exercise.id)
        val setsLinearLayout: LinearLayout = view.findViewById(R.id.setsLinearLayout) as LinearLayout

        setsLinearLayout.removeAllViews()

        for(setEntry: SetEntry in setList){
            val itemSet: View = mInflater.inflate(R.layout.item_set, setsLinearLayout, false)
            val setTextView = itemSet.findViewById(R.id.text_set) as TextView
            setTextView.text = setEntry.toString()
            setsLinearLayout.addView(itemSet)
        }

    }
}