package com.example.mtmimyeon_gitmi.db

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatMessage(var chatRoomId: Int, var userId: String, var message: String) {
    private var time: String

    init {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
        val formatted = current.format(formatter)
        this.chatRoomId = chatRoomId // 채팅방 ID
        this.userId = userId //보낸유저
        this.message = message //메시지 내용
        this.time =  formatted//보낸시간
    }

}