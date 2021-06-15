package com.mju.mtmi.database.entity

data class LastChatting(val message: String, val timeStamp: String) {
    constructor(): this(message = "", timeStamp = "")
}