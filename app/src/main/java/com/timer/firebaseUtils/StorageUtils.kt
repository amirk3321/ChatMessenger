package com.timer.firebaseUtils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object StorageUtils {
    val storageInstance : FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    val storageuserRef : StorageReference
    get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser!!.uid
    ?: throw NullPointerException("Uid"))

    fun onUpladImagebyteimage(byteimage:ByteArray,onSucess :(imagepath :String) ->Unit){
        val ref= storageuserRef.child("profiles/${UUID.nameUUIDFromBytes(byteimage)}")
        ref.putBytes(byteimage).addOnSuccessListener {
            onSucess(ref.path)
        }
    }
    fun onStorageReferencePath(path :String) = storageInstance.getReference(path)
}