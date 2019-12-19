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

package com.hexanovate.familytree.ui.user

import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.widget.DatePicker
import com.hexanovate.familytree.util.DATE_FORMATTER_LONG
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

/**
 * A helper class to display the date on a [TextInputEditText], and allowing it to be changed
 * through click then dialog.
 *
 * @property context            context from the activity/fragment
 * @property textInputEditText  the [TextInputEditText] being used for the date picker
 *
 * @see DateRangeSelectorHelper
 */
class DateSelectorHelperUser(
        private val context: Context,
        private val textInputEditText: TextInputEditText
) {

    /**
     * The [LocalDate] being displayed. This is null if no date has been selected.
     */
    var date: LocalDate? = null
        set(value) {
            field = value
            textInputEditText.setText(value?.format(DATE_FORMATTER_LONG))
        }

    /**
     * Function for extra actions that can be invoked after the user chooses (or changed) a date.
     */
    var onDateSet: ((view: DatePicker, date: LocalDate) -> Unit)? = null

    /**
     * The minimum date available in the [DatePicker].
     *
     * It can be null (its default value) to indicate no minimum date. It must be less than the
     * maximum date, if specified.
     *
     * @see maxDate
     */
    var minDate: LocalDate? = null
        set(value) {
            requireDateBoundaries(value, maxDate)
            field = value
        }

    /**
     * The maximum date available in the [DatePicker].
     *
     * It can be null (its default value) to indicate no maximum date. It must be greater than the
     * minimum date, if specified.
     *
     * @see minDate
     */
    var maxDate: LocalDate? = null
        set(value) {
            requireDateBoundaries(minDate, value)
            field = value
        }

    init {
        textInputEditText.isFocusableInTouchMode = false

        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        // N.B. month-1 and month+1 in code because Android month values are from 0-11 (to
        // correspond with java.util.Calendar) but LocalDate month values are from 1-12.

        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val newDate = LocalDate.of(year, month + 1, dayOfMonth)
            date = newDate
            onDateSet?.invoke(view, newDate)
        }

        textInputEditText.setOnClickListener {
            val initialDate = date ?: LocalDate.now()

            val datePickerDialog = DatePickerDialog(
                    context,
                    listener,
                    initialDate.year,
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth
            )

            minDate?.let { datePickerDialog.datePicker.minDate = dateToMs(it) }
            maxDate?.let { datePickerDialog.datePicker.maxDate = dateToMs(it) }

            datePickerDialog.show()
        }
    }

    /**
     * Converts a date to the number of milliseconds since 01/01/1970.
     * (The Android API requires minimum/maximum date in milliseconds).
     */
    private fun dateToMs(date: LocalDate) =
            date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    /**
     * Checks if the minimum date is before the maximum date, throwing an [IllegalArgumentException]
     * if not.
     *
     * @see minDate
     * @see maxDate
     */
    private fun requireDateBoundaries(min: LocalDate?, max: LocalDate?) {
        if (min != null && max != null) {
            require(min.isBefore(max)) {
                "the minimum date must be less than the maximum date when both are specified"
            }
        }
    }

}
