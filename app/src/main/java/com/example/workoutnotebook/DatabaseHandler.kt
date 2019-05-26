package com.example.workoutnotebook

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * This class handles all database operations
 */

// TODO: Decide if this class should be split a up.. Maybe a seperate class for queries?
// TODO: At the very least organize, group by database operation (insertions, deletions) or models (workout, exercises)
class DatabaseHandler(context: Context, dbName: String = DatabaseHandler.DATABASE_NAME) : SQLiteOpenHelper(context, dbName, null, VERSION) {
    private val TAG: String = "DATABASE_HANDLER"

    companion object {
        private val VERSION = 1
        private val DATABASE_NAME = "exercise_notebook.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ExerciseModel.SQL_CREATE_EXERCISES_TABLE)
        db.execSQL(SetModel.SQL_CREATE_SETS_TABLE)
        db.execSQL(WorkoutModel.SQL_CREATE_WORKOUT_TABLE)
        db.execSQL(WorkoutExerciseModel.SQL_CREATE_LOG_EXERCISE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun createTables(){
        val db: SQLiteDatabase = this.writableDatabase
        db.execSQL(ExerciseModel.SQL_CREATE_EXERCISES_TABLE)
        db.execSQL(SetModel.SQL_CREATE_SETS_TABLE)
        db.execSQL(WorkoutModel.SQL_CREATE_WORKOUT_TABLE)
        db.execSQL(WorkoutExerciseModel.SQL_CREATE_LOG_EXERCISE_TABLE)
        db.close()
    }
    fun dropTables(){
        val db: SQLiteDatabase = this.writableDatabase
        db.execSQL(WorkoutExerciseModel.SQL_DROP_LOG_EXERCISE_TABLE)
        db.execSQL(SetModel.SQL_DROP_SETS_TABLE)
        db.execSQL(ExerciseModel.SQL_DROP_EXERCISES_TABLE)
        db.execSQL(WorkoutModel.SQL_DROP_WORKOUT_TABLE)
        db.close()
    }

    fun insertSet(setEntry: SetEntry) : Long{
        val values = ContentValues()
        values.put(SetModel.COL_REPS, setEntry.reps)
        values.put(SetModel.COL_WEIGHT, setEntry.weight)
        values.put(SetModel.COL_DATE, setEntry.date)
        values.put(SetModel.COL_EXERCISE_ID, setEntry.exerciseId)
        values.put(SetModel.COL_WORKOUT_ID, setEntry.workoutId)
        values.put(SetModel.COL_WEIGHT_UNIT, setEntry.weightUnit)

        val db: SQLiteDatabase = this.writableDatabase
        var setId: Long = -1
        try{
            setId = db.insertOrThrow(SetModel.TABLE_NAME, null, values)
        }
        catch(e: SQLException){ }
        finally {
            db.close()
            return setId
        }
    }

    fun insertExercise(exercise: Exercise) : Long{
        val values = ContentValues()
        values.put(ExerciseModel.COL_NAME, exercise.name)
        values.put(ExerciseModel.COL_DESCRIPTION, exercise.description)
        values.put(ExerciseModel.COL_CATEGORY, exercise.category)

        val db: SQLiteDatabase = this.writableDatabase
        var exerciseId: Long = -1
        try{
            exerciseId = db.insertOrThrow(ExerciseModel.TABLE_NAME, null, values)
        }
        catch(e: SQLException){}
        finally {
            db.close()
            return exerciseId
        }
    }

    fun insertWorkout(workout: Workout) : Long{
        val values = ContentValues()
        values.put(WorkoutModel.COL_DATE, workout.date)

        val db: SQLiteDatabase = this.writableDatabase
        var workoutId: Long = -1L
        try{
            workoutId = db.insertOrThrow(WorkoutModel.TABLE_NAME, null, values)
            workout.exercises?.forEach {
                exercise: Exercise ->
                // TODO: Implement content of foreach loop, add each exercise to workout
            }
        }
        catch(e: SQLException){ }
        finally {
            db.close()
            return workoutId
        }
    }

    fun insertWorkoutExercise(workoutId: Long, exerciseId: Long) : Long{
        val values = ContentValues()
        values.put(WorkoutExerciseModel.COL_WORKOUT_ID, workoutId.toString())
        values.put(WorkoutExerciseModel.COL_EXERCISE_ID, exerciseId.toString())

        val db: SQLiteDatabase = this.writableDatabase
        var newRowPosition: Long = -1
        try{
            newRowPosition = db.insertOrThrow(WorkoutExerciseModel.TABLE_NAME, null, values)
        }
        catch(e: SQLException){ }
        finally {
            db.close()
            return newRowPosition
        }
    }

    fun getCursorExercisesFromWorkout(workoutId: Long? = null) : Cursor {
        val SQL_GET_ALL_EXERCISES: String = "SELECT * FROM " + ExerciseModel.TABLE_NAME
        val SQL_GET_EXERCISES_IN_WORKOUT: String = "SELECT * FROM ${ExerciseModel.TABLE_NAME} WHERE " +
                "${ExerciseModel.COL_ID} IN (" +
                "SELECT ${WorkoutExerciseModel.COL_EXERCISE_ID} FROM ${WorkoutExerciseModel.TABLE_NAME} " +
                "WHERE ${WorkoutExerciseModel.COL_WORKOUT_ID} = ?)"
        if(workoutId == null){
            return readableDatabase.rawQuery(SQL_GET_ALL_EXERCISES, null)
        }
        else{
            return readableDatabase.rawQuery(SQL_GET_EXERCISES_IN_WORKOUT, arrayOf(workoutId.toString()))
        }
    }

    fun getCursorExercisesNotInWorkout(workoutId: Long) : Cursor{
        val SQL_GET_EXERCISES_NOT_IN_WORKOUT = "SELECT * FROM ${ExerciseModel.TABLE_NAME} WHERE " +
                "${ExerciseModel.COL_ID} NOT IN (" +
                "SELECT ${WorkoutExerciseModel.COL_EXERCISE_ID} FROM ${WorkoutExerciseModel.TABLE_NAME} " +
                "WHERE ${WorkoutExerciseModel.COL_WORKOUT_ID} = ?)"
        return readableDatabase.rawQuery(SQL_GET_EXERCISES_NOT_IN_WORKOUT, arrayOf(workoutId.toString()))
    }

    fun getCursorSets(workoutId: Long? = null, exerciseId: Long? = null) : Cursor {
        val SQL_GET_ALL_SETS = "SELECT * FROM " + SetModel.TABLE_NAME
        val SQL_GET_SETS = "SELECT * FROM ${SetModel.TABLE_NAME} " +
                "WHERE ${SetModel.COL_WORKOUT_ID} = ? and " +
                "${SetModel.COL_EXERCISE_ID} = ?;"

        if (workoutId == null){
            return readableDatabase.rawQuery(SQL_GET_ALL_SETS, null)
        }
        else{
            return readableDatabase.rawQuery(SQL_GET_SETS, arrayOf(workoutId.toString(), exerciseId.toString()))
        }
    }

    fun getCursorWorkouts(workoutId: Long? = null) : Cursor {
        val SQL_GET_ALL_WORKOUTS = "SELECT * FROM " + WorkoutModel.TABLE_NAME
        val SQL_GET_WORKOUT = "SELECT * FROM ${WorkoutModel.TABLE_NAME} " +
                "WHERE ${WorkoutModel.COL_ID} = ?;"
        if(workoutId != null){
            return readableDatabase.rawQuery(SQL_GET_WORKOUT, arrayOf(workoutId.toString()))
        }
        else{
            return readableDatabase.rawQuery(SQL_GET_ALL_WORKOUTS, null)
        }
    }

    fun getExercise(exerciseId: Long) : Exercise? {
        val SQL_GET_EXERCISE = "SELECT * FROM ${ExerciseModel.TABLE_NAME} " +
                "WHERE ${ExerciseModel.COL_ID} = ?;"
        val cursor: Cursor = readableDatabase.rawQuery(SQL_GET_EXERCISE, arrayOf(exerciseId.toString()))
        if(cursor.count != 0){
            cursor.moveToFirst()
            return Exercise(
                    name = cursor.getString(ExerciseModel.INDEX_NAME),
                    category = cursor.getString(ExerciseModel.INDEX_CATEGORY),
                    description = cursor.getString(ExerciseModel.INDEX_DESCRIPTION),
                    id = cursor.getLong(ExerciseModel.INDEX_ID)
            )
        }
        else return null
    }

    fun getSets(workoutId: Long, exerciseId: Long) : ArrayList<SetEntry> {
        val setList = ArrayList<SetEntry>()
        val cursor: Cursor = getCursorSets(workoutId, exerciseId)
        while(cursor.moveToNext()){
            setList.add(
                    SetEntry(
                            reps = cursor.getInt(SetModel.INDEX_REPS),
                            weight = cursor.getFloat(SetModel.INDEX_WEIGHT),
                            exerciseId = cursor.getLong(SetModel.INDEX_EXERCISE_ID),
                            workoutId = cursor.getLong(SetModel.INDEX_WORKOUT_ID),
                            weightUnit = cursor.getString(SetModel.INDEX_WEIGHT_UNIT)
                    )
            )
        }
        cursor.close()
        return setList
    }

    /**
     * Returns the ith last workout id of an exercise that contains sets
     * e.g. i = 1 returns last row == current workoutId
     *      i = 2 return second last row == previous workoutId
     */
    fun getPreviousWorkoutIdWithSets(exerciseId: Long, workoutId: Long) : Long {
        val SQL_GET_ITH_LAST_WORKOUT = "SELECT DISTINCT ${SetModel.COL_WORKOUT_ID} " +
                "FROM ${SetModel.TABLE_NAME} " +
                "WHERE ${SetModel.COL_EXERCISE_ID} = ? AND ${SetModel.COL_WORKOUT_ID} < ?" +
                "ORDER BY ${SetModel.COL_WORKOUT_ID} DESC ;"
        val cursor: Cursor = readableDatabase.rawQuery(
                SQL_GET_ITH_LAST_WORKOUT,
                arrayOf(exerciseId.toString(), workoutId.toString())
        )
        var prevWorkoutId = -1L
        if(cursor.count != 0){
            cursor.moveToFirst()
            prevWorkoutId = cursor.getLong(0)
        }
        cursor.close()
        return prevWorkoutId
    }

    fun isValidWorkout(workoutId: Long) : Boolean {
        val cursor: Cursor = getCursorWorkouts(workoutId)
        var isValid = false;
        if(cursor.count != 0) isValid = true
        cursor.close()
        return isValid
    }

    fun getWorkout(workoutId: Long) : Workout? {
        val cursor: Cursor = getCursorWorkouts(workoutId)
        var workout: Workout? = null
        if(cursor.count == 1){
            cursor.moveToFirst()
            workout = Workout(
                    date = cursor.getString(WorkoutModel.INDEX_DATE),
                    exercises = null,
                    id = cursor.getLong(WorkoutModel.INDEX_ID)
            )
        }
        cursor.close()
        return workout
    }

    fun getLastWorkoutId() : Long{
        val SQL_LAST_WORKOUT = "SELECT * FROM ${WorkoutModel.TABLE_NAME} ORDER BY ${WorkoutModel.COL_ID} DESC LIMIT 1;"
        val cursor: Cursor = readableDatabase.rawQuery(SQL_LAST_WORKOUT, null)
        var foundId: Long = -1L
        if(cursor.count == 1){
            cursor.moveToFirst()
            foundId = cursor.getLong(WorkoutModel.INDEX_ID)
        }
        cursor.close()
        return foundId
    }
    fun getCursorWorkoutExercise() : Cursor {
        val SQL_GET_ALL_WORKOUT_EXERCISES = "SELECT * FROM " + WorkoutExerciseModel.TABLE_NAME
        return readableDatabase.rawQuery(SQL_GET_ALL_WORKOUT_EXERCISES, null)
    }

    fun getCursorWorkoutExercise(workoutId: Long) : Cursor {
        val SQL_GET_ALL_WORKOUT_EXERCISES = "SELECT * FROM ${WorkoutExerciseModel.TABLE_NAME} " +
                "WHERE ${WorkoutExerciseModel.COL_WORKOUT_ID} = ? "
        return readableDatabase.rawQuery(SQL_GET_ALL_WORKOUT_EXERCISES, arrayOf(workoutId.toString()))
    }

    fun deleteWorkout(workoutId: Long) : Int {
        var numDeletions: Int = 0
        val db: SQLiteDatabase = this.writableDatabase
        try {
            numDeletions = db.delete(
                    WorkoutModel.TABLE_NAME,
                    "${WorkoutModel.COL_ID} = ?",
                    arrayOf(workoutId.toString())
            )
        }
        catch (e: SQLiteConstraintException){ }
        finally {
            db.close()
            return numDeletions
        }
    }

    fun deleteWorkoutExercise(workoutId: Long, exerciseId: Long) : Int{
        var numDeletions: Int = 0
        val db: SQLiteDatabase = this.writableDatabase
        try {
            numDeletions = db.delete(
                    WorkoutExerciseModel.TABLE_NAME,
                    "${WorkoutExerciseModel.COL_WORKOUT_ID} = ? and ${WorkoutExerciseModel.COL_EXERCISE_ID} = ?",
                    arrayOf(workoutId.toString(), exerciseId.toString())
            )
        }
        catch (e: SQLiteConstraintException){ }
        finally {
            db.close()
            return numDeletions
        }
    }

    fun deleteSets(workoutId: Long, exerciseId: Long, mostRecentOnly: Boolean = true) : Int{
        var numDeletions: Int = 0
        val SQL_DELETE_FOR_ALL_SETS = "${SetModel.COL_WORKOUT_ID} = ? AND ${SetModel.COL_EXERCISE_ID} = ?"
        val SQL_DELETE_FOR_LAST_SET = "${SetModel.COL_ID} = " +
                "(SELECT MAX(${SetModel.COL_ID}) FROM ${SetModel.TABLE_NAME} " +
                "WHERE ${SetModel.COL_WORKOUT_ID} = ? AND ${SetModel.COL_EXERCISE_ID} = ?)"

        var SQL_DELETE_COMMAND_PICKED: String = ""
        if(mostRecentOnly) SQL_DELETE_COMMAND_PICKED = SQL_DELETE_FOR_LAST_SET
        else SQL_DELETE_COMMAND_PICKED = SQL_DELETE_FOR_ALL_SETS

        val db: SQLiteDatabase = this.writableDatabase
        try {
            numDeletions = db.delete(
                    SetModel.TABLE_NAME,
                    SQL_DELETE_COMMAND_PICKED,
                    arrayOf(workoutId.toString(), exerciseId.toString())
            )
        }
        catch (e: SQLException){ }
        finally {
            db.close()
            return numDeletions
        }
    }
}