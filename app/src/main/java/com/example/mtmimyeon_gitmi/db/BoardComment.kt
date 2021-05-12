package com.example.mtmimyeon_gitmi.db

class BoardComment(var commenter: String, var day: String, var content: String) {
    init {
        this.commenter = commenter
        this.day = day
        this.content = content
    }
}