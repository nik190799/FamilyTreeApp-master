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

package com.hexanovate.familytree.ui.validator

import android.view.View
import com.hexanovate.familytree.R
import com.hexanovate.familytree.model.Gender
import com.hexanovate.familytree.model.Person
import com.hexanovate.familytree.util.toTitleCase
import org.threeten.bp.LocalDate

/**
 * Responsible for validating a [Person].
 *
 * @see Person
 * @see Validator
 */
class PersonValidator(
        rootView: View,
        private val personId: Int,
        private val forename: String,
        private val surname: String,
        private val gender: Gender,
        private val dateOfBirth: LocalDate?,
        private val placeOfBirth: String,
        private val dateOfDeath: LocalDate?,
        private val placeOfDeath: String
) : Validator<Person>(rootView) {

    override fun validate(): Person? {
        if (!checkNames(forename, surname)) return null

        // Dates should be ok from dialog constraint, but best to double-check before db write
        if (!checkDates(dateOfBirth, dateOfDeath)) return null

        return Person(
                personId,
                forename.trim().toTitleCase('-'),
                surname.trim().toTitleCase('-'),
                gender,
                dateOfBirth!!,
                placeOfBirth.trim().toTitleCase(),
                dateOfDeath,
                placeOfDeath.trim().toTitleCase()
        )
    }

    /**
     * Checks that the forename and surname of the person are not blank, showing a message in the UI
     * if so.
     *
     * @return true if valid
     */
    private fun checkNames(forename: String, surname: String): Boolean {
        if (forename.isBlank() || surname.isBlank()) {
            showMessage(R.string.validate_name_empty)
            return false
        }
        return true
    }

    /**
     * Checks that the date of birth is not null, not in the future, and is before the date of death
     * if applicable.
     *
     * @return true if valid
     */
    private fun checkDates(dateOfBirth: LocalDate?, dateOfDeath: LocalDate?): Boolean {
        if (dateOfBirth == null) {
            showMessage(R.string.validate_dateOfBirth_empty)
            return false
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            showMessage(R.string.validate_dateOfBirth_future)
            return false
        }

        if (dateOfDeath != null && dateOfDeath.isBefore(dateOfBirth)) {
            showMessage(R.string.validate_dateOfDeath_beforeBirth)
            return false
        }

        return true
    }

}
