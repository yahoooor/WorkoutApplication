package com.example.workoutnotebook

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView

class CreateExerciseActivity : BaseActivity() {
    val TAG: String = "CREATE_EXERCISE_ACT"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_exercise)

        val dbHelper: DatabaseHandler = DatabaseHandler(context = applicationContext)

        val textViewExerciseName = findViewById(R.id.text_edit_exercise_name) as TextView
        val textViewExerciseCategory = findViewById(R.id.text_edit_exercise_category) as TextView
        val textViewExerciseDescription = findViewById(R.id.text_edit_exercise_description) as TextView

        val buttonSaveExercise = findViewById(R.id.btn_save_exercise) as Button
        buttonSaveExercise.setOnClickListener{
            if (TextUtils.isEmpty(textViewExerciseName.text.toString()) ) {
                textViewExerciseName.error = "Exercise needs a name"
            }
            else{
                val exercise: Exercise = Exercise(
                        name = textViewExerciseName.text.toString(),
                        description = textViewExerciseDescription.text.toString(),
                        category = textViewExerciseCategory.text.toString()
                )
                dbHelper.insertExercise(exercise)
                finish()
            }
        }
    }
}
