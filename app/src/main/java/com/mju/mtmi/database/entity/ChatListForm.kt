package com.mju.mtmi.database.entity

data class ChatListForm(
    var imgUrl: String,
    var name: String,
    var lastChat: String,
    var timeStamp: String,
    var chatRoomId: String
){
    constructor() : this("","","","","")
}

