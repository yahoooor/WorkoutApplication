package com.example.workoutnotebook

import java.text.SimpleDateFormat
import java.util.*

class SetModel{
    companion object{
        val TABLE_NAME: String = "sets"

        val COL_ID: String = "_id"
        val INDEX_ID: Int = 0

        val COL_REPS: String = "reps"
        val INDEX_REPS: Int = 1

        val COL_WEIGHT: String = "weight"
        val INDEX_WEIGHT: Int = 2

        val COL_DATE: String = "date"
        val INDEX_DATE: Int = 3

        val COL_EXERCISE_ID: String = "exercise_id"
        val INDEX_EXERCISE_ID: Int = 4

        val COL_WORKOUT_ID: String = "workout_id"
        val INDEX_WORKOUT_ID: Int = 5

        val COL_WEIGHT_UNIT: String = "weight_units"
        val INDEX_WEIGHT_UNIT: Int = 6

        val SQL_CREATE_SETS_TABLE: String = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_REPS INT, $COL_WEIGHT REAL, " +
                "$COL_DATE TEXT, $COL_EXERCISE_ID INTEGER, $COL_WORKOUT_ID INTEGER, " +
                "$COL_WEIGHT_UNIT TEXT, " +
                "FOREIGN KEY($COL_EXERCISE_ID) REFERENCES ${ExerciseModel.TABLE_NAME}(${ExerciseModel.COL_ID}), " +
                "FOREIGN KEY($COL_WORKOUT_ID) REFERENCES ${WorkoutModel.TABLE_NAME}(${WorkoutModel.COL_ID})" +
                ");"
        val SQL_DROP_SETS_TABLE: String = "DROP TABLE IF EXISTS " + TABLE_NAME
    }
}

data class SetEntry(
        val reps: Int,
        val weight: Float,
        val weightUnit: String,
        val date: String = getCurrentCalenderString(),
        val exerciseId: Long,
        val workoutId: Long
){
    override fun toString(): String = "$reps x $weight $weightUnit"
}

fun setListToString(setList: ArrayList<SetEntry>) : String {
    var setListString: String = ""
    for(setEntry: SetEntry in setList){
        setListString += setEntry.toString() + " "
    }
    return setListString
}