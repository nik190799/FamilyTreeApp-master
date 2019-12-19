/*
 *
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

package com.hexanovate.familytree.model

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDate

/**
 * Represents a marriage between two people.
 *
 * @property person1Id          ID of a person in this marriage
 * @property person2Id          ID of another person in this marriage
 * @property startDate          date of marriage
 * @property endDate            date when the marriage ended. If the marriage has not ended, this
 *                              should be null.
 * @property placeOfMarriage    name of the place where the marriage took place. This is optional
 *                              and can be left blank.
 */
data class Marriage(
        val person1Id: Int,
        val person2Id: Int,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val placeOfMarriage: String
) : DataRelationship, WithEvent, Parcelable {

    init {
        require(person1Id > 0) { "person1Id < 1: the id of a person must be greater than 0" }
        require(person2Id > 0) { "person2Id < 1: the id of a person must be greater than 0" }
        require(person1Id != person2Id) {
            "person1Id = person2Id: a person cannot be married to themselves"
        }
    }

    fun isOngoing() = endDate == null

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readSerializable() as LocalDate,
            source.readSerializable() as LocalDate?,
            source.readString()
    )

    /**
     * Returns the person ID of the other person involved in the marriage.
     *
     * @param personId  the person ID of one person involved in the marriage
     */
    fun getOtherSpouseId(personId: Int) = if (personId == person1Id) person2Id else person1Id

    override fun getIds() = Pair(person1Id, person2Id)

    override fun getRelatedEvent() = Anniversary(getIds(), startDate, placeOfMarriage)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(person1Id)
        writeInt(person2Id)
        writeSerializable(startDate)
        writeSerializable(endDate)
        writeString(placeOfMarriage)
    }

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Marriage> = object : Parcelable.Creator<Marriage> {
            override fun createFromParcel(source: Parcel): Marriage = Marriage(source)
            override fun newArray(size: Int): Array<Marriage?> = arrayOfNulls(size)
        }



        /**
         * Instantiates a [Marriage] object by getting values in columns from a [cursor].
         */
//        @JvmStatic
//        fun from(cursor: Cursor): Marriage {
//            val startDate = LocalDate.of(
//                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_YEAR)),
//                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_MONTH)),
//                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_DAY))
//            ) // TODO should marriages dates be optional?
//
//            val endDate = if (cursor.isNull(cursor.getColumnIndex(MarriagesSchema.COL_END_DATE_DAY))) {
//                null
//            } else {
//                LocalDate.of(
//                        cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_END_DATE_YEAR)),
//                        cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_END_DATE_MONTH)),
//                        cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_END_DATE_DAY))
//                )
//            }
//
//            return Marriage(
//                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_ID_1)),
//                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_ID_2)),
//                    startDate,
//                    endDate,
//                    cursor.getString(cursor.getColumnIndex(MarriagesSchema.COL_PLACE_OF_MARRIAGE))
//            )
//        }




        @JvmStatic
        fun from(cursor: Cursor): Marriage {
            val startDate = LocalDate.of(
                    cursor.getInt(cursor.getColumnIndex("startDate_year")),
                    cursor.getInt(cursor.getColumnIndex("startDate_month")),
                    cursor.getInt(cursor.getColumnIndex("startDate_dayOfMonth"))
            ) // TODO should marriages dates be optional?

            val endDate = if (cursor.isNull(cursor.getColumnIndex("endDate_dayOfMonth"))) {
                null
            } else {
                LocalDate.of(
                        cursor.getInt(cursor.getColumnIndex("endDate_year")),
                        cursor.getInt(cursor.getColumnIndex("endDate_month")),
                        cursor.getInt(cursor.getColumnIndex("endDate_dayOfMonth"))
                )
            }

            return Marriage(
                    cursor.getInt(cursor.getColumnIndex("person1_id")),
                    cursor.getInt(cursor.getColumnIndex("person2_id")),
                    startDate,
                    endDate,
                    cursor.getString(cursor.getColumnIndex("placeOfMarriage"))
            )
        }

    }
}
