package com.example.mtmimyeon_gitmi.db

data class Chat(var chatRoomId: Int) {
    init {
        this.chatRoomId = chatRoomId //채팅룸 id
    }
}