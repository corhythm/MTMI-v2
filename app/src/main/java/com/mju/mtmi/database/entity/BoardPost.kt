package com.mju.mtmi.database.entity

import java.io.Serializable

data class BoardPost(
    var subjectCode: String, // 과목코드
    var title: String, // 게시물 제목
    var timeStamp: String, // 게시물 날짜
    var content: String, // 게시물 내용
    var writerUid: String, // 작성자 uid (user)
    var writerName: String,
    var subjectBoardIndex: String, // 게시물의 인덱스
    var commentCount: Int

) : Serializable {
    constructor() : this(
        subjectCode = "",
        title = "",
        timeStamp = "",
        content = "",
        writerUid = "",
        writerName = "",
        subjectBoardIndex = "",
        commentCount = - 1,
    )
}
