package com.example.workoutnotebook

import android.net.Uri

class Meal {
    var mealID: Int = 0
    var mealName: String? = null
    var carbo: String? = null
    var protein: String? = null
    var fat: String? = null
    var imUri: Uri? = null


    constructor(mealName: String, carbo: String, protein: String, fat: String, imUri: Uri?) {
        this.mealName = mealName
        this.carbo = carbo
        this.protein = protein
        this.fat = fat
        this.imUri = imUri
    }


}