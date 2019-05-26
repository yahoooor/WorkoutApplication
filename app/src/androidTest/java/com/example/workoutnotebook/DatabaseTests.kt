package site.arashout.workoutnotebook

import android.database.Cursor
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    val TAG: String = "DATABASE_TESTS"
    val DATABASE_NAME = "test_db"

    var mDbHelper: DatabaseHandler by Delegates.notNull()
    var mCursor: Cursor by Delegates.notNull()
    val mAppContext = InstrumentationRegistry.getTargetContext()

    // TEST DATA
    val EXERCISE_1 = Exercise(name = "Push-Ups")
    val EXERCISE_2 = Exercise(name = "Sit-Ups")
    val EXERCISE_3 = Exercise(name = "Pull-Ups")
    val WORKOUT_EMPTY = Workout()
    val REP_LOW = 1
    val REP_HIGH = 100
    val WEIGHT_ZERO = 0.0f
    val WEIGHT_LOW = 10f
    val WEIGHT_HIGH = 500f
    val WEIGHT_UNIT_DEFAULT = mAppContext.resources.getString(R.string.weight_unit_default)

    @Before
    fun setUp() {
        mDbHelper = DatabaseHandler(mAppContext, DATABASE_NAME)
        mDbHelper.dropTables()
        mDbHelper.createTables()
    }

    @After
    fun tearDown() {
        mDbHelper.dropTables()
        mDbHelper.close()
    }

    @Test
    fun testExerciseInsertion() {
        val r1: Long = mDbHelper.insertExercise(EXERCISE_1)
        val r2: Long = mDbHelper.insertExercise(EXERCISE_2)

        Assert.assertEquals(1, r1)
        Assert.assertEquals(2, r2)
    }

    @Test
    fun testExerciseQuery(){
        val exerciseId1 = mDbHelper.insertExercise(EXERCISE_1)
        val exerciseId2 = mDbHelper.insertExercise(EXERCISE_2)

        val exercise1 = mDbHelper.getExercise(exerciseId1)
        val exercise2 = mDbHelper.getExercise(exerciseId2)
        Assert.assertEquals(EXERCISE_1, exercise1)
        Assert.assertEquals(EXERCISE_2, exercise2)
        Assert.assertEquals(null, mDbHelper.getExercise(3))
    }

    @Test
    fun testExercisesInWorkoutQuery() {
        mDbHelper.insertExercise(EXERCISE_1)
        mDbHelper.insertExercise(EXERCISE_2)

        mCursor = mDbHelper.getCursorExercisesFromWorkout()
        Assert.assertEquals(2, mCursor.count)
        mCursor.moveToFirst()
        Assert.assertEquals(EXERCISE_1.name, mCursor.getString(ExerciseModel.INDEX_NAME))

        mCursor.close()
    }

    /**
     * As of now duplicate exercises are okay!
     */
    @Test
    fun testDuplicateExercise() {
        mDbHelper.insertExercise(EXERCISE_1)
        mDbHelper.insertExercise(EXERCISE_1)
        mCursor = mDbHelper.getCursorExercisesFromWorkout()
        Assert.assertEquals(2, mCursor.count)
        mCursor.close()
    }

    @Test
    fun testSetInsertion() {
        val exerciseId: Long = mDbHelper.insertExercise(EXERCISE_1)
        val workoutId: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val r1 = mDbHelper.insertSet(
                SetEntry(
                        reps = REP_LOW,
                        weight = WEIGHT_ZERO,
                        exerciseId = exerciseId,
                        workoutId = workoutId,
                        weightUnit = WEIGHT_UNIT_DEFAULT
                )
        )
        val r2 = mDbHelper.insertSet(
                SetEntry(
                        reps = REP_HIGH,
                        weight = WEIGHT_HIGH,
                        exerciseId = exerciseId,
                        workoutId = workoutId,
                        weightUnit = WEIGHT_UNIT_DEFAULT
                )
        )
        val r3 = mDbHelper.insertSet(SetEntry(reps = REP_LOW, weight = WEIGHT_LOW, exerciseId = exerciseId, workoutId = workoutId, weightUnit = WEIGHT_UNIT_DEFAULT))
        Assert.assertEquals(1, r1)
        Assert.assertEquals(2, r2)
        Assert.assertEquals(3, r3)
    }

    /**
     * Testing a set that doesn't reference a proper exercise_id
     */
    @Test
    fun testSetWithoutReference() {
        val exerciseId: Long = mDbHelper.insertExercise(EXERCISE_1)
        val workoutId: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val r: Long = mDbHelper.insertSet(SetEntry(
                reps = REP_LOW, weight = WEIGHT_ZERO,
                exerciseId = exerciseId + 10, workoutId = workoutId ,
                weightUnit = WEIGHT_UNIT_DEFAULT) //Intential bad exercise id
        )
        Assert.assertEquals(-1, r)
    }

    @Test
    fun testGetSets() {
        val exerciseId: Long = mDbHelper.insertExercise(EXERCISE_3)
        val workoutId: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        mDbHelper.insertSet(SetEntry(
                reps = REP_HIGH,
                weight = WEIGHT_ZERO,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT
        ))
        mDbHelper.insertSet(SetEntry(
                reps = REP_HIGH,
                weight = WEIGHT_LOW,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT
        ))
        mDbHelper.insertSet(SetEntry(
                reps = REP_LOW,
                weight = WEIGHT_HIGH,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT))
        val setEntries = mDbHelper.getSets(workoutId = workoutId, exerciseId = exerciseId)
        Assert.assertEquals(3, setEntries.count())
        // TODO: Use an array to store test data instead of manually indexing...
        Assert.assertEquals(REP_HIGH, setEntries[0].reps)
        Assert.assertEquals(REP_HIGH, setEntries[1].reps)
        Assert.assertEquals(REP_LOW, setEntries[2].reps)
        Assert.assertEquals(WEIGHT_UNIT_DEFAULT, setEntries[0].weightUnit)
        Assert.assertEquals(WEIGHT_ZERO, setEntries[0].weight)
        Assert.assertEquals(WEIGHT_LOW, setEntries[1].weight)
        Assert.assertEquals(WEIGHT_HIGH, setEntries[2].weight)
    }

    @Test
    fun testSetDeletion(){
        val exerciseId: Long = mDbHelper.insertExercise(EXERCISE_3)
        val workoutId: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        mDbHelper.insertSet(SetEntry(
                reps = REP_HIGH,
                weight = WEIGHT_ZERO,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT
        ))
        mDbHelper.insertSet(SetEntry(
                reps = REP_HIGH,
                weight = WEIGHT_LOW,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT
        ))
        mDbHelper.insertSet(SetEntry(
                reps = REP_LOW,
                weight = WEIGHT_HIGH,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT))
        var numDeletions = mDbHelper.deleteSets(workoutId = workoutId, exerciseId = exerciseId)
        var setEntries = mDbHelper.getSets(workoutId = workoutId, exerciseId = exerciseId)
        Assert.assertEquals(2, setEntries.count())
        Assert.assertEquals(1, numDeletions)
        // Ensure only most recent set deleted
        Assert.assertEquals(REP_HIGH, setEntries[0].reps)
        Assert.assertEquals(REP_HIGH, setEntries[1].reps)

        numDeletions = mDbHelper.deleteSets(workoutId = workoutId, exerciseId = exerciseId, mostRecentOnly = false)
        setEntries = mDbHelper.getSets(workoutId = workoutId, exerciseId = exerciseId)
        Assert.assertEquals(0, setEntries.count())
        Assert.assertEquals(2, numDeletions)
    }

    /**
     * Testing simple insertion of workouts
     */
    @Test
    fun testWorkoutInsertion() {
        mDbHelper.insertWorkout(WORKOUT_EMPTY)
        mDbHelper.insertWorkout(WORKOUT_EMPTY)
        mCursor = mDbHelper.getCursorWorkouts()
        Assert.assertEquals(2, mCursor.count)
        mCursor.close()
    }

    @Test
    fun testWorkoutDeletion() {
        val workoutId1: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val workoutId2: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)

        var numDeletions: Int = mDbHelper.deleteWorkout(workoutId1)
        Assert.assertEquals(1, numDeletions)
        numDeletions = mDbHelper.deleteWorkout(workoutId2)
        Assert.assertEquals(1, numDeletions)

        mCursor = mDbHelper.getCursorWorkouts()
        Assert.assertEquals(0, mCursor.count)

        mCursor.close()
    }

    /**
     * Should not be able to delete workout with sets attached to them
     */
    @Test
    fun testWorkoutDeletionWithSets() {
        val workoutId: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val exerciseId: Long = mDbHelper.insertExercise(EXERCISE_1)
        mDbHelper.insertSet(SetEntry(
                reps = REP_HIGH,
                weight = WEIGHT_HIGH,
                exerciseId = exerciseId,
                workoutId = workoutId,
                weightUnit = WEIGHT_UNIT_DEFAULT))

        val numDeletions = mDbHelper.deleteWorkout(workoutId)
        mCursor = mDbHelper.getCursorWorkouts()

        Assert.assertEquals(0, numDeletions)
        Assert.assertEquals(1, mCursor.count)
        mCursor.close()
    }

    /**
     * Should not be able to delete with exercises attached
     */
    @Test
    fun testWorkoutDeletionWithExercises() {
        val workoutId: Long = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val exerciseId: Long = mDbHelper.insertExercise(EXERCISE_1)

        mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)

        val numDeletions = mDbHelper.deleteWorkout(workoutId)
        mCursor = mDbHelper.getCursorWorkouts()

        Assert.assertEquals(0, numDeletions)
        Assert.assertEquals(1, mCursor.count)
        mCursor.close()
    }

    @Test
    fun testInsertWorkoutExercise() {
        val workoutId = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val exerciseId = mDbHelper.insertExercise(EXERCISE_1)
        val r: Long = mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
        Assert.assertNotEquals(-1, r)
    }

    @Test
    fun testDeleteWorkoutExercise() {
        val workoutId = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val exerciseId = mDbHelper.insertExercise(EXERCISE_1)
        mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
        val status = mDbHelper.deleteWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
        Assert.assertEquals(1, status)
    }

    /**
     * Should not be able to exerciseWorkout relation without valid exercise ID
     */
    @Test
    fun testBadWorkoutExercise() {
        val workoutId = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val r: Long = mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = 1)
        Assert.assertEquals(-1, r)
    }

    /**
     * Should not be able to add a exercise to workout twice
     */
    @Test
    fun testDuplicateWorkoutExercise() {
        val workoutId = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val exerciseId = mDbHelper.insertExercise(EXERCISE_1)
        mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
        val r: Long = mDbHelper.insertWorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
        Assert.assertEquals(-1, r)
    }

    @Test
    fun testWorkoutQueries() {
        val exerciseId1 = mDbHelper.insertExercise(EXERCISE_1)
        val exerciseId2 = mDbHelper.insertExercise(EXERCISE_2)
        val exerciseId3 = mDbHelper.insertExercise(EXERCISE_3)

        val workoutId1 = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val workoutId2 = mDbHelper.insertWorkout(WORKOUT_EMPTY)

        mDbHelper.run {
            insertWorkoutExercise(workoutId = workoutId1, exerciseId = exerciseId1)
            insertWorkoutExercise(workoutId = workoutId1, exerciseId = exerciseId3)
            insertWorkoutExercise(workoutId = workoutId2, exerciseId = exerciseId2)
        }

        mCursor = mDbHelper.getCursorWorkoutExercise()
        Assert.assertEquals(3, mCursor.count)
        mCursor = mDbHelper.getCursorWorkoutExercise(workoutId1)
        Assert.assertEquals(2, mCursor.count)
        mCursor = mDbHelper.getCursorWorkoutExercise(workoutId2)
        Assert.assertEquals(1, mCursor.count)

        mCursor = mDbHelper.getCursorExercisesFromWorkout(workoutId1)
        Assert.assertEquals(2, mCursor.count)

        mCursor = mDbHelper.getCursorExercisesFromWorkout(workoutId2)
        Assert.assertEquals(1, mCursor.count)

        mCursor.close()
    }

    @Test
    fun testGetPreviousWorkoutId(){
        val exerciseId1 = mDbHelper.insertExercise(EXERCISE_1)

        val workoutId1 = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val workoutId2 = mDbHelper.insertWorkout(WORKOUT_EMPTY)
        val workoutId3 = mDbHelper.insertWorkout(WORKOUT_EMPTY)

        mDbHelper.insertWorkoutExercise(workoutId = workoutId1, exerciseId = exerciseId1)

        var i: Int = 0
        while(i < 3) {

            mDbHelper.insertSet(
                    SetEntry(
                        reps = REP_LOW,
                        weight = WEIGHT_ZERO,
                        exerciseId = exerciseId1,
                        workoutId = workoutId1,
                        weightUnit = WEIGHT_UNIT_DEFAULT
                    )
            )
            i++
        }
        i = 0
        while(i < 3) {
            mDbHelper.insertSet(
                    SetEntry(
                            reps = REP_LOW,
                            weight = WEIGHT_ZERO,
                            exerciseId = exerciseId1,
                            workoutId = workoutId2,
                            weightUnit = WEIGHT_UNIT_DEFAULT
                    )
            )
            i++
        }
        Assert.assertEquals(-1, mDbHelper.getPreviousWorkoutIdWithSets(exerciseId = exerciseId1, workoutId = workoutId1))
        Assert.assertEquals(1, mDbHelper.getPreviousWorkoutIdWithSets(exerciseId = exerciseId1, workoutId = workoutId2))
        Assert.assertEquals(2, mDbHelper.getPreviousWorkoutIdWithSets(exerciseId = exerciseId1, workoutId = workoutId3))
    }
}
