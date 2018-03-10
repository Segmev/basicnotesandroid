package com.et.segmev.basicnotes

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.edit_note_layout.*

class EditNoteActivity : AppCompatActivity() {
    private val dbHandler: MyDBHandler = MyDBHandler(this, null)
    private var isNewNote = true
    private val note: Note = Note("", "")
    private var backPressedOnce = false

    companion object {
        const val EXTRA_NOTE_TITLE = "note_title"
        const val EXTRA_NOTE_DESCRIPTION = "note_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_note_layout)
        supportActionBar?.title = getString(R.string.editnote_title)

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
            if (setResult())
                finish()
            else
                Toast.makeText(this@EditNoteActivity, "A note already exist with this title.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@EditNoteActivity, "You must write a title.", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.add(Menu.NONE, 1000, Menu.NONE, R.string.done)
        menuItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (backPressedOnce || (noteTitle.text.isEmpty() && noteInfo.text.isEmpty())) {
            super.onBackPressed()
            finish()
            return
        }
        backPressedOnce = true
        Toast.makeText(
                this@EditNoteActivity,
                "All changes will be lost. Press again to go back.",
                Toast.LENGTH_SHORT
        ).show()
        Handler().postDelayed({ backPressedOnce = false }, 3000)
    }

    private fun setResult(): Boolean {
        note.title = noteTitle.text.toString()
        note.info = noteInfo.text.toString()
        if (note.title.isEmpty() && note.info.isEmpty()) {
            if (!isNewNote)
                dbHandler.deleteNote(note.id)
        }
        else {
            if (isNewNote) {
                if (dbHandler.findNote(note.title) != null)
                    return false
                dbHandler.addNote(note)
            }
            else {
                dbHandler.updateNote(note)
            }
        }
        return true
    }
}