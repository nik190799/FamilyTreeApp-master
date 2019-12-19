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

package com.hexanovate.familytree.ui.user.person
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.hexanovate.familytree.MySingleton
import com.hexanovate.familytree.R
import com.hexanovate.familytree.database.manager.PersonManager
import com.hexanovate.familytree.database.schemas.PersonsSchema
import com.hexanovate.familytree.model.Marriage
import com.hexanovate.familytree.model.Person
import com.hexanovate.familytree.ui.user.DynamicPagerAdapterUser
import com.hexanovate.familytree.ui.user.marriage.EditMarriageActivityUser

import org.json.JSONObject

/**
 * This activity provides the UI for adding a new person from the database, with a guided format.
 *
 * **To edit an existing person, [EditPersonActivity] should be used, not this.**
 *
 * When the user adds all the information and confirms, the data for the new person will be written
 * to the database, and the newly created [Person] will be sent back to the activity from which this
 * was started as a result.
 *
 * @see EditPersonActivity
 */
class CreatePersonActivityUser : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "CreatePersonActivity"

        /**
         * Intent extra key for *returning* a [Person] from the calling activity.
         *
         * This activity is only for creating a new [Person], so anything with this key passed to
         * this activity will be ignored.
         */
        const val EXTRA_PERSON = "extra_person"

        /**
         * The number of pages to be displayed in this activity.
         */
        private const val NUM_PAGES = 3
    }


    private val personManager = PersonManager(this)

    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var viewPager: ViewPager
    private val pagerAdapter = DynamicPagerAdapterUser()

    private lateinit var nextButton: FloatingActionButton

    /**
     * The creator class for adding the name, image, and gender for a the person.
     *
     * @see marriageCreator
     * @see childrenCreator
     * @see person
     */
    private lateinit var personDetailsCreator: PersonDetailsCreator

    /**
     * The creator class for adding marriages to the person.
     *
     * It will be initialised (and so should only be accessed) only after creating the [Person]
     * object and writing it to the database.
     *
     * @see personDetailsCreator
     * @see childrenCreator
     * @see person
     */
    private lateinit var marriageCreator: PersonMarriageCreator

    /**
     * The creator class for adding children to the person.
     *
     * It will be initialised (and so should only be accessed) only after creating the [Person]
     * object and writing it to the database.
     *
     * @see personDetailsCreator
     * @see marriageCreator
     * @see person
     */
    private lateinit var childrenCreator: PersonChildrenCreator

    /**
     * The ID assigned to this new [Person] being created.
     *
     * As a lazily-initialised variable, its value will remain constant, so it will have the same
     * value before and after the [Person] has been written to the database.
     */
    private val personId by lazy { personManager.nextAvailableId() }

    /**
     * The newly created [Person] (already written to the database).
     *
     * It will be assigned after a [PersonDetailsCreator] writes to the database; until then it will
     * remain at its default value of null.
     */
    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_person)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener {
            if (personHasCreated()) sendSuccessfulResult(person!!) else sendCancelledResult()
        }

        setupLayout()
    }

    private fun personHasCreated() = person != null

    private fun setupLayout() {
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager).apply { adapter = pagerAdapter }
        nextButton = findViewById(R.id.fab_next)

        setupPersonCreatorPage(0)
    }

    /**
     * Sets up a page of the layout, determined by the [pageIndex].
     *
     * @param pageIndex an integer identifying the page being set up. This is not the same as the
     *                  position of the page in the [viewPager].
     */
    private fun setupPersonCreatorPage(pageIndex: Int) {
        val creator = getCreatorClass(pageIndex)

        // Change "Next" button's test to "Done" if last page
        if (pageIndex == NUM_PAGES - 1) nextButton.setImageResource(R.drawable.ic_done_white_24dp)

        // Add the new page and go to it
        val page = creator.setupPageLayout(layoutInflater, viewPager)
        val pagePos = pagerAdapter.addView(page)
        viewPager.currentItem = pagePos

        // Remove the previous page so the user cannot swipe back
        if (pagePos > 0) pagerAdapter.removeView(viewPager, pagePos - 1)

        // Change action for when the next button is pressed
        nextButton.setOnClickListener {
            if (!creator.writeData()) return@setOnClickListener
            // Only continue if data successfully written to database (i.e. not invalid data)

            if (pageIndex < NUM_PAGES - 1) {
                setupPersonCreatorPage(pageIndex + 1) // setup the next page
            } else {
                completePersonCreation()
            }
        }
    }

    /**
     * Returns the creator class responsible for displaying the page with [index].
     */
    private fun getCreatorClass(index: Int) = when (index) {
        0 -> {
            personDetailsCreator = PersonDetailsCreator(
                    this,
                    personId,
                    coordinatorLayout,
                    { newPerson -> person = newPerson },
                    { startActivityForResult(
                                PersonActivityCommonsUser.getImagePickerIntent(this),
                                PersonActivityCommonsUser.REQUEST_PICK_IMAGE
                    ) },
                    { newFullName -> updateTitle(newFullName) }
            )
            personDetailsCreator
        }
        1 -> {
            marriageCreator = PersonMarriageCreator(this, person!!) { _, _ ->
                val intent = Intent(this, EditMarriageActivityUser::class.java)
                        .putExtra(EditMarriageActivityUser.EXTRA_EXISTING_PERSON, person!!)
                startActivityForResult(intent, PersonActivityCommonsUser.REQUEST_CREATE_MARRIAGE)
            }
            marriageCreator
        }
        2 -> {
            childrenCreator = PersonChildrenCreator(this, person!!) { _, _ ->
                val intent = Intent(this, CreatePersonActivityUser::class.java)
                startActivityForResult(intent, PersonActivityCommonsUser.REQUEST_CREATE_CHILD)
            }
            childrenCreator
        }
        else -> throw IllegalArgumentException("invalid index: $index")
    }

    private fun updateTitle(fullName: String) {
        supportActionBar!!.title = if (fullName.isBlank()) {
            getString(R.string.title_create_person)
        } else {
            getString(R.string.title_create_person_withName, fullName)
        }
    }

    private fun completePersonCreation() {
        // Data has already been written to the database
        // Just send back a successful result
        Log.d("person data",person!!.forename)
        sendSuccessfulResult(person!!)

        //uploading Data to online server
        uploadData()
    }

    /**
     * Sends an "ok" result back to where this activity was invoked from.
     *
     * @param result    the newly created [Person]
     * @see android.app.Activity.RESULT_OK
     */
    private fun sendSuccessfulResult(result: Person) {
        Log.d(LOG_TAG, "Sending successful result: $result")
        val returnIntent = Intent().putExtra(EXTRA_PERSON, result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    /**
     * Sends a "cancelled" result back to where this activity was invoked from.
     *
     * @see android.app.Activity.RESULT_CANCELED
     */
    private fun sendCancelledResult() {
        Log.d(LOG_TAG, "Sending cancelled result")
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PersonActivityCommonsUser.REQUEST_CREATE_CHILD -> if (resultCode == Activity.RESULT_OK) {
                // User has successfully created a new child from the dialog
                val child = data!!.getParcelableExtra<Person>(CreatePersonActivityUser.EXTRA_PERSON)
                childrenCreator.addChild(child)
            }

            PersonActivityCommonsUser.REQUEST_CREATE_MARRIAGE -> if (resultCode == Activity.RESULT_OK) {
                // User has successfully created a new marriage from the dialog
                val marriage =
                        data!!.getParcelableExtra<Marriage>(EditMarriageActivityUser.EXTRA_MARRIAGE)
                marriageCreator.addMarriage(marriage)
            }

            PersonActivityCommonsUser.REQUEST_PICK_IMAGE -> {
                val bitmap = PersonActivityCommonsUser.getImageFromResult(
                        data, resultCode, coordinatorLayout, contentResolver)
                personDetailsCreator.setPersonImage(bitmap)
            }
        }
    }

    override fun onBackPressed() = sendCancelledResult()

    fun uploadData(){
        val stringrequest = object : StringRequest(Method.POST, "https://hexanovate.000webhostapp.com/manager/PersonManager.php",
                Response.Listener { response ->
                    try{
                        val jsonObject = JSONObject(response)
                        if (!jsonObject.getBoolean("error")) {
                            Toast.makeText(this, "Person added", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Invalid Request", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[PersonsSchema.COL_FORENAME] = person!!.forename
                params[PersonsSchema.COL_SURNAME] = person!!.surname
                params[PersonsSchema.COL_GENDER_ID] = ""+person!!.gender.id
                params[PersonsSchema.COL_BIRTH_DATE_DAY] = ""+person!!.dateOfBirth.dayOfMonth
                params[PersonsSchema.COL_BIRTH_DATE_MONTH] = ""+person!!.dateOfBirth.monthValue
                params[PersonsSchema.COL_BIRTH_DATE_YEAR] = ""+person!!.dateOfBirth.year
                params[PersonsSchema.COL_PLACE_OF_BIRTH] = person!!.placeOfBirth
                params[PersonsSchema.COL_DEATH_DATE_DAY] = ""+person!!.dateOfDeath
                params[PersonsSchema.COL_DEATH_DATE_MONTH] =""+person!!.dateOfDeath
                params[PersonsSchema.COL_DEATH_DATE_YEAR] = ""+person!!.dateOfDeath
                params[PersonsSchema.COL_PLACE_OF_DEATH] = person!!.placeOfDeath
                return params
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringrequest)
    }

}
