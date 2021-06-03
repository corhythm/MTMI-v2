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
                binding.buttonMyClassMailToProfHomework -> { // 과제
                    mailTitle = "[명지대/과제]  OOO수업 과제 관련해서 문의드립니다 60****** OOO"
                    mailContent = "교수님 안녕하세요?\n교수님의 N시 OO요일 OOO수업을 듣는 OOO학과 OOO입니다." +
                            "\n다름이 아니라 (물어보고 싶은 내용)" +
                            "ex) ~ 로 방향을 설정하였습니다. " +
                            "혹시 이 부분이 주관적인 방향이진 않을까 우려가 되어 문의 드립니다. " +
                            "바쁘시겠지만 확인 후, 답변 부탁드리면 감사하겠습니다. OOO 드림."
                }
                binding.buttonMyClassMailToProfReinforcement -> { // 증원
                    mailTitle = "[명지대/증원]  OOO수업 증원 관련해서 문의드립니다 60****** OOO"
                    mailContent =
                        "교수님 안녕하세요? 교수님의 N시 OO요일 OOO과목 수강을 희망하는 OOO학과 60******학번 OOO입니다." +
                                "다름이 아니라 OOO과목 수업과 관련하여 수강인원 증원이 가능할지 여쭙고 싶어 메일 드립니다." +
                                "~과목을 수강함으로써 관련 분야에 대한 전문성을 키우고자 합니다. " +
                                "이를 위해 수강신청 기간에 수업을 신청하려 노력하였지만 경쟁률이 높아 안타깝게 실패하여 이렇게 증원 예정이 있으신지 여쭙게 되었습니다." +
                                "해당 과목을 절실히 수강하고자 하는데 기회를 주시면 정말 감사드리겠습니다. 바쁘신 와중에 메일 읽어주셔서 감사합니다." +
                                "행복한 하루 보내세요. 바쁘시겠지만 확인 후, 답변해주시면 감사하겠습니다. OOO드림"
                }
                binding.buttonMyClassMailToProfQuestion -> { // 면담 요청
                    mailTitle = "[면담요청이유 (간단히)] ㅇㅇ대학교 OOO과 학사 OOO입니다. "
                    mailContent = "안녕하세요, ㅇㅇㅇ교수님 ㅇㅇ대학교 OOO과 OOO입니다. 오늘도 좋은하루 보내고 계시나요? " +
                            "다름이 아니라 (면담을 하여는 이유를 작성하여 주세요)" +
                            "* 대학원 진학의 경우 학업사항, 경력사항 등을 작성해 주세요. " +
                            "교수님께서 시간을 내주신다면 잠시라도 찾아 뵙고 상담을 통해 도움을 얻고싶은데 괜찮으신지 여쭙고싶어 메일 드립니다." +
                            " 메일 읽어주셔서 감사 드리며, 답변 기다리겠습니다. OOO 드림"
                }
                binding.buttonMyClassMailToProfAttandance -> { // 출석
                    mailTitle = "[명지대/출석]  OOO수업 출석 관련해서 문의드립니다 60****** OOO"
                    mailContent =
                        "교수님 안녕하세요? 교수님의 N시 OO요일 OOO수업을 듣는 OOO학과 OOO입니다. 다름이 아니라, O/O에 OOOO로 인해 " +
                                "수업에 참석하지 못할 것 같습니다. 다른 날짜로 조정이 불가피한 상황이라, 출석 인정 받을 수 있는 다른 방법이 있을 지 문의 드립니다." +
                                "출석인정이 가능하다면 관련 서류를 발급받을 수 있는지 확인해보고자 합니다. 확인 부탁드립니다! 감사합니다. OOO 드림."
                }
                binding.buttonMyClassMailToProfGrade -> { // 성적
                    mailTitle = "[명지대/성적]  OOO수업 성적 관련하여 문의드립니다 60****** OOO"
                    mailContent = "교수님 안녕하세요? 교수님의 N시 OO요일 OOO수업을 듣는 OOO학과 OOO입니다. " +
                            "다름이 아니라 이번학기 수업 성적이 제가 예상한 것보다 낮아 어떤 부분이 부족했는지 알고 싶어 메일 드렸습니다." +
                            "(물어보고 싶은 부분 작성) ex (중간/기말)시험 점수는 확인을 하였고 레포트나 과제 부분에서 점수가 많이 깎인 것 같습니다." +
                            "부족한 부분을 알려주시면 앞으로 보안하고 싶습니다! 바쁘신 와중에 메일 확인해주셔서 감사합니다. 한 학기동안 좋은 수업해 주셔서 많이 배웠습니다." +
                            "다시 한번 감사 드립니다 :) OOO 드림."
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

        // 처음에 default click
        binding.buttonGroupMyClassMailToProfGroup.selectButton(binding.buttonMyClassMailToProfHomework)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }


}