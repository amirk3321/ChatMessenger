package com.timer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.timer.AppConstent.AppConstent
import com.timer.firebaseUtils.StorageUtils
import com.timer.firebaseUtils.firestoreUtil
import com.timer.glide.GlideApp
import com.timer.model.Imagemassge
import com.timer.model.TextMassage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.single_image_layout.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var mRegistrationListener : ListenerRegistration
    private var mRecyclerViewState=true
    private lateinit var mMassageSection : Section
    private val RC_SELECTIMAGE=123
    private lateinit var mChannelId :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=intent.getStringExtra(AppConstent.NAMR)
        val otherUID =intent.getStringExtra(AppConstent.USER_ID)
        firestoreUtil.onCreateChatChannel(otherUID){
            ChannelId ->
            mChannelId=ChannelId
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
               val imageIntent=Intent().apply {
                   type="Image/*"
                   action=Intent.ACTION_GET_CONTENT
                   putExtra(Intent.EXTRA_MIME_TYPES, arrayListOf("image/png,image/jpeg"))
               }
                startActivityForResult(imageIntent,RC_SELECTIMAGE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SELECTIMAGE && resultCode == Activity.RESULT_OK &&
                data!=null && data.data !=null){
            val selectedimageUri=data.data
            val selectedImgBMP=MediaStore.Images.Media.getBitmap(contentResolver,selectedimageUri)
            val outputStream=ByteArrayOutputStream()
            selectedImgBMP.compress(Bitmap.CompressFormat.JPEG,99,outputStream)
            val imagebyteArray=outputStream.toByteArray()
            StorageUtils.onSentMassagePicture(imagebyteArray){
                imagepath: String ->
                val sentImagemsg=Imagemassge(imagepath,Calendar.getInstance().time,FirebaseAuth.getInstance().currentUser!!.uid)
                firestoreUtil.onSentMassage(sentImagemsg,mChannelId)

            }
        }
    }
}
