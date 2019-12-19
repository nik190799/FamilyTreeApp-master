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

package com.hexanovate.familytree.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hexanovate.familytree.R
import com.hexanovate.familytree.database.manager.MarriagesManager
import com.hexanovate.familytree.model.Person

/**
 * A compound view for displaying the icon and name of a person.
 */
class PersonView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val personImageView: PersonCircleImageView
    private val nameTextView: TextView
    private val marriageIcon: ImageView

    var person: Person? = null
        set(value) {
            if (value == null) {
                throw IllegalArgumentException("person cannot be null")
            }

            field = value

            nameTextView.text = value.fullName
            personImageView.person = value

            val isMarried = MarriagesManager(context).getMarriages(value.id).isNotEmpty()
            marriageIcon.visibility = if (isMarried) View.VISIBLE else View.GONE
        }

    init {
        // Inflate XML resource for compound view with "this" as the parent
        val personView = View.inflate(context, R.layout.widget_person, this)
        personImageView = personView.findViewById(R.id.circleImageView)
        nameTextView = personView.findViewById(R.id.name)
        marriageIcon = personView.findViewById(R.id.icon_married)
    }

}
