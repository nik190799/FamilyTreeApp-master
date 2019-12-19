/*
 * Copyright 2018 Farbod Salamat-Zadeh
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

package com.hexanovate.familytree.ui.subadmin.event

import android.content.Context
import com.hexanovate.familytree.database.manager.MarriagesManager
import com.hexanovate.familytree.database.manager.PersonManager
import com.hexanovate.familytree.model.Anniversary
import com.hexanovate.familytree.model.Birthday
import com.hexanovate.familytree.model.Event

/**
 * Convenience class for finding events from the database.
 */
class EventHandler(private val context: Context) {

    fun getEvents(): List<Event> {
        val events = ArrayList<Event>()
        events.addAll(getAnniversaries())
        events.addAll(getBirthdays())
        return events
    }

    private fun getAnniversaries(): List<Anniversary> {
        val anniversaries = ArrayList<Anniversary>()
        val marriage = MarriagesManager(context).getAll()
        for (m in marriage) {
            anniversaries.add(m.getRelatedEvent())
        }
        return anniversaries
    }

    private fun getBirthdays(): List<Birthday> {
        val birthdays = ArrayList<Birthday>()
        val people = PersonManager(context).getAll()
        for (p in people) {
            birthdays.add(p.getRelatedEvent())
        }
        return birthdays
    }

}
