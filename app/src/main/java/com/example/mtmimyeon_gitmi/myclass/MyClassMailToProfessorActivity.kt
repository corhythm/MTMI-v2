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

    companion object {
        fun getInstance(): MyClassMailToProfessorActivity {
            return MyClassMailToProfessorActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassMailToProfessorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        //binding.textViewMyClassMailToProfContent.movementMethod = ScrollingMovementMethod()
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
                    putExtra(Intent.EXTRA_SUBJECT, "Email subject") // title
                    putExtra(
                        Intent.EXTRA_TEXT,
                        binding.textViewMyClassMailToProfContent.text
                    ) // content
                    // putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment")) // 첨부 파일
                    // You can also attach multiple items by passing an ArrayList of Uris
                })
            }, 1500)
        }

        binding.buttonGroupMyClassMailToProfGroup.setOnSelectListener {
            // 버튼 클릭할 때마다 들어갈 텍스트, 나중에 DB에서 가져오는 걸로 바꿔야 함
            when (it) {
                binding.buttonMyClassMailToProfHomework -> {
                    binding.textViewMyClassMailToProfContent.text = "교수님 Homework 7.1이 문제가 잘 이해가 안 돼서 질문드립니다. 이렇게 푸는 거 맞나요?"
                }
                binding.buttonMyClassMailToProfReinforcement -> {
                    binding.textViewMyClassMailToProfContent.text = "교수님, 안녕하세요? 저는 60172121 컴퓨터공학과 재학 중인 김아무개입니다. 다름이 아니라..."
                }
                binding.buttonMyClassMailToProfQuestion -> {
                    binding.textViewMyClassMailToProfContent.text = "교수님, 이번 학기 강의 전부 저번 학기 재탕하시는 거 아닌가요? 사용하는 라이브러리도 전부 deprecated 됐다고" +
                            "하고 뭔가 냄새가 납니다만..."
                }
                binding.buttonMyClassMailToProfAttandance -> {
                    binding.textViewMyClassMailToProfContent.text = "교수님 출석관련해서 궁금한 점이 있습니다.."
                }
                binding.buttonMyClassMailToProfTest -> {
                    binding.textViewMyClassMailToProfContent.text = "교수님 이번 시험 문제에 오류가 있는 거 같습니다. 그건 그렇고 시험 난이도 너무 한 거 아닌가요? 사과부탁드립니다."
                }
                binding.buttonMyClassMailToProfGrade -> {
                    binding.textViewMyClassMailToProfContent.text = "교수님 성적 관련해서 이의 신청은 언제부터 가능한 건가요? 아무래도 제 학점이 말이 안 돼서요... 잘못된 거 같습니다"
                }
            }
        }

        val professorList = ArrayList<String>()
        professorList.add("강성욱")
        professorList.add("최정현")
        professorList.add("김민형")
        professorList.add("베르바토프")
        professorList.add("김재정")
        professorList.add("웨인 루니")
        professorList.add("코로나")
        professorList.add("데이비드 베컴")
        professorList.add("게슈틸")
        professorList.add("시진핑")
        professorList.add("오바마")
        professorList.add("도날드 도람푸")

        binding.spinnerMyClassMailToProfSelectMail.setItem(professorList)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }


}