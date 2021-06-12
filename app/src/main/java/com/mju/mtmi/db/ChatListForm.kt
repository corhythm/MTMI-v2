package com.mju.mtmi.db

data class ChatListForm(
    var imgUrl: String,
    var name: String,
    var lastChat: String,
    var timeStamp: String,
    var chatRoomId: String
){
    constructor() : this("","","","","")
}

