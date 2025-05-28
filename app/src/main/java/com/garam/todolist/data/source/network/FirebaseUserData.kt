package com.garam.todolist.data.source.network

data class FirebaseUserData(
    var uid : String = "",
    var email : String? = "",
    var loginType : String = ""
)
