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



package com.hexanovate.familytree.login


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.hexanovate.familytree.MySingleton
import com.hexanovate.familytree.R
import com.hexanovate.familytree.login.helpers.InputValidation
import com.hexanovate.familytree.login.model.User



import org.json.JSONObject
import java.util.*


/**
 * Created by lalit on 8/27/2016.
 */
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@RegisterActivity

    private var nestedScrollView: NestedScrollView? = null



    private var textInputLayoutForename: TextInputLayout? = null
    private var textInputLayoutSurname: TextInputLayout? = null
   // private var textInputLayoutDateOfBirth: TextInputLayout? =null
    private var textInputLayoutPlaceOfBirth: TextInputLayout? =null




    private var textInputLayoutEmail: TextInputLayout? = null
    private var textInputLayoutPassword: TextInputLayout? = null
    private var textInputLayoutConfirmPassword: TextInputLayout? = null



    private var textInputEditTextForename: TextInputEditText? = null
    private var textInputEditTextSurname: TextInputEditText? = null
    //private var textInputEditTextDateOfBirth: TextInputEditText? = null
    private var textInputEditTextPlaceOfBirth: TextInputEditText? = null

    private var textInputEditTextEmail: TextInputEditText? = null
    private var textInputEditTextPassword: TextInputEditText? = null
    private var textInputEditTextConfirmPassword: TextInputEditText? = null

    private var appCompatButtonDateOfBirth: AppCompatButton? = null
    private var appCompatButtonRegister: AppCompatButton? = null
    private var appCompatTextViewLoginLink: AppCompatTextView? = null

    private var inputValidation: InputValidation? = null


    private var user: User? = null
    private var gender_id=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        initListeners()
        initObjects()

        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        appCompatButtonDateOfBirth?.setOnClickListener {
            val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay ->

                user!!.dateOfBirth_year = mYear
                user!!.dateOfBirth_month = mMonth
                user!!.dateOfBirth_dayOfMonth = mDay

            }, year, month, day)

            dpd.show()

            Log.d("Date_Of_Birth", user?.dateOfBirth_dayOfMonth.toString()+"/"+
                                             user?.dateOfBirth_month.toString()+"/"+
                                             user?.dateOfBirth_year.toString())

        }

    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_male ->
                    if (checked) {
                        gender_id =0
                    }
                R.id.radio_female ->
                    if (checked) {
                        gender_id=1
                    }
            }
        }
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {
        nestedScrollView = findViewById<View>(R.id.nestedScrollView) as NestedScrollView

        textInputLayoutForename = findViewById<View>(R.id.textInputLayoutForename) as TextInputLayout
        textInputLayoutSurname = findViewById<View>(R.id.textInputLayoutSurname) as TextInputLayout
        textInputLayoutPlaceOfBirth = findViewById<View>(R.id.textInputLayout_placeOfBirth) as TextInputLayout
        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout
        textInputLayoutConfirmPassword = findViewById<View>(R.id.textInputLayoutConfirmPassword) as TextInputLayout




        textInputEditTextForename = findViewById<View>(R.id.textInputEditTextForename) as TextInputEditText
        textInputEditTextSurname = findViewById<View>(R.id.textInputEditTextSurname) as TextInputEditText
        textInputEditTextPlaceOfBirth = findViewById<View>(R.id.editText_placeOfBirth) as TextInputEditText
        textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText
        textInputEditTextConfirmPassword = findViewById<View>(R.id.textInputEditTextConfirmPassword) as TextInputEditText

        appCompatButtonDateOfBirth = findViewById<View>(R.id.appCompatButtonDateOfBirth) as AppCompatButton
        appCompatButtonRegister = findViewById<View>(R.id.appCompatButtonRegister) as AppCompatButton

        appCompatTextViewLoginLink = findViewById<View>(R.id.appCompatTextViewLoginLink) as AppCompatTextView

    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        appCompatButtonRegister!!.setOnClickListener(this)
        appCompatTextViewLoginLink!!.setOnClickListener(this)

    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        inputValidation = InputValidation(activity)
        //databaseHelper = new DatabaseHelper(activity);
        user = User()

    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {

            R.id.appCompatButtonRegister -> postDataToSQLite()

            R.id.appCompatTextViewLoginLink -> finish()
        }
    }



    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private fun postDataToSQLite() {




        if (!inputValidation!!.isInputEditTextFilled(textInputEditTextForename!!, textInputLayoutForename, getString(R.string.error_message_name))) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(textInputEditTextEmail!!, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation!!.isInputEditTextEmail(textInputEditTextEmail!!, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(textInputEditTextPassword!!, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return
        }
        if (!inputValidation!!.isInputEditTextMatches(textInputEditTextPassword!!, textInputEditTextConfirmPassword!!,
                        textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return
        }

        user!!.forename = textInputEditTextForename!!.text.toString().trim { it <= ' ' }
        user!!.surname = textInputEditTextSurname!!.text.toString().trim{ it <= ' '}
        user!!.gender_id = gender_id
        user!!.placeOfBirth = textInputEditTextPlaceOfBirth!!.text.toString().trim{ it <= ' '}
        user!!.email = textInputEditTextEmail!!.text.toString().trim { it <= ' ' }
        user!!.password = textInputEditTextPassword!!.text.toString().trim { it <= ' ' }
        user!!.isAdmin = 0



        val stringRequest = object : StringRequest(Method.POST, "https://hexanovate.000webhostapp.com/adduser.php",
                Response.Listener { response ->
                    try {
                        val obj = JSONObject(response)
                        if (obj.getBoolean("error")) {
                            Toast.makeText(applicationContext, "User already exist", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "User created succesfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                params["forename"] = user!!.forename
                params["surname"] = user!!.surname
                params["gender_id"] = "" + user!!.gender_id
                params["dateOfBirth_dayOfMonth"] = "" + user!!.dateOfBirth_dayOfMonth
                params["dateOfBirth_month"] = "" + user!!.dateOfBirth_month
                params["dateOfBirth_year"] = "" + user!!.dateOfBirth_year
                params["placeOfBirth"] = "" + user!!.placeOfBirth
                params["email"] = user!!.email
                params["password"] = user!!.password
                params["isAdmin"] = "" + user!!.isAdmin


                return params
            }
        }

        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextForename!!.text = null
        textInputEditTextSurname!!.text = null
        textInputEditTextPlaceOfBirth!!.text = null
        textInputEditTextEmail!!.text = null
        textInputEditTextPassword!!.text = null
        textInputEditTextConfirmPassword!!.text = null

    }
}


