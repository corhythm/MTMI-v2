package com.example.mtmimyeon_gitmi.crawling

import android.text.BoringLayout
import android.util.Log
import com.example.mtmimyeon_gitmi.LmsAuthenticationDialog
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.Exception

class CrawlingLmsInfo(private val lmsAuthenticationDialog: LmsAuthenticationDialog) {
    private val TAG = "로그"
    private var cookies = HashMap<String, String>() // cookies value
    private var data = HashMap<String, String>() // data
    private val myId = "60172121" // input your id
    private val myPw = "ksu99038485!" // input your pw
    private val subjectList = ArrayList<Element>() // 과목
    private val profAndTimeList = ArrayList<Element>() // 교수


    // 파라미터로 넘어온 값을 토대로 크롤링하고 결과값을 반환
    private fun doCrawling(
        url: String,
        cookies: HashMap<String, String>,
        data: HashMap<String, String> = HashMap<String, String>(),
        httpMethod: Connection.Method
    ): Connection.Response {
        return Jsoup.connect(url)
            .followRedirects(true)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .cookies(cookies)
            .data(data)
            .method(httpMethod)
            .execute()
    }

    fun startCrawling() {

        try {
            // 1. 처음 세션 값을 얻기 위해 통합로그인 페이지에서 cookie 값 GET
            val res1 = doCrawling(
                url = "https://sso1.mju.ac.kr/login.do?redirect_uri=http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp",
                cookies = this.cookies,
                httpMethod = Connection.Method.GET
            )
            cookies["JSESSIONID"] = res1.cookie("JSESSIONID")
            Log.d(TAG, "res1.headers = ${res1.headers()}")
            Log.d(TAG, "res1.cookies = ${res1.cookies()}")
//        Log.d(TAG, "res1.body = ${res1.body()}")
            Log.d(
                TAG,
                "============================================================================================================================"
            )
            lmsAuthenticationDialog.catchCurrentStart("Authentication", "lms 세션 받아오기 성공")

            // 2. 학부생 id, pw POST로 전송
            data["id"] = myId
            data["passwrd"] = myPw

            val res2 = doCrawling(
                url = "https://sso1.mju.ac.kr/mju/userCheck.do",
                cookies = this.cookies,
                data = data,
                httpMethod = Connection.Method.POST
            )

            Log.d(TAG, "res2.headers = ${res2.headers()}")
            Log.d(TAG, "res2.cookies = ${res2.cookies()}")
            Log.d(TAG, "res2.body = ${res2.body()}")
            Log.d(
                TAG,
                "============================================================================================================================"
            )

            lmsAuthenticationDialog.catchCurrentStart("Authentication", "LMS 로그인 성공! 잠시만 기다려주세요")
            // 3. 데이터 안의 id, pw key값 이름 바꿔주고 redirect_uri 키 추가.
            data.clear()
//            cookies["JSESSIONID"] = res1.cookie("JSESSIONID")
            data["user_id"] = myId
            data["user_pwd"] = myPw
            data["redirect_uri"] = "http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp"

            val res3 = doCrawling(
                url = "https://sso1.mju.ac.kr/login/ajaxActionLogin2.do",
                cookies = this.cookies,
                data = data,
                httpMethod = Connection.Method.POST
            )

            Log.d(TAG, "res3.headers = ${res3.headers()}")
            Log.d(TAG, "res3.cookies = ${res3.cookies()}")
            Log.d(TAG, "res3.body = ${res3.body()}")

            // 여기서 세션 id 값이 바뀌므로 그전에 사용하던 거 사용하면 안 됨
            // 세션 값 교체
            // SCOUTER, access_token, refresh_token 쿠키 값 추가
            cookies["JSESSIONID"] = res3.cookie("JSESSIONID")
            cookies["SCOUTER"] = res3.cookie("SCOUTER")
            cookies["access_token"] = res3.cookie("access_token")
            cookies["refresh_token"] = res3.cookie("refresh_token")
            Log.d(
                TAG,
                "============================================================================================================================"
            )
            lmsAuthenticationDialog.catchCurrentStart("Authentication", "토큰 받아오기 성공! 거의 다 완료됐어요")

            val res4 = doCrawling(
                url = "https://lms.mju.ac.kr/ilos/lo/login_bandi_sso.acl",
                cookies = this.cookies,
                data = data,
                httpMethod = Connection.Method.POST
            )


            Log.d(TAG, "res4.headers = ${res4.headers()}")
            Log.d(TAG, "res4.cookies = ${res4.cookies()}")
            Log.d(TAG, "res4.body = ${res4.body()}")
            Log.d(
                TAG,
                "============================================================================================================================"
            )

            lmsAuthenticationDialog.catchCurrentStart("Authentication", "lms 인증 성공!")

            // data 값 초기화
            data.clear()
            // 2021학년도 1학기 수강신청한 과목 불러오기
            data["YEAR"] = "2021"
            data["TERM"] = "1"
            data["encoding"] = "utf-8"

            val res5 = doCrawling(
                url = "https://lms.mju.ac.kr/ilos/mp/course_register_list.acl",
                cookies = this.cookies,
                data = data,
                httpMethod = Connection.Method.POST
            )

            Log.d(TAG, "res5.headers = ${res5.headers()}")
            Log.d(TAG, "res5.cookies = ${res5.cookies()}")
            Log.d(TAG, "res5.body = ${res5.body()}")
            Log.d(
                TAG,
                "============================================================================================================================"
            )

            // 현재 학기 수강중인 과목들 출력
//        Jsoup.parse(res5.body()).select("div > div > div > div > a > span").forEach { subjectList.add(it) }
//        Jsoup.parse(res5.body()).select("div > div > div > ul > li").forEach { profAndTimeList.add(it) }
//
//        var j = 0
//        for(i in 0 until subjectList.size) {
//            println("${subjectList[i].text()} ${profAndTimeList[j++].text()} ${profAndTimeList[j++].text()}")
//        }

            // # 과목 코드 >> ['eclassRoom(', 'A20203KMA021010012', '); return false;']
            val kjKeyHtml = Jsoup.parse(res5.body()).select("div > div > div > a")
            val kjKeyList = ArrayList<String>()

            for (sj in kjKeyHtml) {
                kjKeyList.add(sj.attributes()["onclick"].split("'")[1])
                Log.d(TAG, sj.attributes()["onclick"].split("'")[1])
            }

            cookies["_language_"] = "ko"

            data.clear()
            data["KJKEY"] = kjKeyList[4] // temp
            data["returnURI"] = "/ilos/st/course/submain_form.acl"
            data["encoding"] = "utf-8"

            val res6 = doCrawling(
                url = "https://lms.mju.ac.kr/ilos/st/course/eclass_room2.acl",
                cookies = this.cookies,
                data = data,
                httpMethod = Connection.Method.POST
            )

            Log.d(TAG, "res6.cookies() = ${res6.cookies()}")
            Log.d(TAG, "res6.headers() = ${res6.headers()}")
            Log.d(TAG, "res6.body() = ${res6.body()}")

            data.clear()
            data["start"] = ""
            data["display"] = "1"
            data["SCH_VALUE"] = ""
            data["ud"] = myId
            data["ky"] = kjKeyList[4]
            data["encoding"] = "utf-8"


            val res7 = doCrawling(
                url = "https://lms.mju.ac.kr/ilos/st/course/report_list.acl",
                cookies = this.cookies,
                data = data,
                httpMethod = Connection.Method.POST
            )

            Log.d(TAG, Jsoup.parse(res7.body()).select("table > tbody > tr > td").text())
        } catch (exception: Exception) {
            Log.d(TAG, "Exception: $exception")
        }

    }
}