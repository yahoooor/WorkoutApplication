package com.example.workoutnotebook


import java.text.SimpleDateFormat
import java.util.*

val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd H:mm:ss")

fun getCurrentCalenderString() : String{
    val calender: Calendar = Calendar.getInstance()
    return formatter.format(calender.time)
}