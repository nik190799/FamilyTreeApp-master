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

package com.hexanovate.familytree.ui.user.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hexanovate.familytree.R
import com.hexanovate.familytree.database.manager.MarriagesManager
import com.hexanovate.familytree.database.manager.PersonManager
import com.hexanovate.familytree.model.Anniversary
import com.hexanovate.familytree.model.Birthday
import com.hexanovate.familytree.model.Event
import com.hexanovate.familytree.ui.subadmin.NavigationDrawerActivity
import com.hexanovate.familytree.ui.subadmin.NavigationParameters
import com.hexanovate.familytree.ui.user.marriage.ViewMarriageActivityUser
import com.hexanovate.familytree.ui.user.person.ViewPersonActivityUser
import com.hexanovate.familytree.util.standardNavigationParams
import com.hexanovate.familytree.util.withNavigation


/**
 * Activity for displaying a list of birthdays and anniversaries.
 */
class EventsActivityUser : NavigationDrawerActivity() {

    companion object {

        /**
         * Request code for starting [ViewPersonActivity] for result.
         */
        private const val REQUEST_PERSON_VIEW = 1

        /**
         * Request code for starting [ViewMarriageActivity] for result.
         */
        private const val REQUEST_MARRIAGE_VIEW = 2
    }

    private val eventHandler = EventHandlerUser(this)
    private lateinit var events: ArrayList<Event>

    private lateinit var eventAdapter: EventAdapterUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setSupportActionBar(findViewById(R.id.toolbar))

        // Won't be using the FAB
        findViewById<FloatingActionButton>(R.id.fab).visibility = View.GONE

        populateList()
    }

    private fun populateList() {
        events = eventHandler.getEvents() as ArrayList<Event>
        events.sort()

        eventAdapter = EventAdapterUser(this, events)
        eventAdapter.onItemClick { _, event -> viewEvent(event) }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = eventAdapter
        }
    }

    /**
     * Refreshes the list by fetching the data (list) from the database and displaying it in the UI
     */
    private fun refreshList() {
        events.clear()
        events.addAll(eventHandler.getEvents())
        events.sort()
        eventAdapter.notifyDataSetChanged()
    }

    /**
     * Starts the appropriate activity to view an [event], for result.
     */
    private fun viewEvent(event: Event) = when (event) {
        is Anniversary -> {
            val marriage = MarriagesManager(this).get(event.personIds)
            val intent = Intent(this, ViewMarriageActivityUser::class.java)
                    .putExtra(ViewMarriageActivityUser.EXTRA_MARRIAGE, marriage)
            startActivityForResult(intent, REQUEST_MARRIAGE_VIEW)
        }
        is Birthday -> {
            val person = PersonManager(this).get(event.personId)
            val intent = Intent(this, ViewPersonActivityUser::class.java)
                    .putExtra(ViewPersonActivityUser.EXTRA_PERSON, person)
            startActivityForResult(intent, REQUEST_PERSON_VIEW)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode in arrayOf(REQUEST_PERSON_VIEW, REQUEST_MARRIAGE_VIEW)) {
            // A person could be modified by starting "edit" activities from "view" activities

            if (resultCode == Activity.RESULT_OK) {
                // Refresh the list
                refreshList()
            }
        }
    }

    override fun getSelfNavigationParams(): NavigationParameters? =
            standardNavigationParams(NAVDRAWER_ITEM_EVENTS, findViewById(R.id.toolbar))

}
