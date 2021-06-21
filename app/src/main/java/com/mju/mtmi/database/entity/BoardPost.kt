package com.mju.mtmi.database.entity

import java.io.Serializable

data class BoardPost(
    var boardIdx: String, // 과목코드
    var title: String, // 게시물 제목
    var timeStamp: String, // 게시물 날짜
    var content: String, // 게시물 내용
    var writerIdx: String, // 작성자 uid (user)
    var postIdx: String, // 게시물의 인덱스
    var commentCount: Int

) : Serializable {
    constructor() : this(
        boardIdx = "",
        title = "",
        timeStamp = "",
        content = "",
        writerIdx = "",
        postIdx = "",
        commentCount = - 1,
    )
}
