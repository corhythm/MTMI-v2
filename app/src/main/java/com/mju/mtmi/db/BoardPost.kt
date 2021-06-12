package com.mju.mtmi.db

import java.io.Serializable

data class BoardPost(
    var subjectCode: String?, // 과목코드
    var title: String?, // 게시물 제목
    var day: String?, // 게시물 날짜
    var content: String?, // 게시물 내용
    var writerUid: String?, // 작성자 uid (user)
    var writerName: String?,
    var subjectBoardIndex: String?, // 게시물의 인덱스
    var view: Int?

):Serializable {
    constructor() : this("","","","","","","",-1,)
}
