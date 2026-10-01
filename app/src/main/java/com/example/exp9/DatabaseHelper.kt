package com.example.exp9

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * Inserts or updates a user in the SQLite database.
     */
    fun insertUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }

        // If user exists, update password, else insert
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )

        val result: Long
        if (cursor.moveToFirst()) {
            result = db.update(TABLE_USERS, values, "$COLUMN_USERNAME = ?", arrayOf(username)).toLong()
        } else {
            result = db.insert(TABLE_USERS, null, values)
        }
        cursor.close()
        db.close()
        return result != -1L
    }

    /**
     * Checks if username and password exist in SQLite DB.
     */
    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    /**
     * Retrieves all registered user records from SQLite DB.
     */
    fun getAllUsers(): List<String> {
        val userList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                userList.add("User #$id: Username = $username | Password = $password")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    /**
     * Retrieves all distinct usernames stored in the SQLite database for AutoComplete suggestions.
     */
    fun getAllUsernames(): List<String> {
        val usernames = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_USERNAME FROM $TABLE_USERS ORDER BY $COLUMN_USERNAME ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                if (!username.isNullOrEmpty()) {
                    usernames.add(username)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return usernames
    }

    /**
     * Retrieves password for a given username from SQLite database.
     */
    fun getPasswordForUser(username: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_PASSWORD),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )

        var password: String? = null
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
        }
        cursor.close()
        db.close()
        return password
    }
}
