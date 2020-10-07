package com.example.ontouchtodo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context,
    databaseName: String = "tododatabase.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, databaseName, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("create table if not exists todo_table (id text primary key, task_name text, task_type text, task_date text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}