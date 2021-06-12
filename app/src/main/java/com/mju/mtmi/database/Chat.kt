package com.mju.mtmi.database

data class Chat(var chatRoomId: String, var sendUser: String, var receiveUser: String) {
    constructor(): this("", "", "")
}
