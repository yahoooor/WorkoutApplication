package com.example.workoutnotebook



class ExerciseModel{
    companion object{
        val TABLE_NAME: String = "exercises"

        val COL_ID: String = "_id"
        val INDEX_ID: Int = 0

        val COL_NAME: String = "exercise_name"
        val INDEX_NAME: Int = 1

        val COL_CATEGORY: String = "category"
        val INDEX_CATEGORY: Int = 2

        val COL_DESCRIPTION: String = "description"
        val INDEX_DESCRIPTION: Int = 3

        val SQL_CREATE_EXERCISES_TABLE: String = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY NOT NULL, $COL_NAME TEXT, " +
                "$COL_CATEGORY TEXT, $COL_DESCRIPTION TEXT " +
                ");"

        val SQL_DROP_EXERCISES_TABLE: String = "DROP TABLE IF EXISTS " + TABLE_NAME
    }
}

data class Exercise(
        val name: String,
        val category: String = "",
        val description: String = "",
        val id: Long = -1
){
    override fun equals(other: Any?): Boolean {
        if(other !is Exercise) return false
        if(
            this.name != other.name ||
            this.category != other.category ||
            this.description != other.description)
        {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result += 31 * result + category.hashCode()
        result += 31 * result + description.hashCode()
        return result
    }
}