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

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hexanovate.familytree.R
import com.hexanovate.familytree.database.manager.MarriagesManager
import com.hexanovate.familytree.database.manager.PersonManager
import com.hexanovate.familytree.model.Anniversary
import com.hexanovate.familytree.model.Birthday
import com.hexanovate.familytree.model.Event
import com.hexanovate.familytree.ui.widget.PersonCircleImageView

import com.hexanovate.familytree.util.OnDataClick

/**
 * A [RecyclerView] adapter for displaying [events] in a standard list layout.
 */
class EventAdapterUser(
        private val context: Context,
        private val events: List<Event>
) : RecyclerView.Adapter<EventAdapterUser.ViewHolder>() {

    private var onItemClickAction: OnDataClick<Event>? = null

    fun onItemClick(action: OnDataClick<Event>) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_event, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val event = events[position]
        when (event) {
            is Anniversary -> setupAnniversaryLayout(holder!!, event)
            is Birthday -> setupBirthdayLayout(holder!!, event)
        }
    }

    private fun setupAnniversaryLayout(holder: ViewHolder, anniversary: Anniversary) {
        val marriage = MarriagesManager(context).get(anniversary.personIds)

        val pm = PersonManager(context)
        val person1 = pm.get(marriage.person1Id)
        val person2 = pm.get(marriage.person2Id)

        with(holder) {
            imageIcon.setImageResource(R.drawable.ic_marriage_black_24dp)
            titleText.text = person1.fullName + " and " + person2.fullName
            subtitleText.text = anniversary.getDateText()
            personImageView1.person = person1
            personImageView2.person = person2
        }
    }

    private fun setupBirthdayLayout(holder: ViewHolder, birthday: Birthday) {
        val person = PersonManager(context).get(birthday.personId)

        with(holder) {
            imageIcon.setImageResource(R.drawable.ic_birthday_black_24dp)
            titleText.text = person.fullName
            subtitleText.text = birthday.getDateText()
            personImageView1.person = person
            personImageView2.visibility = View.GONE
        }
    }

    override fun getItemCount() = events.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIcon: ImageView = itemView.findViewById(R.id.imageView_icon)
        val titleText: TextView = itemView.findViewById(R.id.title)
        val subtitleText: TextView = itemView.findViewById(R.id.subtitle)
        val personImageView1: PersonCircleImageView = itemView.findViewById(R.id.imageView_person1)
        val personImageView2: PersonCircleImageView = itemView.findViewById(R.id.imageView_person2)

        init {
            itemView.setOnClickListener {
                val position = layoutPosition
                onItemClickAction?.invoke(it, events[position])
            }
        }
    }

}
