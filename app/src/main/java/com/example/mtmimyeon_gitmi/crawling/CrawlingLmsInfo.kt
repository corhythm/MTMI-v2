package com.example.mtmimyeon_gitmi.crawling

import android.util.Log
import com.example.mtmimyeon_gitmi.LmsAuthenticationDialog
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import www.sanju.motiontoast.MotionToast
import java.lang.Exception

enum class CrawlingStatus {
    DOING, FINISH
}

class CrawlingLmsInfo(
    private val myId: String,
    private val myPw: String,
    private val lmsAuthenticationDialog: LmsAuthenticationDialog
) {
    private val TAG = "로그"
    private var cookies = HashMap<String, String>() // cookies value
    private var data = HashMap<String, String>() // data
    private val subjectList = ArrayList<String>() // 과목
    private val professorList = ArrayList<String>() // 교수
    private val lectureTimeList = ArrayList<String>() // 시간
    private val kjKeyList = ArrayList<String>() // 과목 코드

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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Main) {
                    lmsAuthenticationDialog.catchCurrentStatus(
                        "Authentication",
                        "LMS 학사 정보를 가져오는 중이에요, 잠시만 기다려주세요",
                        MotionToast.TOAST_INFO,
                    )
                }
                // 1. 처음 세션 값을 얻기 위해 통합로그인 페이지에서 cookie 값 GET
                val res1 = doCrawling(
                    url = "https://sso1.mju.ac.kr/login.do?redirect_uri=http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp",
                    cookies = cookies,
                    httpMethod = Connection.Method.GET
                )

                cookies["JSESSIONID"] = res1.cookie("JSESSIONID")

                // 2. 학부생 id, pw POST로 전송
                data["id"] = myId
                data["passwrd"] = myPw

                val res2 = doCrawling(
                    url = "https://sso1.mju.ac.kr/mju/userCheck.do",
                    cookies = cookies,
                    data = data,
                    httpMethod = Connection.Method.POST
                )


                // 3. 데이터 안의 id, pw key값 이름 바꿔주고 redirect_uri 키 추가.
                data.clear()
//            cookies["JSESSIONID"] = res1.cookie("JSESSIONID")
                data["user_id"] = myId
                data["user_pwd"] = myPw
                data["redirect_uri"] = "http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp"


                val res3 = doCrawling(
                    url = "https://sso1.mju.ac.kr/login/ajaxActionLogin2.do",
                    cookies = cookies,
                    data = data,
                    httpMethod = Connection.Method.POST
                )

                cookies["JSESSIONID"] = res3.cookie("JSESSIONID") // 세션 ID가 바뀌므로 교체
                cookies["SCOUTER"] = res3.cookie("SCOUTER") // SCOUTER 쿠키 추가
                cookies["access_token"] = res3.cookie("access_token") // accecss_token 추가
                cookies["refresh_token"] = res3.cookie("refresh_token") // refresh_token 추가


                val res4 = doCrawling(
                    url = "https://lms.mju.ac.kr/ilos/lo/login_bandi_sso.acl",
                    cookies = cookies,
                    data = data,
                    httpMethod = Connection.Method.POST
                )


                // data 값 초기화
                data.clear()
                // 2021학년도 1학기 수강신청한 과목 불러오기
                data["YEAR"] = "2021"
                data["TERM"] = "1"
                data["encoding"] = "utf-8"

                val res5 = doCrawling(
                    url = "https://lms.mju.ac.kr/ilos/mp/course_register_list.acl",
                    cookies = cookies,
                    data = data,
                    httpMethod = Connection.Method.POST
                )


                // 수강 중인 과목 저장
                Jsoup.parse(res5.body()).select("div > div > div > div > a > span")
                    .forEach { subjectList.add(it.text()) }

                // 교수, 시간 임시 저장
                val temp = ArrayList<Element>()
                Jsoup.parse(res5.body()).select("div > div > div > ul > li")
                    .forEach { temp.add(it) }

                for (i in 0 until temp.size) {
                    if (i % 2 == 0) {
                        professorList.add(temp[i].text())
                    } else {
                        lectureTimeList.add(temp[i].text())
                    }
                }


                Log.d("로그", "과목 리스트 = $subjectList")
                Log.d("로그", "교수 리스트 = $professorList")
                Log.d("로그", "시간 리스트 = $lectureTimeList")

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
                    cookies = cookies,
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
                    cookies = cookies,
                    data = data,
                    httpMethod = Connection.Method.POST
                )

                Log.d(TAG, Jsoup.parse(res7.body()).select("table > tbody > tr > td").text())
                withContext(Main) {
                    lmsAuthenticationDialog.catchCurrentStatus(
                        "Authentication",
                        "LMS 학사 정보 동기화 성공!",
                        MotionToast.TOAST_SUCCESS
                    )
                    // dialog 종료
                    lmsAuthenticationDialog.dismiss()

                }
                // SharedPreferences ID, PW 저장
                SharedPrefManager.setUserLmsId(userId = myId)
                SharedPrefManager.setUserLmsPW(userPw = myPw)
                SharedPrefManager.setUserLmsSubjectInfoList(
                    professorList = professorList,
                    subjectCode = kjKeyList,
                    lectureTimeList = lectureTimeList,
                    subjectList = subjectList
                )

            } catch (exception: Exception) {
                withContext(Main) {
                    Log.d(TAG, "error: $exception")
                    lmsAuthenticationDialog.catchCurrentStatus(
                        "Error!",
                        "동기화 과정 중 오류가 발생했습니다!",
                        MotionToast.TOAST_ERROR
                    )
                    lmsAuthenticationDialog.dismiss()
                    // 쉐어드에 저장된 모든 데이터 삭제
                    SharedPrefManager.clearAllData()
                }
            }


        }


    }
}