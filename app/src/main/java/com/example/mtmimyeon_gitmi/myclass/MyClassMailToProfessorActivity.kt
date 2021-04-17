package com.example.mtmimyeon_gitmi.myclass

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityMyclassMailToProfessorBinding

class MyClassMailToProfessorActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMyclassMailToProfessorBinding
    // This property is only valid between onCreateView and OnDestroyView
    private lateinit var professorList: MutableList<String>

    companion object {
        fun getInstance(): MyClassMailToProfessorActivity {
            return MyClassMailToProfessorActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyclassMailToProfessorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSpinner()

        //binding.textViewMyClassMailToProfContent.movementMethod = ScrollingMovementMethod()
    }


    private fun initSpinner() { // 자세한 사용법: https://github.com/Chivorns/SmartMaterialSpinner 참조
        professorList = ArrayList()
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