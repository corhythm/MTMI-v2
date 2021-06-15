package com.mju.mtmi.database.entity

data class ChattingRoom(val chattingRoomId: String, val chattingParticipants: ArrayList<String>?) {
    constructor() : this(chattingRoomId = "", chattingParticipants = null)
}
