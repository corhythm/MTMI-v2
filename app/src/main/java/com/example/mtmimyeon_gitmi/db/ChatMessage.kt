package com.example.mtmimyeon_gitmi.db

class ChatMessage(var message: String, var time: Int, var userId: String) {
    init {
        this.message = message
        this.time = time
        this.userId = userId
    }
}