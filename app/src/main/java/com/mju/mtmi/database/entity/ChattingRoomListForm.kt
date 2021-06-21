package com.mju.mtmi.database.entity

// 이건 DB와 일대일 매칭되는 entity는 아님.
data class ChattingRoomListForm(
    val chattingRoomIdx: String, // 채팅방 idx
    val chattingParticipants: ArrayList<String>?, // 채팅 참여자들 idx
    val lastChattingMessage: LastChattingMessage? // 마지막 메시지
) {
    constructor() : this(chattingRoomIdx = "", chattingParticipants = null, lastChattingMessage = null)
}
