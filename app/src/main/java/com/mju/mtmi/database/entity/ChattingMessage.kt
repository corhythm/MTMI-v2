package com.mju.mtmi.database.entity

data class ChattingMessage(
    val chattingRoomIdx: String,
    val chattingMessageIdx: String,
    val writerIdx: String,
    val content: String,
    val timeStamp: String
) {
    constructor() : this(
        chattingRoomIdx = "",
        chattingMessageIdx = "",
        writerIdx = "",
        content = "",
        timeStamp = ""
    )
}