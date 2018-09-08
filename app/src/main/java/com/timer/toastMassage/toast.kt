package com.timer.toastMassage

import android.content.Context
import android.widget.Toast

object toast {
    fun msg(context :Context,msg : String){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
    }
}