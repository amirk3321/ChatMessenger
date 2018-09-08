package com.timer.fragmetPagerAdapter


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.timer.R
import com.timer.firebaseUtils.StorageUtils
import com.timer.firebaseUtils.firestoreUtil
import com.timer.glide.GlideApp
import com.timer.model.User
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private val RC_IMAGE_CHANGE=123
    private var mImageStateChange=false
    private lateinit var mImagebyte : ByteArray
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        view.apply {
            profile.setOnClickListener {
                val imageintent=Intent().apply {
                    type="image/*"
                    action=Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(imageintent,"Select Image"),RC_IMAGE_CHANGE)
            }
            save.setOnClickListener {
                if (::mImagebyte.isInitialized){
                    StorageUtils.onUpladImagebyteimage(mImagebyte){

                        firestoreUtil.onUpdateUserData(name.text.toString(),
                                bio.text.toString(),it)
                    }
                }else
                    firestoreUtil.onUpdateUserData(name.text.toString(),bio.text.toString(),null)

            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_IMAGE_CHANGE &&
                resultCode ==Activity.RESULT_OK &&
                data!=null && data.data !=null){
            val selectedimage=data.data
            val imagebmp=MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedimage)
            val outputStream=ByteArrayOutputStream()
            imagebmp.compress(Bitmap.CompressFormat.JPEG,99,outputStream)
            mImagebyte=outputStream.toByteArray()
            GlideApp.with(this).load(mImagebyte)
                    .into(profile)
            mImageStateChange=true
        }
    }

    override fun onStart() {
        super.onStart()
        firestoreUtil.getCurrentUser { user: User ->
            if (this@ProfileFragment.isVisible){
                name.setText(user.name)
                bio.setText(user.bio)
                if (!mImageStateChange && user.image!=null){
                    GlideApp.with(this)
                            .load(StorageUtils.onStorageReferencePath(user.image))
                            .placeholder(R.drawable.default_img)
                            .into(profile)
                }
            }
        }
    }
}
