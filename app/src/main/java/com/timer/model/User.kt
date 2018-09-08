package com.timer.model

data class User(val name : String,val bio : String,val image : String?) {
    constructor(): this("","",null)
}