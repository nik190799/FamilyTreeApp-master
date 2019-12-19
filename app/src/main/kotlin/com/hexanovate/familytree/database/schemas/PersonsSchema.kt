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

package com.hexanovate.familytree.database.schemas

import android.provider.BaseColumns
import com.hexanovate.familytree.model.Person

/**
 * Schema for the 'persons' table.
 * It contains constants for the column names of the table, and SQLite statement for creation.
 *
 * _N.B. data for marriages is stored in a separate table (see [MarriagesSchema])_
 *
 * @see Person
 */
object PersonsSchema {

    const val TABLE_NAME = "persons"

    /** Name of the integer primary key column, corresponding to [Person.id] */
    const val COL_ID = BaseColumns._ID

    const val COL_FORENAME = "forename"
    const val COL_SURNAME = "surname"
    const val COL_GENDER_ID = "gender_id"

    const val COL_BIRTH_DATE_DAY = "dateOfBirth_dayOfMonth"
    const val COL_BIRTH_DATE_MONTH = "dateOfBirth_month"
    const val COL_BIRTH_DATE_YEAR = "dateOfBirth_year"
    const val COL_PLACE_OF_BIRTH = "placeOfBirth"

    const val COL_DEATH_DATE_DAY = "dateOfDeath_dayOfMonth"
    const val COL_DEATH_DATE_MONTH = "dateOfDeath_month"
    const val COL_DEATH_DATE_YEAR = "dateOfDeath_year"
    const val COL_PLACE_OF_DEATH = "placeOfDeath"

    /**
     * SQLite statement which creates the "persons" table upon execution.
     * @see com.hexanovate.familytree.database.DatabaseHelper
     */
    const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "$COL_FORENAME TEXT NOT NULL, $COL_SURNAME TEXT NOT NULL, " +
            "$COL_GENDER_ID INTEGER NOT NULL, $COL_BIRTH_DATE_DAY INTEGER NOT NULL, " +
            "$COL_BIRTH_DATE_MONTH INTEGER NOT NULL, $COL_BIRTH_DATE_YEAR INTEGER NOT NULL, " +
            "$COL_PLACE_OF_BIRTH TEXT, $COL_DEATH_DATE_DAY INTEGER, $COL_DEATH_DATE_MONTH INTEGER, " +
            "$COL_DEATH_DATE_YEAR INTEGER, $COL_PLACE_OF_DEATH TEXT)"

}
