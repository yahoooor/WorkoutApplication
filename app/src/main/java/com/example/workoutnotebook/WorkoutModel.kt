package com.example.workoutnotebook



class WorkoutModel {
    companion object{
        val TABLE_NAME: String = "workouts"

        val COL_ID: String = "_id"
        val INDEX_ID: Int = 0

        val COL_DATE: String = "date"
        val INDEX_DATE: Int = 1

        val SQL_CREATE_WORKOUT_TABLE: String = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,$COL_DATE TEXT " +
                ");"

        val SQL_DROP_WORKOUT_TABLE: String = "DROP TABLE IF EXISTS " + TABLE_NAME
    }
}

/**
 * This table is an intermediate table that connects a list of exercises to a single workout
 */
class WorkoutExerciseModel {
    companion object{
        val TABLE_NAME: String = "workout_exercises"

        val COL_WORKOUT_ID: String = "workout_id"
        val INDEX_LOG_ID: Int = 0

        val COL_EXERCISE_ID: String = "exercise_id"
        val INDEX_EXERCISE_ID: Int = 1

        val SQL_CREATE_LOG_EXERCISE_TABLE: String = "CREATE TABLE $TABLE_NAME (" +
                "$COL_WORKOUT_ID INTEGER NOT NULL, $COL_EXERCISE_ID INTEGER NOT NULL, "  +
                "FOREIGN KEY($COL_WORKOUT_ID) REFERENCES ${WorkoutModel.TABLE_NAME}(${WorkoutModel.COL_ID}), " +
                "FOREIGN KEY($COL_EXERCISE_ID) REFERENCES ${ExerciseModel.TABLE_NAME}(${ExerciseModel.COL_ID}), " +
                "PRIMARY KEY ($COL_WORKOUT_ID, $COL_EXERCISE_ID)" +
                ");"
        val SQL_DROP_LOG_EXERCISE_TABLE: String = "DROP TABLE IF EXISTS " + TABLE_NAME
    }
}

data class Workout(
        val date: String = getCurrentCalenderString(),
        val exercises: ArrayList<Exercise>? = null,
        val id: Long = -1L
)

