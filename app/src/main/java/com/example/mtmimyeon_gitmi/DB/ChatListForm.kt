package com.example.mtmimyeon_gitmi.db

data class ChatListForm(
    var imgUrl: String,
    var name: String,
    var lastChat: String,
    var timeStamp: String,
    var chatRoomId: String
){
    constructor() : this("","","","","")
}

