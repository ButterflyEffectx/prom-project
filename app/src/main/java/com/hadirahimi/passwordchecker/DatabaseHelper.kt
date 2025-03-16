package com.hadirahimi.passwordchecker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "password_history.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_PASSWORDS = "passwords"
        const val COLUMN_ID = "id"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_STRENGTH = "strength"
        const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_PASSWORDS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_STRENGTH TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PASSWORDS")
        onCreate(db)
    }

    fun savePassword(password: String, strength: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, password)
            put(COLUMN_STRENGTH, strength)
            put(COLUMN_DATE, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }

        val id = db.insert(TABLE_PASSWORDS, null, values)
        db.close()
        return id
    }

    fun getAllPasswords(): ArrayList<PasswordItem> {
        val passwordList = ArrayList<PasswordItem>()
        val selectQuery = "SELECT * FROM $TABLE_PASSWORDS ORDER BY $COLUMN_DATE DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                val strength = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STRENGTH))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

                passwordList.add(PasswordItem(id, password, strength, date))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return passwordList
    }

    fun deletePassword(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_PASSWORDS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}