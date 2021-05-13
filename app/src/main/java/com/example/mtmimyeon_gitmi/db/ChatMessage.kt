package com.example.mtmimyeon_gitmi.db

class ChatMessage(var chatRoomId: Int,var userId: String,var message: String, var time: Int) {
    init {
        this.chatRoomId = chatRoomId // 채팅방 ID
        this.userId = userId //보낸유저
        this.message = message //메시지 내용
        this.time = time //보낸시간

    }
}