package com.example.mtmimyeon_gitmi.db

data class SubjectBoard(
    var subjectCode: String,
    var title: String,
    var day: String,
    var content: String,
    var comment: String,
    var writerUid: String,
    var subjectBoardIndex: Int,
    var status: Boolean
) {
    init {
        this.subjectCode = subjectCode
        this.title = title
        this.day = day
        this.content = content
        this.comment = comment
        this.writerUid = writerUid
        this.subjectBoardIndex = subjectBoardIndex
        this.status = status
    }
}
