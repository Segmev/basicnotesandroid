package com.et.segmev.basicnotes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by segmev on 05/03/2018.
 */
class CustomNotesAdaptater(val notesList: ArrayList<Note>): RecyclerView.Adapter<CustomNotesAdaptater.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.txtName?.text = notesList[position].name
        holder?.txtTitle?.text = notesList[position].title

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.note_item_layout, parent, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName = itemView.findViewById<TextView>(R.id.txtName)
        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)

    }

}