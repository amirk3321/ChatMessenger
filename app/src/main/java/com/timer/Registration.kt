package com.timer

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.timer.firebaseUtils.firestoreUtil
import com.timer.toastMassage.toast
import kotlinx.android.synthetic.main.registration.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class Registration : AppCompatActivity() {
    val mAuthProvider = listOf(AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true)
            .setRequireName(true).build())
    val RC_REGISTRAION=123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        supportActionBar!!.title="Registration"

        if (FirebaseAuth.getInstance().currentUser !=null){
            startActivity<HomeActivity>()
            finish()
        }

        signInGoogle.setOnClickListener {
            startActivityForResult(Intent(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(mAuthProvider).build()),RC_REGISTRAION)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_REGISTRAION){
          val response=IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val progressDialog=indeterminateProgressDialog("Setting up Account...")
                firestoreUtil.initilizeUser {
                    startActivity(intentFor<HomeActivity>().newTask().clearTask())
                    progressDialog.dismiss()
                }

            }else if(resultCode==Activity.RESULT_CANCELED){
                    if (response==null) return

                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK ->{
                        longSnackbar(rootRegistration,"No Network found...")
                    }
                    ErrorCodes.UNKNOWN_ERROR ->{
                        longSnackbar(rootRegistration,"Unknown Error Occurred")
                    }
                }

                }
            }
        }
    }

