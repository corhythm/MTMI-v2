package com.mju.mtmi.database.entity

data class Chat(var chatRoomId: String, var sendUser: String, var receiveUser: String) {
    constructor(): this("", "", "")
}
