package com.example.mtmimyeon_gitmi.myclass

import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityMyclassMailToProfessorBinding
import com.marozzi.roundbutton.RoundButton

class MyClassMailToProfessorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyclassMailToProfessorBinding

    companion object {
        fun getInstance(): MyClassMailToProfessorActivity {
            return MyClassMailToProfessorActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyclassMailToProfessorBinding.inflate(layoutInflater)
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
            }, 1500)
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


}