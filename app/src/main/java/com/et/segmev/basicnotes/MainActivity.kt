package com.et.segmev.basicnotes

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val ADD_NOTE_CODE = 1
    val dbHandler: MyDBHandler = MyDBHandler(this, null)
    var adapter: CustomNotesAdapter? = null
    var lastDeletedNote: Note? = null
    var orderByAsc = false
    var toggleButtonIsChecked = false

    private fun updateNoteList(noteList: ArrayList<Note>) {
        adapter?.updateData(noteList, lastDeletedNote)
        empty_view.visibility = if (noteList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = getString(R.string.mainactivity_title)

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        adapter = CustomNotesAdapter(dbHandler.findAllNotes(true))
        rv.adapter = adapter
        addNote.setOnClickListener {
            intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("isNewNote", true)
            startActivityForResult(intent, ADD_NOTE_CODE)
        }

        val swipeController: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?,
                                target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                if (lastDeletedNote != null)
                    dbHandler.deleteNote(lastDeletedNote?.id!!)
                lastDeletedNote = adapter?.returnNote(viewHolder?.adapterPosition!!)!!
                Toast.makeText(this@MainActivity, "Note deleted.", Toast.LENGTH_LONG).show()
                updateNoteList(dbHandler.findAllNotes(orderByAsc))
                autoCompleteTextView.setAdapter(ArrayAdapter<String>(this@MainActivity,
                        android.R.layout.simple_dropdown_item_1line, dbHandler.getAllTitles()))
                invalidateOptionsMenu()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(rv)

        autoCompleteTextView.setAdapter(ArrayAdapter<String>(this@MainActivity,
                android.R.layout.simple_dropdown_item_1line, dbHandler.getAllTitles()))
        autoCompleteTextView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateNoteList(dbHandler.findFilteredNotes(p0?.toString()!!, toggleButtonIsChecked, orderByAsc))
            }
        })
        toggleButton.setOnClickListener {
            toggleButtonIsChecked = !toggleButtonIsChecked
            if (toggleButtonIsChecked) {
                toggleButton.text = getString(R.string.everywhere)
                toggleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            else {
                toggleButton.text = getString(R.string.in_titles_only)
                toggleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            }
            updateNoteList(dbHandler.findFilteredNotes(autoCompleteTextView.text.toString(),
                    toggleButtonIsChecked, orderByAsc))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.add(Menu.NONE, 42, Menu.NONE, getString(R.string.undo_menuitem))
        menu.add(Menu.NONE, 84, Menu.NONE, getString(R.string.most_recent_first_menuitem))
        menuItem.isEnabled = false
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.getItem(0).isEnabled = lastDeletedNote != null
        menu.getItem(1).title = if (!orderByAsc)
            getString(R.string.lest_recent_first_menuitem)
        else
            getString(R.string.most_recent_first_menuitem)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            42 -> {
                if (lastDeletedNote != null)
                    dbHandler.addNote(lastDeletedNote!!)
                lastDeletedNote = null
                updateNoteList(dbHandler.findAllNotes(orderByAsc))
                Toast.makeText(this@MainActivity, getString(R.string.note_restored_toast),
                        Toast.LENGTH_LONG).show()
                invalidateOptionsMenu()
            }
            84 -> {
                orderByAsc = !orderByAsc
                invalidateOptionsMenu()
                updateNoteList(dbHandler.findAllNotes(orderByAsc))
            }
        }
        autoCompleteTextView.setAdapter(ArrayAdapter<String>(this@MainActivity,
                android.R.layout.simple_dropdown_item_1line, dbHandler.getAllTitles()))
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        autoCompleteTextView.setAdapter(ArrayAdapter<String>(this@MainActivity,
                android.R.layout.simple_dropdown_item_1line, dbHandler.getAllTitles()))
        updateNoteList(dbHandler.findAllNotes(orderByAsc))
    }

    override fun onRestart() {
        super.onRestart()
        autoCompleteTextView.setAdapter(ArrayAdapter<String>(this@MainActivity,
                android.R.layout.simple_dropdown_item_1line, dbHandler.getAllTitles()))
        updateNoteList(dbHandler.findAllNotes(orderByAsc))
    }

    override fun onStop() {
        if (lastDeletedNote != null)
            dbHandler.deleteNote(lastDeletedNote?.id!!)
        super.onStop()
    }
}
