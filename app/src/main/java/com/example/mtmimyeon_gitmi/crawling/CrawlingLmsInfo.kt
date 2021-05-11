package com.example.mtmimyeon_gitmi.crawling

import android.util.Log
import com.example.mtmimyeon_gitmi.item.Homework
import com.example.mtmimyeon_gitmi.item.ItemSubjectInfo
import com.example.mtmimyeon_gitmi.util.AES128
import com.example.mtmimyeon_gitmi.util.Secret
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


class CrawlingLmsInfo(
    private val myId: String,
    private val myPw: String,
    private val lmsAuthenticationDialog: LmsAuthenticationDialog
) {
    private val TAG = "로그"
    private val cookies = HashMap<String, String>() // cookies value
    private val data = HashMap<String, String>() // data
    private val subjectInfoList = ArrayList<ItemSubjectInfo>()

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
        Log.d(TAG, "myId = $myId, myPW = $myPw")
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
                    cookies = cookies, httpMethod = Connection.Method.GET
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
                data["user_id"] = myId
                data["user_pwd"] = myPw
                data["redirect_uri"] = "http://lms.mju.ac.kr/ilos/bandi/sso/index.jsp"

                val res3 = doCrawling(
                    url = "https://sso1.mju.ac.kr/login/ajaxActionLogin2.do",
                    cookies = cookies,
                    data = data,
                    httpMethod = Connection.Method.POST
                )


                // 4. 여기서 세션 id 값이 바뀌므로 그전에 사용하던 거 사용하면 안 됨
                // 세션 값 교체
                // SCOUTER, access_token, refresh_token 쿠키 값 추가
                cookies["JSESSIONID"] = res3.cookie("JSESSIONID")
                cookies["SCOUTER"] = res3.cookie("SCOUTER")
                cookies["access_token"] = res3.cookie("access_token")
                cookies["refresh_token"] = res3.cookie("refresh_token")

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
                Jsoup.parse(res5.body()).select("div > div > div > div > a > span").forEach {
                    subjectInfoList.add(ItemSubjectInfo(subjectName = it.text()))
                }


                // 교수, 시간 임시 저장
                val tempProfAndTime = ArrayList<Element>()
                Jsoup.parse(res5.body()).select("div > div > div > ul > li")
                    .forEach { tempProfAndTime.add(it) }

                // 교수, 시간 저장
                println("tempProfAndTime.size = ${tempProfAndTime.size}")
                for (i in 0 until tempProfAndTime.size) {
                    if (i % 2 == 0) {
                        subjectInfoList[i / 2].professorName = tempProfAndTime[i].text()
                    } else {
                        subjectInfoList[i / 2].lectureTime = tempProfAndTime[i].text()
                    }
                }


                // # 과목 코드 >> ['eclassRoom(', 'A20203KMA021010012', '); return false;']
                val kjKeyHtml = Jsoup.parse(res5.body()).select("div > div > div > a")
                println(kjKeyHtml)


                // 과목 코드 저장
                for (i in 0 until subjectInfoList.size) {
                    subjectInfoList[i].subjectCode =
                        kjKeyHtml[i].attributes()["onclick"].split("'")[1]
                    println(subjectInfoList[i]) // 확인용
                }

                cookies["_language_"] = "ko"

                data.clear()
                data["KJKEY"] = subjectInfoList[3].subjectCode // temp
                data["returnURI"] = "/ilos/st/course/submain_form.acl"
                data["encoding"] = "utf-8"

                // 과목별 과제 저장
                subjectInfoList.forEach { it ->
                    cookies["_language_"] = "ko"

                    data.clear()
                    data["KJKEY"] = it.subjectCode // temp
                    data["returnURI"] = "/ilos/st/course/submain_form.acl"
                    data["encoding"] = "utf-8"

                    val res6 = doCrawling(
                        url = "https://lms.mju.ac.kr/ilos/st/course/eclass_room2.acl",
                        cookies = cookies,
                        data = data,
                        httpMethod = Connection.Method.POST
                    )

                    data.clear()
                    data["start"] = ""
                    data["display"] = "1"
                    data["SCH_VALUE"] = ""
                    data["ud"] = myId
                    data["ky"] = it.subjectCode
                    data["encoding"] = "utf-8"

                    val res7 = doCrawling(
                        url = "https://lms.mju.ac.kr/ilos/st/course/report_list.acl",
                        cookies = cookies,
                        data = data,
                        httpMethod = Connection.Method.POST
                    )

                    // 번호, 제목, 진행, 점수, 배점, 마감기한 가져오기(과제 제출여부는 별도로 태그가 다르므로 별도로 가져와야 함)
                    val tempHomework =
                        Jsoup.parse(res7.body()).select("table > tbody > tr > td").eachText()
                    println(tempHomework.size)

                    // 과제 제출 여부 저장
                    Jsoup.parse(res7.body()).select("table > tbody > tr > td > img")
                        .forEach { isSubmittedText ->
//                println(isSubmittedText.attr("title").toString())
                            it.homeworkList.add(
                                Homework(isSubmitted = isSubmittedText.attr("title").toString())
                            )
                        }

                    if (tempHomework.size > 1) { // 과제함에 과제가 한 개라도 있으면
                        for (i in 0 until tempHomework.size step 6) { // 나머지 정보(순서, 제목, 진행여부, 점수, 배점, 마감 기한) 입력
                            it.homeworkList[i / 6].order = tempHomework[i]
                            it.homeworkList[i / 6].title = tempHomework[i + 1]
                            it.homeworkList[i / 6].onGoing = tempHomework[i + 2]
                            it.homeworkList[i / 6].myScore = tempHomework[i + 3]
                            it.homeworkList[i / 6].totalScore = tempHomework[i + 4]
                            it.homeworkList[i / 6].deadline = tempHomework[i + 5]
                        }
                    }

                    // 출석율 받아오기
                    data.clear()
                    data["ud"] = myId
                    data["ky"] = it.subjectCode
                    data["encoding"] = "utf-8"

                    val res8 = doCrawling(
                        url = "https://lms.mju.ac.kr/ilos/st/course/attendance_list.acl",
                        cookies = cookies,
                        data = data,
                        httpMethod = Connection.Method.POST
                    )


                    it.nowAttendanceRate =
                        Jsoup.parse(res8.body()).select("div > div > span > span")[0].text()
                    it.totalAttendanceRate =
                        Jsoup.parse(res8.body()).select("div > div > span > span")[1].text()

                    Log.d(TAG, it.toString())
                    Log.d(TAG, "------------------------------------------------------------------------------------------")
                }

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
                SharedPrefManager.setUserLmsSubjectInfoList(subjectInfoList)

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