package com.example.mtmimyeon_gitmi.db

data class Chat(var chatRoom: Int) {
    init {
        this.chatRoom = chatRoom //채팅룸 id
    }
}