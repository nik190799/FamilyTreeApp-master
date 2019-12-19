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

package com.hexanovate.familytree.ui.admin.marriage

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hexanovate.familytree.R
import com.hexanovate.familytree.database.manager.PersonManager
import com.hexanovate.familytree.model.Marriage
import com.hexanovate.familytree.ui.widget.PersonCircleImageView

import com.hexanovate.familytree.util.DATE_FORMATTER_LONG
import com.hexanovate.familytree.util.OnDataClick

/**
 * A [RecyclerView] adapter for displaying [marriages] in a standard list layout.
 */
class MarriageAdapterAdmin(
        private val context: Context,
        private val personId: Int,
        private val marriages: List<Marriage>
) : RecyclerView.Adapter<MarriageAdapterAdmin.ViewHolder>() {

    private var onItemClickAction: OnDataClick<Marriage>? = null

    fun onItemClick(action: OnDataClick<Marriage>) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_person, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val marriage = marriages[position]
        val spouseId = marriage.getOtherSpouseId(personId)
        val spouse = PersonManager(context).get(spouseId)

        with(holder!!) {
            nameText.text = spouse.fullName
            infoText.text = marriage.startDate.format(DATE_FORMATTER_LONG)
            personImageView.person = spouse
        }
    }

    override fun getItemCount() = marriages.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val personImageView: PersonCircleImageView = itemView.findViewById(R.id.circleImageView)
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val infoText: TextView = itemView.findViewById(R.id.text2)

        init {
            itemView.setOnClickListener {
                val position = layoutPosition
                onItemClickAction?.invoke(it, marriages[position])
            }
        }
    }

}
