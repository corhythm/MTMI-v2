package com.mju.mtmi.database.entity

data class ChattingMessage(
    var chattingRoomId: String,
    var name: String,
    var userId: String,
    var message: String,
    var imageUri: String,
    var timeStamp: String
) {
    constructor() : this(
        chattingRoomId = "",
        name = "",
        userId = "",
        message = "",
        imageUri = "",
        timeStamp = ""
    )
}