package com.timer.firebaseUtils

import android.content.Context
import android.util.Log
import com.timer.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.timer.AppConstent.AppConstent
import com.timer.AppConstent.LogConstent
import com.timer.model.TextMassage
import com.timer.model.chatChannel
import com.timer.model.massage
import com.timer.recyclerViewAdapter.Person
import com.timer.recyclerViewAdapter.TextMassageItems
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.appcompat.v7.Appcompat

object firestoreUtil {
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUerRef: DocumentReference
        get() = firestore.document("user/${FirebaseAuth.getInstance()
                .currentUser?.uid ?: throw NullPointerException("UID is Null")}")

    val chatchannelRef = firestore.collection("ChatChannel")

    fun initilizeUser(onComplete: () -> Unit) {
        currentUerRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newuser = User(FirebaseAuth.getInstance()
                        .currentUser?.displayName ?: ""
                        , "", null)
                currentUerRef.set(newuser).addOnCompleteListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    fun onUpdateUserData(name: String, bio: String, imagepath: String?) {
        val datamap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) datamap["name"] = name
        if (bio.isNotBlank()) datamap["bio"] = bio
        if (imagepath != null) datamap["image"] = imagepath
        currentUerRef.update(datamap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUerRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    fun onItemUpdateListener(context: Context, onSuccess: (List<Item>) -> Unit): ListenerRegistration {
        return firestore.collection("user").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e(LogConstent.TAG, LogConstent.MSG, firebaseFirestoreException)
                return@addSnapshotListener
            }
            val listItems = mutableListOf<Item>()
            querySnapshot!!.documents.forEach {
                if (it.id != FirebaseAuth.getInstance().currentUser?.uid) {
                    listItems.add(Person(it.toObject(User::class.java)!!, it.id, context))
                }
            }
            onSuccess(listItems)
        }
    }

    fun onRemoveListenerRegistration(registrationlistener: ListenerRegistration) = registrationlistener.remove()

    fun onCreateChatChannel(otherUID: String, onSuccess: (ChannelId: String) -> Unit) {
        currentUerRef.collection("EngageUsers").document(otherUID)
                .get().addOnSuccessListener {
                    if (it.exists()) {
                        onSuccess(it["chatchannelID"] as String)
                        return@addOnSuccessListener
                    }
                    val currentUID = FirebaseAuth.getInstance().currentUser!!.uid
                    val newchatChannel = chatchannelRef.document()
                    newchatChannel.set(chatChannel(mutableListOf(currentUID, otherUID)))

                    currentUerRef.collection("EngageUsers")
                            .document(otherUID)
                            .set(mapOf("chatchannelID" to newchatChannel.id))

                    firestore.collection("user")
                            .document(otherUID)
                            .collection("EngageUsers")
                            .document(currentUID)
                            .set(mapOf("chatchannelID" to newchatChannel.id))
                    onSuccess(newchatChannel.id)
                }
    }

    fun onUpdateMassageListener(context: Context, channelId: String,
                                onSuccess: (massageslistItem: List<Item>) -> Unit): ListenerRegistration {
        return chatchannelRef.document(channelId).collection("massages")
                .orderBy("time").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e(LogConstent.TAG, LogConstent.MSG, firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    val listItems = mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        if (it["type"]==AppConstent.TEXT){
                            listItems.add(TextMassageItems(it.toObject(TextMassage::class.java)!!,context))
                        }else{}
                    }
                    onSuccess(listItems)
                }
    }
    fun onSentMassage(massage :massage,channelId: String){
        chatchannelRef.document(channelId).collection("massages")
                .add(massage)
    }
}

