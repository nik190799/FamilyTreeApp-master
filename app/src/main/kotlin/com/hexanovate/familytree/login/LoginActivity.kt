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

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.hexanovate.familytree.MySingleton
import com.hexanovate.familytree.R
import com.hexanovate.familytree.login.helpers.InputValidation
import com.hexanovate.familytree.login.model.User


import com.hexanovate.familytree.ui.admin.home.MainActivityAdmin
import com.hexanovate.familytree.ui.subadmin.home.MainActivity
import com.hexanovate.familytree.ui.user.home.MainActivityUser


import org.json.JSONObject

import java.util.HashMap


class LoginActivity : AppCompatActivity(), View.OnClickListener {


    private var nestedScrollView: NestedScrollView? = null

    private var textInputLayoutEmail: TextInputLayout? = null
    private var textInputLayoutPassword: TextInputLayout? = null

    private var textInputEditTextEmail: TextInputEditText? = null
    private var textInputEditTextPassword: TextInputEditText? = null

    private var appCompatButtonLogin: AppCompatButton? = null

    private var textViewLinkRegister: AppCompatTextView? = null

    private var inputValidation: InputValidation? = null

    private var isAdmin=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        initListeners()
        initObjects()

        val button1 = findViewById<Button>(R.id.button_db)
        button1.setOnClickListener { view ->
            val intent = Intent(view.context, AndroidDatabaseManager::class.java)
            view.context.startActivity(intent)
        }
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {

        nestedScrollView = findViewById<View>(R.id.nestedScrollView) as NestedScrollView

        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout

        textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText

        appCompatButtonLogin = findViewById<View>(R.id.appCompatButtonLogin) as AppCompatButton

        textViewLinkRegister = findViewById<View>(R.id.textViewLinkRegister) as AppCompatTextView

    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        appCompatButtonLogin!!.setOnClickListener(this)
        textViewLinkRegister!!.setOnClickListener(this)
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        //        databaseHelper = new DatabaseHelper(activity);
        inputValidation = InputValidation(this)

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonLogin -> verifyFromSQLite()

            R.id.textViewLinkRegister -> {
                // Navigate to RegisterActivity
                val intentRegister = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(intentRegister)
            }
        }
    }


    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private fun verifyFromSQLite() {
        if (!inputValidation!!.isInputEditTextFilled(textInputEditTextEmail!!, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation!!.isInputEditTextEmail(textInputEditTextEmail!!, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(textInputEditTextPassword!!, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return
        }


        val stringRequest = object : StringRequest(Request.Method.POST, "https://hexanovate.000webhostapp.com/login.php",
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        if (!jsonObject.getBoolean("error")) {
                            Toast.makeText(applicationContext, "Logged in", Toast.LENGTH_SHORT).show()
                            //Toast.makeText(applicationContext,jsonObject.toString(),Toast.LENGTH_LONG).show()
                            val user = User(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("forename"),
                                    jsonObject.getString("surname"),
                                    jsonObject.getInt("gender_id"),
                                    jsonObject.getInt("dateOfBirth_dayOfMonth"),
                                    jsonObject.getInt("dateOfBirth_month"),
                                    jsonObject.getInt("dateOfBirth_year"),
                                    jsonObject.getString("placeOfBirth"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("password"),
                                    jsonObject.getInt("isAdmin"))

                            Toast.makeText(applicationContext,user.forename+" welcome to the FamilyTree", Toast.LENGTH_SHORT).show()
                            isAdmin = user.isAdmin

                            when (isAdmin) {
                                0 -> {
                                    val intent = Intent(this@LoginActivity, MainActivityUser::class.java)
                                    startActivity(intent)
                                }
                                1 -> {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                2 -> {
                                    val intent = Intent(this@LoginActivity, MainActivityAdmin::class.java)
                                    startActivity(intent)
                                }
                            }

                        } else {
                            Toast.makeText(applicationContext, "Invalid Request", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = textInputEditTextEmail!!.text.toString().trim { it <= ' ' }
                params["password"] = textInputEditTextPassword!!.text.toString().trim { it <= ' ' }
                return params
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }


    private fun emptyInputEditText() {
        textInputEditTextEmail!!.text = null
        textInputEditTextPassword!!.text = null
    }
}

/**
 * This method is to empty all input edit text
 */





