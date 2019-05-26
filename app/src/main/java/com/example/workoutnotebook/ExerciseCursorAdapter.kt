package com.example.workoutnotebook

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates


class ExerciseCursorAdapter(context: Context, cursor: Cursor) : CursorAdapter(context, cursor, 0) {
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val mInflator = LayoutInflater.from(context)

        val view: View = mInflator.inflate(R.layout.item_exercise, parent, false)

        val viewHolder: ExerciseViewHolder = ExerciseViewHolder(view)
        view.tag = viewHolder

        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder: ExerciseViewHolder = view.tag as ExerciseViewHolder
        val exercise: Exercise = Exercise(
                name = cursor.getString(ExerciseModel.INDEX_NAME),
                description = cursor.getString(ExerciseModel.INDEX_DESCRIPTION),
                category = cursor.getString(ExerciseModel.INDEX_CATEGORY),
                id = cursor.getLong(ExerciseModel.INDEX_ID)
        )
        viewHolder.mExercise = exercise
        viewHolder.mExerciseTextView.text = exercise.name

    }

    open class ExerciseViewHolder(view: View){
        var mExercise: Exercise by Delegates.notNull()
        var mExerciseTextView: TextView by Delegates.notNull()

        init {
            mExerciseTextView = view.findViewById(R.id.exerciseTextView) as TextView
        }
    }
}