package com.example.mtmimyeon_gitmi.util

import java.util.regex.Matcher
import java.util.regex.Pattern

class ValidationRegex {
    companion object {

        // 이메일 형식 체크
        fun isRegexEmail(target: String): Boolean {
            val regex: String = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
            val pattern: Pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = pattern.matcher(target)
            return matcher.find()
        }
    }
}