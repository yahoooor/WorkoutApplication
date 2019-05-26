package com.example.workoutnotebook

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CaloriesListAdapter(var mCtx:Context, var resource:Int, var items:List<CaloriesModel>)
    : ArrayAdapter<CaloriesModel>(mCtx, resource, items){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        var view:View = layoutInflater.inflate(resource,null)
        var name: TextView = view.findViewById(R.id.tvName) as TextView
        var carbohydrates: TextView = view.findViewById(R.id.tvCarbohydrates) as TextView
        var protein: TextView = view.findViewById(R.id.tvProtein) as TextView
        var fat: TextView = view.findViewById(R.id.tvFat) as TextView


        var mItems:CaloriesModel = items[position]


        name.text = mItems.name
        carbohydrates.text = mItems.carbohydrates
        protein.text = mItems.protein
        fat.text = mItems.fat

        return view
    }
}