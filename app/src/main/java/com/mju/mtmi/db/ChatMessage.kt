
package com.mju.mtmi.db

data class ChatMessage(
    var chatRoomId: String,
    var name: String,
    var userId: String,
    var message: String,
    var imageUri: String,
    var timeStamp: String
) {
    constructor() : this("","","","","","")

    init {
        this.chatRoomId = chatRoomId // 채팅방 ID
        this.name = name // 유저 이름
        this.userId = userId //보낸유저
        if (imageUri.isEmpty()) {
            imageUri = ""
        }else {
            this.imageUri = imageUri
        }
        this.message = message //메시지 내용
        this.timeStamp = timeStamp//보낸시간
    }
}