package com.example.workoutnotebook

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class DbHelper(context: Context,
               factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, DATABASE_NAME,
                factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_MEALS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + // COLUMN_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MEAL_NAME + " TEXT," +
                COLUMN_CARBO + " TEXT," +
                COLUMN_PROTEIN + " TEXT," +
                COLUMN_FAT + " TEXT," +
                COLUMN_URI + " TEXT" + ")")
        db.execSQL(CREATE_MEALS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }
    fun addMeal(meal: Meal, tableName: String) {
        val values = ContentValues()
        //values.put(COLUMN_MEAL_ID, meal.mealID)
        values.put(COLUMN_MEAL_NAME, meal.mealName)
        values.put(COLUMN_CARBO, meal.carbo)
        values.put(COLUMN_PROTEIN, meal.protein)
        values.put(COLUMN_FAT, meal.fat)
        values.put(COLUMN_URI, meal.imUri.toString())
        val db = this.writableDatabase
        db.insertOrThrow(tableName, null, values)
        db.close()
    }
    fun updateUri(imUri: Uri, tableName: String, name: String){
        val values = ContentValues()
        //values.put(COLUMN_MEAL_ID, meal.mealID)
        values.put(COLUMN_URI, imUri.toString())
        val db = this.writableDatabase
        db.update(tableName, values, "$COLUMN_MEAL_NAME = \"$name\"",null)
    }
    fun getallRecords(tableName: String):Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $tableName", null)
    }

    fun removeMeal(name: String, tableName: String): Int {
        val db = this.writableDatabase
        return db.delete("`$tableName`","$COLUMN_MEAL_NAME = \"$name\"", null)
    }

    fun getTables(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
    }

    fun addTable(tableName: String){
        val db = this.writableDatabase

        val CREATE_MEALS_TABLE = ("CREATE TABLE " +
                tableName + "("
                + // COLUMN_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MEAL_NAME + " TEXT," +
                COLUMN_CARBO + " TEXT," +
                COLUMN_PROTEIN + " TEXT," +
                COLUMN_FAT + " TEXT," +
                COLUMN_URI + " TEXT" + ")")
        db.execSQL(CREATE_MEALS_TABLE)
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "mindorksName.db"
        val TABLE_NAME = "`21-05-2019`"
        val COLUMN_MEAL_ID = "id"
        val COLUMN_MEAL_NAME = "meal"
        val COLUMN_CARBO = "carbo"
        val COLUMN_PROTEIN = "protein"
        val COLUMN_FAT = "fat"
        val COLUMN_URI = "uri"

    }
}