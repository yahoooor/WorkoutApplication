package com.example.workoutnotebook



enum class REQUEST_CODE {
    ADD_EXERCISE,
    CREATE_EXERCISE,
    ADD_SET,
    SETTINGS,
    STEP_COUNTER,
    CALORIES
}
enum class WORKOUT_ACTION {
    LATEST_WORKOUT,
    NEW_WORKOUT,
    EXISTING_WORKOUT
}
object EXTRA_KEYS {
    const val WORKOUT_ID: String = "WORKOUT_ID"
    const val EXERCISE_ID: String = "EXERCISE_ID"
    const val WORKOUT_STARTUP_ACTION = "WORKOUT_STARTUP_ACTION"
}