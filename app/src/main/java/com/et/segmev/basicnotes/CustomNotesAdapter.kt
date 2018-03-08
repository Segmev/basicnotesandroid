package com.et.segmev.basicnotes

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by segmev on 05/03/2018.
 */

class CustomNotesAdapter(private val notesList: ArrayList<Note>): RecyclerView.Adapter<CustomNotesAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.txtTitle?.text = notesList[position].title
        holder?.txtInformation?.text = notesList[position].info
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.note_item_layout, parent, false)
        v.setOnClickListener {
            val intent = Intent(v.context, EditNoteActivity::class.java)
            intent.putExtra("title", notesList[parent?.indexOfChild(v)!!].title)
            intent.putExtra("info", notesList[parent.indexOfChild(v)].info)
            intent.putExtra("isNewNote", false)
            intent.putExtra("id", notesList[parent.indexOfChild(v)].id)
            v.context.startActivity(intent)
        }
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun updateData(updatedNotesList: ArrayList<Note>) {
        this.notesList.clear()
        this.notesList.addAll(updatedNotesList)
        notifyDataSetChanged()
    }

    fun returnNote(position: Int): Note {
        return notesList[position]
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)!!
        val txtInformation = itemView.findViewById<TextView>(R.id.txtInformation)!!
    }

}