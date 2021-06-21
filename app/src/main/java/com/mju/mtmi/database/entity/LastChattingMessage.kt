package com.mju.mtmi.database.entity

data class LastChattingMessage(val message: String, val timeStamp: String) {
    constructor(): this(message = "", timeStamp = "")
}