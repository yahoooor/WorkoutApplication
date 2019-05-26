package com.example.workoutnotebook

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_meal_photo.*

class MealPictureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_meal_photo)

        var getImUri = intent.getParcelableExtra<Uri>("imageUri")
        if(getImUri != null){
            imageView2.setImageURI(getImUri)
        }
    }
}