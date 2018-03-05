package com.et.segmev.basicnotes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val users = ArrayList<Note>()
        users.add(Note("Paul", "Mr"))
        users.add(Note("Jane", "Miss"))
        users.add(Note("John", "Dr"))
        users.add(Note("Amy", "Mrs"))

        var adapter = CustomNotesAdaptater(users)
        rv.adapter = adapter
    }
}
