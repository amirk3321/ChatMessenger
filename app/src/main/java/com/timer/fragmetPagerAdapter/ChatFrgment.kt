package com.timer.fragmetPagerAdapter


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ListenerRegistration
import com.timer.R
import com.timer.firebaseUtils.firestoreUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_frgment.*


class ChatFrgment : Fragment() {
    lateinit var mRegistrationlistener : ListenerRegistration
    var mRecyclerViewState=true
    lateinit var mPeopleItemsSection : Section
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view= inflater.inflate(R.layout.fragment_chat_frgment, container, false)
        mRegistrationlistener=firestoreUtil.onItemUpdateListener(this.context!!){
            listItems: List<Item> ->
            fun init(){
                people_list_item_recycler.apply {
                    layoutManager= LinearLayoutManager(this@ChatFrgment.context)
                    adapter=GroupAdapter<ViewHolder>().apply {
                        mPeopleItemsSection=Section(listItems)
                        add(mPeopleItemsSection)
                    }
                }
                mRecyclerViewState=false
            }
            fun update(){}
            if (mRecyclerViewState)
                init()
            else
                update()
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreUtil.onRemoveListenerRegistration(mRegistrationlistener)
        mRecyclerViewState=true
    }
}
