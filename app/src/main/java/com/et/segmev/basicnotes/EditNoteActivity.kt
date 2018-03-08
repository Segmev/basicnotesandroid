package com.et.segmev.basicnotes

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.edit_note_layout.*

/**
 * Created by segmev on 07/03/2018.
 */
class EditNoteActivity : AppCompatActivity() {
    val dbHandler : MyDBHandler = MyDBHandler(this, null, null, 1)
    var isNewNote = true
    val note: Note = Note("", "")

    companion object {
        const val EXTRA_NOTE_TITLE = "note_title"
        const val EXTRA_NOTE_DESCRIPTION = "note_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_note_layout)

        if (savedInstanceState == null) {
            isNewNote = intent.extras.getBoolean("isNewNote", true)
            if (!isNewNote) {
                note.id = intent.extras.getInt("id")
                note.title = intent.extras.getString("title")
                note.info = intent.extras.getString("info")
            }
        } else {
            isNewNote = savedInstanceState.getBoolean("isNewNote", true)
            if (!isNewNote) {
                note.id = savedInstanceState.getInt("id")
                note.title = savedInstanceState.getString("title")
                note.info = savedInstanceState.getString("info")
            }
        }
        if (!isNewNote) {
            noteTitle.setText(note.title)
            noteInfo.setText(note.info)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (!noteTitle.text.isEmpty()) {
            setResult()
            finish()
        } else {
            Toast.makeText(this@EditNoteActivity, "You must write a title.", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.add(Menu.NONE, 1000, Menu.NONE, R.string.done)
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setResult() {
        note.title = noteTitle.text.toString()
        note.info = noteInfo.text.toString()
        if (note.title.isEmpty() && note.info.isEmpty()) {
            if (!isNewNote)
                dbHandler.deleteNote(note.id)
        }
        else {
            if (isNewNote) {
                dbHandler.addNote(note)
            }
            else {
                dbHandler.updateNote(note)
            }
        }
    }
}