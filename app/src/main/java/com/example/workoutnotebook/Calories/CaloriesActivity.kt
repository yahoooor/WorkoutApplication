package com.example.workoutnotebook

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.*

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_calories.*
import java.util.*


class CaloriesActivity : AppCompatActivity() {

    var list = mutableListOf<CaloriesModel>()
    var dbHandler = DbHelper(this, null)
    //var activeTable = "calories"
    var currDate: String = "21-05-2019"

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_DONE = 1001
    var image_uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories)

        var addMeal = findViewById(R.id.btnAddMeal) as Button
        loadData(tvDate.text.toString())

        var caloriesListView = findViewById(R.id.listViewCalories) as? ListView
        val inflater = layoutInflater
        val header = inflater.inflate(R.layout.header, caloriesListView, false) as ViewGroup
        caloriesListView!!.addHeaderView(header)

        caloriesListView!!.adapter = CaloriesListAdapter(this, R.layout.row, list)

        addMeal.setOnClickListener{
            openMealDialog()
        }

        btnCalendar.setOnClickListener {
            showDateDialog()
        }

        caloriesListView.setOnItemLongClickListener { _, _, position, _ ->
            var currName = list[position-1].name

            list.removeAt(position-1)
            dbHandler.removeMeal(currName,currDate)

            caloriesListView!!.adapter = CaloriesListAdapter(this, R.layout.row, list)

            true // False if not consumed
        }

        caloriesListView.setOnItemClickListener{_, _, position, _ ->
            if(list[position-1].imUri == null){
                Toast.makeText(this, "nie ma zdjęcia", Toast.LENGTH_SHORT).show()

            }
            else {
                openPhotoActivity(list[position - 1].imUri!!)
            }
        }



    }
    private fun openPhotoActivity(imageUri: Uri){
        val intent = Intent(this, MealPictureActivity::class.java)
        intent.putExtra("imageUri", imageUri)
        setResult(Activity.RESULT_OK,intent)
        startActivityForResult(intent, 0)
    }

    private fun openMealDialog() {
        val dialogBuilder1 = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.meal_dialog, null)
        dialogBuilder1.setView(dialogView)

        var caloriesListView = findViewById(R.id.listViewCalories) as? ListView

        var editMeal = dialogView.findViewById(R.id.editName) as? EditText
        var editCarbo = dialogView.findViewById(R.id.editCarbohydrates) as? EditText
        var editProtein = dialogView.findViewById(R.id.editProtein) as? EditText
        var editFat = dialogView.findViewById(R.id.editFat) as? EditText
        var checkBoxAddPhoto = dialogView.findViewById(R.id.cbAddPhoto) as CheckBox



        dialogBuilder1.setTitle("Add meal")
        dialogBuilder1.setPositiveButton("OK") { _, _ ->
            list.add(CaloriesModel("","","","", null))
            list[list.size-1].name = editMeal?.text.toString()
            list[list.size-1].carbohydrates = editCarbo?.text.toString()
            list[list.size-1].protein = editProtein?.text.toString()
            list[list.size-1].fat = editFat?.text.toString()

            val meal = Meal(list[list.size-1].name, list[list.size-1].carbohydrates, list[list.size-1].protein,
                    list[list.size-1].fat, image_uri)

            dbHandler.addMeal(meal, "`$currDate`")

            if(checkBoxAddPhoto.isChecked){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission, PERMISSION_CODE)
                    }
                    else{
                        openCamera()
                    }
                }
                else{
                    openCamera()
                }
            }

            caloriesListView!!.adapter = CaloriesListAdapter(this, R.layout.row, list)

        }
        dialogBuilder1.setNegativeButton("Cancel"){_,_ ->

        }
        val b = dialogBuilder1.create()
        b.show()
    }

    fun showDateDialog(){
        val c = Calendar.getInstance()
        var activeTable: String
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

           // list[list.size - 1].Data = "0$dayOfMonth".takeLast(2) + "-" + "0${monthOfYear+1}".takeLast(2) + "-" + year
            tvDate.text = "0$dayOfMonth".takeLast(2) + "-" + "0${monthOfYear+1}".takeLast(2) + "-" + year
            activeTable = tvDate.text as String
            currDate = "0$dayOfMonth".takeLast(2) + "-" + "0${monthOfYear+1}".takeLast(2) + "-" + year

            var tables: Cursor? = dbHandler.getTables()
            var tableList = mutableListOf<String>()
            while (tables?.moveToNext()!!) {
                tableList.add(tables.getString(0))
            }
            if(tableList.contains(activeTable)){
                Toast.makeText(this, "Istnieje tabela:" + activeTable, Toast.LENGTH_SHORT).show()
                //loadData("$activeTable")
                loadData(currDate)

            }
            else{
                Toast.makeText(this, "Dodano tabelę: " + activeTable, Toast.LENGTH_SHORT).show()
                dbHandler.addTable("`$activeTable`")

            }
            loadData(currDate)


        }, year, month, day)
        dpd.show()
    }

    fun loadData(currTable: String){
        list.clear()

        var caloriesListView = findViewById(R.id.listViewCalories) as? ListView
        var c: Cursor? = dbHandler.getallRecords("`$currTable`")

            while (c!!.moveToNext()) { //dodajemy do listy dane z bazy danych
                //Toast.makeText(this, c.getString(0), Toast.LENGTH_SHORT).show()
                list.add(CaloriesModel(c.getString(0), c.getString(1), c.getString(2), c.getString(3), Uri.parse(c.getString(4))))
            }

         caloriesListView!!.adapter = CaloriesListAdapter(this, R.layout.row, list)

    }

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        setResult(Activity.RESULT_OK,cameraIntent)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_DONE)
        image_uri?.let { dbHandler.updateUri(it, "`$currDate`", list[list.size -1].name ) }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        //list[list.size-1].imUri = image_uri
        image_uri?.let { dbHandler.updateUri(it, "`$currDate`", list[list.size -1].name ) }

    }

}