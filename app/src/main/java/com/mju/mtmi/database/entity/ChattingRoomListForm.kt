package com.mju.mtmi.database.entity

// 이건 DB와 일대일 매칭되는 entity는 아님.
data class ChattingRoomListForm(
    val chattingRoomId: String,
    val sendUser: String,
    val receiveUser: String,
    val lastChatting: LastChatting?
) {
    constructor() : this("", "", "", lastChatting = null)
}
