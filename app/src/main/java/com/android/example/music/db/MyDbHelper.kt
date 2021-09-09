package com.android.example.music.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.android.example.music.models.Music

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, "music.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE musics(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, artist TEXT NOT NULL, art text not null, data text not null, album text not null, duration integer not null)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun loadMusic(music: Music) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("name", music.name)
        contentValues.put("artist", music.artist)
        contentValues.put("art", music.imagePath)
        contentValues.put("data", music.musicPath)
        contentValues.put("album", music.album)
        contentValues.put("duration", music.duration)
        db.insert("musics", null, contentValues)
        db.close()
    }

    fun getAllMusics(): List<Music> {
        val list = ArrayList<Music>()
        val db = this.readableDatabase
        val query = "SELECT * FROM musics"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val artist = cursor.getString(2)
                val art = cursor.getString(3)
                val data = cursor.getString(4)
                val album = cursor.getString(5)
                val duration = cursor.getInt(6)
                list.add(Music(id, art, data, name, album, artist, duration))
            } while (cursor.moveToNext())
        }
        return list
    }

}