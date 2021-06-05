package com.example.mtmimyeon_gitmi.db

data class Chat(var chatRoomId: String, var sendUser: String, var receiveUser: String) {
    constructor() : this("", "", "")

    init {
        this.chatRoomId = chatRoomId //채팅룸 id
        this.sendUser = sendUser
        this.receiveUser = receiveUser
    }
}
