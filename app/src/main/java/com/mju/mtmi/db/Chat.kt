package com.mju.mtmi.db

data class Chat(var chatRoomId: String, var sendUser: String, var receiveUser: String) {
    constructor(): this("", "", "")
}
