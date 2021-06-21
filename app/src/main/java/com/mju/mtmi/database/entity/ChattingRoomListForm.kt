package com.mju.mtmi.database.entity

// 이건 DB와 일대일 매칭되는 entity는 아님.
data class ChattingRoomListForm(
    val chattingRoomIdx: String,
    val sendUserIdx: String,
    val receiveUserIdx: String,
    val lastChattingMessage: LastChattingMessage?
) {
    constructor() : this(chattingRoomIdx = "", sendUserIdx = "", receiveUserIdx = "", lastChattingMessage = null)
}
