package com.timer.recyclerViewAdapter

import android.content.Context
import com.timer.R
import com.timer.firebaseUtils.StorageUtils
import com.timer.glide.GlideApp
import com.timer.model.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.single_people_layout.*

class Person(val person :User,val uid :String,val ctx :Context):Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.name_people.text=person.name
        viewHolder.bio_people.text=person.bio
        if (person.image!=null)
            GlideApp.with(ctx).load(StorageUtils.onStorageReferencePath(person.image))
                    .placeholder(R.drawable.default_img)
                    .into(viewHolder.profile_people)
    }

    override fun getLayout()= R.layout.single_people_layout
}