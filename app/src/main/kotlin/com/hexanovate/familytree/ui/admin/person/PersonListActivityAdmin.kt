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
package com.hexanovate.familytree.ui.admin.person

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hexanovate.familytree.R
import com.hexanovate.familytree.database.manager.PersonManager
import com.hexanovate.familytree.model.Person
import com.hexanovate.familytree.ui.subadmin.NavigationDrawerActivity
import com.hexanovate.familytree.util.standardNavigationParams
import com.hexanovate.familytree.util.withNavigation

/**
 * Activity for displaying a list of people.
 */
class PersonListActivityAdmin : NavigationDrawerActivity() {

    companion object {

        /**
         * Request code for starting [ViewPersonActivity] for result.
         */
        private const val REQUEST_PERSON_VIEW = 1

        /**
         * Request code for starting [CreatePersonActivity] for result.
         */
        private const val REQUEST_PERSON_EDIT = 2
    }

    private val personManager = PersonManager(this)
    private lateinit var people: ArrayList<Person>

    private lateinit var personAdapter: PersonAdapterAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setSupportActionBar(findViewById(R.id.toolbar))

        setupFab()
        populateList()
    }

    private fun setupFab() {
        val addPersonButton = findViewById<FloatingActionButton>(R.id.fab)
        addPersonButton.setOnClickListener { addPerson() }
    }

    private fun populateList() {
        people = personManager.getAll() as ArrayList<Person>
        people.sort()

        personAdapter = PersonAdapterAdmin(people)
        personAdapter.onItemClick { _, person -> viewPerson(person) }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = personAdapter
        }
    }

    /**
     * Refreshes the list by fetching the data (list) from the database and displaying it in the UI
     */
    private fun refreshList() {
        people.clear()
        people.addAll(personManager.getAll())
        people.sort()
        personAdapter.notifyDataSetChanged()
    }

    /**
     * Starts [ViewPersonActivity] to view a [Person], for result.
     * @param person    the person to be passed to [ViewPersonActivity]
     */
    private fun viewPerson(person: Person) {
        val intent = Intent(this, ViewPersonActivityAdmin::class.java)
                .putExtra(ViewPersonActivityAdmin.EXTRA_PERSON, person)
        startActivityForResult(intent, REQUEST_PERSON_VIEW)
    }

    /**
     * Starts [EditPersonActivity] to create a [Person], for result.
     */
    private fun addPerson() {
        val intent = Intent(this, CreatePersonActivityAdmin::class.java)
        startActivityForResult(intent, REQUEST_PERSON_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode in arrayOf(REQUEST_PERSON_VIEW, REQUEST_PERSON_EDIT)) {
            // A person could be modified by starting EditPersonActivity from ViewPersonActivity as
            // well as from CreatePersonActivity directly

            if (resultCode == Activity.RESULT_OK) {
                // Refresh the list
                refreshList()
            }
        }
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_PERSON_LIST, findViewById(R.id.toolbar))

}
