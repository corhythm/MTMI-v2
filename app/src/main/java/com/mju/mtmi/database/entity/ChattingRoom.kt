package com.mju.mtmi.database.entity

data class ChattingRoom(val chattingRoomIdx: String, val chattingParticipants: ArrayList<String>?) {
    constructor() : this(chattingRoomIdx= "", chattingParticipants = null)
}
