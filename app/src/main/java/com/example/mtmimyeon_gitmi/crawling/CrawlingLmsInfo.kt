package com.example.mtmimyeon_gitmi.crawling

import android.text.BoringLayout
import org.jsoup.Connection
import org.jsoup.Jsoup

class CrawlingLmsInfo {
    private var cookies = HashMap<String, String>() // cookies value
    private val headers = HashMap<String, String>() // header
    private var data = HashMap<String, String>() // data
    private val myId = "60172121" // input your id
    private val myPw = "ksu99038485!" // input your pw

    init {
        // set header
        headers["Accept"] =
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        headers["Accept-Encoding"] = "gzip, deflate, br"
        headers["Accept-Language"] = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7"
        headers["Connection"] = "keep-alive"
        headers["Host"] = "sso1.mju.ac.kr"
        headers["sec-ch-ua-mobile"] = "?0"
        headers["Sec-Fetch-Dest"] = "document"
        headers["Sec-Fetch-Mode"] = "navigate"
        headers["Sec-Fetch-Site"] = "none"
        headers["Upgrade-Insecure-Requests"] = "1"
        headers["User-Agent"] =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Whale/2.9.116.15 Safari/537.36"

        // set cookies
        cookies["JSESSIONID"] = ""
        cookies["SCOUTER"] = ""
        cookies["access_token"] = ""
        cookies["refresh_token"] = ""
    }

    fun doCrawling() {
        val res1 =
            Jsoup.connect("https://sso1.mju.ac.kr/login.do?redirect_uri=http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp")
                .headers(headers)
                .followRedirects(true)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .method(Connection.Method.GET)
                .execute()

        cookies["JSESSIONID"] = res1.cookie("JSESSIONID")

        println("1) request url = https://sso1.mju.ac.kr/login.do?redirect_uri=http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp")
        println("sessionId = $cookies[\"JSESSIONID\"]")
        //println("res1.body = ${res1.body()}")
        println("res1.headers = ${res1.headers()}")
        println("res1.cookies = ${res1.cookies()}")
        println("-------------------------------------------------------------------------------------------")




        val res2 = Jsoup.connect("https://sso1.mju.ac.kr/mju/userCheck.do")
            .headers(headers)
            .followRedirects(true)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .method(Connection.Method.POST)
            .cookies(cookies)
            .data("id", myId)
            .data("passwrd", myPw)
            .timeout(0) // infinite timeout
            .execute()

        println("2) request url = https://sso1.mju.ac.kr/mju/userCheck.do")
        println("res2.headers = ${res2.headers()}")
        println("res2.cookies = ${res2.cookies()}")
        println("res2.body = ${res2.body()}")
        println("-------------------------------------------------------------------------------------------")


        val res3 = Jsoup.connect("https://sso1.mju.ac.kr/login/ajaxActionLogin2.do")
            .headers(headers)
            .followRedirects(true)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .method(Connection.Method.POST)
            .cookie("JSESSIONID", cookies["JSESSIONID"])
            .data("user_id", myId)
            .data("user_pwd", myPw)
            .data("redirect_uri", "http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp")
            .timeout(0) // infinite timeout
            .execute()

        // 여기서 세션 id 값이 바뀌므로 그전에 사용하던 거 사용하면 안 됨
        cookies["JSESSIONID"] = res3.cookie("JSESSIONID")
        cookies["SCOUTER"] = res3.cookie("SCOUTER")
        cookies["access_token"] = res3.cookie("access_token")
        cookies["refresh_token"] = res3.cookie("refresh_token")

        println("3) request url = https://sso1.mju.ac.kr/login/ajaxActionLogin2.do")
        println("res3.headers = ${res3.headers()}")
        println("res3.cookies = ${res3.cookies()}")
        println("res3.body = ${res3.body()}")
        println("-------------------------------------------------------------------------------------------")

        val res4: Connection.Response = Jsoup.connect("https://lms.mju.ac.kr/ilos/lo/login_bandi_sso.acl")
            .headers(headers)
            .followRedirects(true)
            .ignoreHttpErrors(true)
            .ignoreContentType(true)
            .method(Connection.Method.GET)
            .cookies(cookies)
            .timeout(0) // infinite timeout
            .execute()

        println("5) request url = https://lms.mju.ac.kr/ilos/lo/login_bandi_sso.acl")
        println("res5.headers = ${res4.headers()}")
        println("res5.cookies = ${res4.cookies()}")
        println("res5 = ${res4.body()}")
        println("-------------------------------------------------------------------------------------------")

        val res5 = Jsoup.connect("https://lms.mju.ac.kr/ilos/mp/course_register_list.acl")
            .headers(headers)
            .followRedirects(true)
            .ignoreHttpErrors(true)
            .ignoreContentType(true)
            .method(Connection.Method.POST)
            .cookies(cookies)
            .data("YEAR", "2021")
            .data("TERM", "1")
            .data("encoding", "utf-8")
            .timeout(0) // infinite timeout
            .execute()

        println("5) request url = https://lms.mju.ac.kr/ilos/mp/course_register_list.acl")
        println("res6.headers = ${res5.headers()}")
        println("res6.cookies = ${res5.cookies()}")
        //println("res6 = ${res5.body()}") // 전체 html 문서 출력
        println(Jsoup.parse(res5.body()).select("div > div > div > div > a > span"))
        println("-------------------------------------------------------------------------------------------")
    }
}