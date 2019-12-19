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
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.hexanovate.familytree.R
import com.hexanovate.familytree.model.Person
import com.hexanovate.familytree.util.IOUtils
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A special subclass of [CircleImageView] specifically for displaying images of [people][Person].
 *
 * Essentially, it encapsulates the process of retrieving the image of a [Person], and deciding on
 * the border colour.
 */
class PersonCircleImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : CircleImageView(context, attrs, defStyle) {

    var person: Person? = null
        set(value) {
            field = value
            updatePersonImage()
        }

    init {
        updatePersonImage()
    }

    /**
     * Updates the [person]'s image and border color displayed on this [CircleImageView] .
     *
     * @see IOUtils.getDefaultImage
     * @see IOUtils.readPersonImageWithDefault
     */
    private fun updatePersonImage() {
        val imageDrawable: Drawable
        val borderColorRes: Int
        if (person == null) {
            imageDrawable = IOUtils.getDefaultImage(context)
            borderColorRes = R.color.black
        } else {
            imageDrawable =
                    IOUtils.readPersonImageWithDefault(person!!.id, context.applicationContext)
            borderColorRes = person!!.gender.getColorRes()
        }

        setImageDrawable(imageDrawable)
        borderColor = ContextCompat.getColor(context, borderColorRes)
    }

}
