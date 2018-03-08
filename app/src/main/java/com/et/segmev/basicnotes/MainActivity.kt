package com.et.segmev.basicnotes

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val ADD_NOTE_CODE = 1
    val dbHandler : MyDBHandler = MyDBHandler(this, null, null, 1)
    var adapter: CustomNotesAdapter? = null
    val deletedNotes: ArrayList<Note> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        adapter = CustomNotesAdapter(dbHandler.findAllNotes())
        rv.adapter = adapter
        addNote.setOnClickListener {
            intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("isNewNote", true)
            startActivityForResult(intent, ADD_NOTE_CODE)
        }
        val swipeController: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT)
            }

            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val note: Note =  adapter?.returnNote(viewHolder?.adapterPosition!!)!!
                Toast.makeText(this@MainActivity, "Note deleted.", Toast.LENGTH_LONG).show()
                dbHandler.deleteNote(note.id)
                adapter?.updateData(dbHandler.findAllNotes())
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(rv)
    }

    override fun onResume() {
        super.onResume()
        adapter?.updateData(dbHandler.findAllNotes())
    }

    override fun onRestart() {
        super.onRestart()
        adapter?.updateData(dbHandler.findAllNotes())
    }
}
