/*
 * Copyright 2019 Hexanovate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hexanovate.familytree.database

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hexanovate.familytree.database.schemas.ChildrenSchema
import com.hexanovate.familytree.database.schemas.MarriagesSchema
import com.hexanovate.familytree.database.schemas.PersonsSchema
import java.util.ArrayList

/**
 * Helper singleton class for managing the SQLite database.
 */
class DatabaseHelper(context: Context): SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
) {



    companion object {

        private const val LOG_TAG = "DatabaseHelper"

        private const val DATABASE_NAME = "FamilyTreeDatabase.db"
        private const val DATABASE_VERSION = 1

        private var instance: DatabaseHelper? = null

        @JvmStatic fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i(LOG_TAG, "onCreate() invoked")

        with(db!!) {
            execSQL(PersonsSchema.SQL_CREATE)
            execSQL(ChildrenSchema.SQL_CREATE)
            execSQL(MarriagesSchema.SQL_CREATE)
            //execSQL(CREATE_USER_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i(LOG_TAG, "onUpgrade() invoked with oldVersion $oldVersion and newVersion $newVersion")

        throw IllegalArgumentException() // not implemented
    }

    fun getData(Query: String): ArrayList<Cursor?> {
        //get writable database
        val sqlDB = this.writableDatabase
        val columns = arrayOf("message")
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        val alc = ArrayList<Cursor?>(2)
        val Cursor2 = MatrixCursor(columns)
        alc.add(null)
        alc.add(null)

        try {
//execute the query results will be save in Cursor c
            val c = sqlDB.rawQuery(Query, null)

            //add value to cursor2
            Cursor2.addRow(arrayOf<Any>("Success"))

            alc[1] = Cursor2
            if (null != c && c.count > 0) {

                alc[0] = c
                c.moveToFirst()

                return alc
            }
            return alc
        } catch (ex: Exception) {
            Log.d("printing exception", ex.message)

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(arrayOf<Any>("" + ex.message))
            alc[1] = Cursor2
            return alc
        }

    }

}
