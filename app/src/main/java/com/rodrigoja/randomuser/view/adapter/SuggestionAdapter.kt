package com.rodrigoja.randomuser.view.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.rodrigoja.randomuser.R

class SuggestionAdapter(context: Context, c: Cursor?, autoRequery: Boolean): CursorAdapter(context, c, autoRequery) {
    var callback: (String) -> Unit = {}

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvName = view?.findViewById(R.id.tvSuggestion) as TextView?
        if (cursor?.getColumnIndex("name") != -1
                && cursor?.getString(cursor.getColumnIndex("name")) != null){
            val name: String = cursor.getString(cursor.getColumnIndex("name"))
            val email: String = cursor.getString(cursor.getColumnIndex("email"))
            tvName?.text = name
            view?.setOnClickListener {
                callback(email)
            }
        }
    }
}