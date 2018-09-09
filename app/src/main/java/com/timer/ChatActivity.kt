package com.timer

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.timer.AppConstent.AppConstent
import com.timer.firebaseUtils.firestoreUtil
import com.timer.model.TextMassage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var mRegistrationListener : ListenerRegistration
    private var mRecyclerViewState=true
    private lateinit var mMassageSection : Section
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=intent.getStringExtra(AppConstent.NAMR)
        val otherUID =intent.getStringExtra(AppConstent.USER_ID)
        firestoreUtil.onCreateChatChannel(otherUID){
            ChannelId ->
            snet_massage_text.setOnClickListener {
                val sentmsg=TextMassage(text_massage.text.toString(),
                        Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        AppConstent.TEXT)
                text_massage.setText("")
                firestoreUtil.onSentMassage(sentmsg,ChannelId)
            }
            sent_image_msg.setOnClickListener {
                toast("not Implemented yet..")
                //TODO("Not Implement yet..")
            }

            mRegistrationListener=firestoreUtil.onUpdateMassageListener(this,ChannelId){
                massageslistItem ->
                fun init(){
                    massage_recyclerview.apply {
                        layoutManager=LinearLayoutManager(this@ChatActivity)
                        adapter=GroupAdapter<ViewHolder>().apply {
                            mMassageSection=Section(massageslistItem)
                            this.add(mMassageSection)
                        }
                    }
                    mRecyclerViewState=false
                }
                fun update() =mMassageSection.update(massageslistItem)

                if (mRecyclerViewState)
                    init()
                else
                    update()

                massage_recyclerview.scrollToPosition(massage_recyclerview.adapter.itemCount -1)

            }

        }

    }
}
