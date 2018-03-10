package com.et.segmev.basicnotes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class MyDBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 5
        private val DATABASE_NAME = "notesDB.db"
        val TABLE_NOTES = "notes"
        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title"
        val COLUMN_DESCRIPTION = "description"
        val COLUMN_CREATEDAT = "created_at"
        val COLUMN_UPDATEDAT = "updated_at"
        val TAG = "DBHelper"
    }

    override fun onCreate(p0: SQLiteDatabase) {
        val CREATE_NOTES_TABLE = (
                "CREATE TABLE " + TABLE_NOTES + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY, "
                        + COLUMN_TITLE + " TEXT UNIQUE NOT NULL, "
                        + COLUMN_DESCRIPTION + " TEXT, "
                        + COLUMN_CREATEDAT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        + COLUMN_UPDATEDAT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")"
                )
        p0.execSQL(CREATE_NOTES_TABLE)
        val values = ContentValues()
        values.put(COLUMN_TITLE, "A simple note card")
        values.put(COLUMN_DESCRIPTION, "Any note added will appear here. You can click on a note to edit it, and swipe a note to remove it.")
        p0.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(p0)
    }

    fun addNote(note: Note) {
        Log.i(TAG, "add note " + note.title)
        val values = ContentValues()
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_DESCRIPTION, note.info)
        val p0 = this.writableDatabase
        p0.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        p0.close()
    }

    fun findAllNotes(orderByAsc: Boolean): ArrayList<Note> {
        Log.i(TAG, "find all notes")
        val p0 = this.writableDatabase
        var query = "SELECT * from $TABLE_NOTES  ORDER BY datetime($COLUMN_UPDATEDAT)"
        query += if (orderByAsc) " ASC" else " DESC"
        val cursor = p0.rawQuery(query, null)
        val notes = ArrayList<Note>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                notes.add(Note(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(0))))
                Log.e("date", cursor.getString(1) + " -> "+ cursor.getString(3) + " " + cursor.getString(4))
                cursor.moveToNext()
            }
        }
        cursor.close()
        p0.close()
        return notes
    }

    fun findFilteredNotes(word: String, everywhere: Boolean, orderByAsc: Boolean): ArrayList<Note> {
        Log.i(TAG, "find only notes with $word")

        var query = "SELECT * from $TABLE_NOTES WHERE $COLUMN_TITLE LIKE \"%$word%\" "
        if (everywhere)
            query += " OR $COLUMN_DESCRIPTION LIKE \"%$word%\" "
        query += " ORDER BY datetime($COLUMN_UPDATEDAT) "
        query += if (orderByAsc) " ASC" else " DESC"


        val p0 = this.writableDatabase
        val cursor = p0.rawQuery(query, null)

        val notes = ArrayList<Note>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                notes.add(Note(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(0))))
                cursor.moveToNext()
            }
        }
        cursor.close()
        p0.close()
        return notes
    }

    fun getAllTitles(): Array<String?> {
        Log.i(TAG, "find all titles")
        val p0 = this.writableDatabase
        val cursor = p0.rawQuery("SELECT * from $TABLE_NOTES", null)
        val titles = ArrayList<String>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                titles.add(cursor.getString(1))
                cursor.moveToNext()
            }
        }
        cursor.close()
        p0.close()
        val titlesArray = arrayOfNulls<String>(titles.size)
        titles.toArray(titlesArray)
        return titlesArray
    }

    fun findNote(word: String): Note? {
        Log.i(TAG, "find $word")

        val p0 = this.writableDatabase
        val cursor = p0.rawQuery("SELECT * from $TABLE_NOTES WHERE $COLUMN_TITLE = \"$word\" ", null)
        var note: Note? = null

        if (cursor.moveToFirst()) {
            note = Note(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(0)))
        }
        cursor.close()
        p0.close()
        return note
    }

    fun updateNote(note: Note) {
        Log.i(TAG, "update " + note.id)
        val values = ContentValues()
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_DESCRIPTION, note.info)
        values.put(COLUMN_UPDATEDAT, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
        val p0 = this.writableDatabase
        p0.update(TABLE_NOTES, values, "$COLUMN_ID = " + note.id, null)
        p0.close()
    }

    fun deleteNote(noteID: Int) {
        Log.i(TAG, "delete $noteID")
        val query = "SELECT * from $TABLE_NOTES WHERE $COLUMN_ID = \"$noteID\" "
        val p0 = this.writableDatabase
        val cursor = p0.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val id = cursor.getString(0)
            p0.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(id))
        }

        cursor.close()
        p0.close()
    }
}