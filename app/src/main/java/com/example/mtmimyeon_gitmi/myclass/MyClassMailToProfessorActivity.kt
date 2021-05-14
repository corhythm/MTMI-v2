package com.example.mtmimyeon_gitmi.myClass

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassMailToProfessorBinding
import com.marozzi.roundbutton.RoundButton

class MyClassMailToProfessorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassMailToProfessorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassMailToProfessorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() { // 자세한 사용법: https://github.com/Chivorns/SmartMaterialSpinner 참조

        binding.buttonMyClassMailToProfSend.setOnClickListener {
            binding.buttonMyClassMailToProfSend.startAnimation()
            Handler().postDelayed({ // delay 후 실행할 코드
                binding.buttonMyClassMailToProfSend.stopAnimation()
                binding.buttonMyClassMailToProfSend.setResultState(RoundButton.ResultState.SUCCESS)
                binding.buttonMyClassMailToProfSend.revertAnimation()

                // email 보내기(이메일 앱이 깔려 있으면 관련된 앱으로 이동)
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    // The intent does not have a URI, so declare the "text/plain" MIME type
                    type = "text/html"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("professor@mju.ac.kr")) // receiver
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        binding.editTextMyClassMailToProfMailTitle.text.toString()
                    ) // title
                    putExtra(
                        Intent.EXTRA_TEXT,
                        binding.editTextMyClassMailToProfMailContent.text.toString()
                    ) // content
                    // putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment")) // 첨부 파일
                    // You can also attach multiple items by passing an ArrayList of Uris
                })
            }, 1000)
        }

        binding.buttonGroupMyClassMailToProfGroup.setOnSelectListener {
            // 버튼 클릭할 때마다 들어갈 텍스트, 나중에 DB에서 가져오는 걸로 바꿔야 함
            var mailTitle = ""
            var mailContent = ""

            when (it) {
                binding.buttonMyClassMailToProfHomework -> {
                    mailTitle = "과제 관련해서 문의드립니다 - 컴퓨터공학과 60172121 OOO"
                    mailContent = "안녕하세요, OOO교수님," +
                            "저는 컴퓨터공학과 수강생 컴퓨터공학과 17학번 OOO입니다." +
                            "이번 주 과제 주신 거 중에 getchar를 사용하라고 하신 게 잘 이해가 안 됩니다." +
                            "getchar는 그럴 때 사용하는 게 아니지 않나요?"
                }
                binding.buttonMyClassMailToProfReinforcement -> {
                    mailTitle = "안녕하세요 컴퓨터공학과 OOO입니다. 증원 관련해서 문의드릴 게 있습니다."
                    mailContent = "안녕하세요 교수님, 환절기에 건강 유의하시고 계신가요? 저는 금요일 10:00 ~ 13:00 운영체제 B반을 수강을 희망하는" +
                            "17학번 OOO입니다. 제가 이번에 교수님께서 개강하신 운영체제론을 꼭 듣고 싶은데 아쉽게도 수강신청에 성공하시 못 했습니다. 혹시" +
                            "실례가 안 된다면 증원을 요청드려도 될까요?"
                }
                binding.buttonMyClassMailToProfQuestion -> {
                    mailTitle = "[컴퓨터공학과 60172121 OOO] 수업 내용 관련 문의드립니다."
                    mailContent = "안녕하세여 OOO 교수님! 저는 시스템클라우드보안을 수강하고 있는 컴퓨터공학과 17학번 OOO입니다." +
                            "궁금한 것이 있는 수업시간 전후에 여쭙기에는 내용이 많습니다. 가능하다면 30분에서 1시간 정도 교수님 " +
                            "연구실을 방문해서 여쭙고 싶은데 혹시 가능하신지요? 저는 월/수 10:00 ~ 13:00 시간에 가능합니다. 가능하다면" +
                            "제가 교수님 시간에 맞추겠습니다. 바쁘신 와중에 시간 내주셔서 감사합니다. 날이 추운데 감기 조심하세요/" +
                            "OOO 드림"
                }
                binding.buttonMyClassMailToProfAttandance -> {
                    mailTitle = "출석 관련해서 문의드립니다 - 컴퓨터공학과 60172121 OOO"
                    mailContent = "안녕하세요, OOO교수님," +
                            "저는 컴퓨터공학과 수강생 컴퓨터공학과 17학번 OOO입니다." +
                            "교수님의 열정 가득한 가르침 덕분에, 저의 학문적 호기심을 기를 수 있었습니다. 항상 감사드립니다." +
                            "다름이 아니라 이번에 학생회 임원이 되어 대동제 준비를 하게 되었는데," +
                            "행사 준비 관계로 4월 23일 2시 강의 참석이 제한될 거 같아서 메일 드립니다." +
                            "학교 행사이기 때문에 학과 사무실에서 혀조증과 참여 확인서는 발급된다고 하는데" +
                            "혹시 해당 서류를 제출하면 출석 인정이 가능한 건지 문의드립니다" +
                            "혹시 필요하실까봐 제 번호를 남깁니다. 제 번호는 010 0000 0000 입니다." +
                            "그럼 확인 부탁드리겠습니다! 감사합니다."
                }
                binding.buttonMyClassMailToProfTest -> {
                    mailTitle = "출석 관련해서 문의드립니다 - 컴퓨터공학과 60172121 OOO"
                    mailContent = "안녕하세요, OOO교수님," +
                            "저는 컴퓨터공학과 수강생 컴퓨터공학과 17학번 OOO입니다." +
                            "교수님의 수업을 통해 컴퓨터공학에 대한 저의 생각과 지식을 발전시키는 좋은 기회를 얻었습니다" +
                            "다름이 아니라, 이번 학기 컴퓨터구조론 성적이 제가 예상했던 것보다 다소 낮은 점수인 것 같습니다." +
                            "바쁘시겠지만 제가 어떤 부분이 부족했는지 알려주실 수 있을까요?" +
                            "부족한 부분을 학업에 참고하여 보안하고 싶습니다. 바쁘신 와중에 시간 내주셔서 감사합니다. 날이 추운데" +
                            "감기 조심하세요. OOO 드림."
                }
                binding.buttonMyClassMailToProfGrade -> {
                    mailTitle = "안녕하세요 17학번 컴퓨터공학과 OOO입니다. 성적과 관련해서 궁금한 사항이 있습니다."
                    mailContent = "안녕하세요, OOO교수님," +
                            "저는 컴퓨터공학과 수강생 컴퓨터공학과 17학번 OOO입니다." +
                            "교수님의 수업을 통해 컴퓨터공학에 대한 저의 생각과 지식을 발전시키는 좋은 기회를 얻었습니다" +
                            "다름이 아니라, 이번 학기 컴퓨터구조론 성적이 제가 예상했던 것보다 다소 낮은 점수인 것 같습니다." +
                            "바쁘시겠지만 제가 어떤 부분이 부족했는지 알려주실 수 있을까요?" +
                            "부족한 부분을 학업에 참고하여 보안하고 싶습니다. 바쁘신 와중에 시간 내주셔서 감사합니다. 날이 추운데" +
                            "감기 조심하세요. OOO 드림."
                }
            }
            binding.editTextMyClassMailToProfMailTitle.setText(mailTitle)
            binding.editTextMyClassMailToProfMailContent.setText(mailContent)
        }

        val professorList = ArrayList<String>()
        professorList.add("류연승 교수님")
        professorList.add("장혁수 교수님")
        professorList.add("조세형 교수님")
        professorList.add("조민경 교수님")
        professorList.add("김직수 교수님")
        professorList.add("박현민 교수님")
        professorList.add("김상운 교수님")
        professorList.add("김상귀 교수님")
        professorList.add("안희철 교수님")
        professorList.add("신민호 교수님")
        professorList.add("이충기 교수님")
        professorList.add("장희점 교수님")
        professorList.add("권동섭 교수님")
        professorList.add("김상균 교수님")
        professorList.add("윤병주 교수님")
        professorList.add("이강선 교수님")
        professorList.add("이명호 교수님")

        binding.spinnerMyClassMailToProfSelectMail.setItem(professorList)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }


}