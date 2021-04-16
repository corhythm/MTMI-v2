package com.example.mtmimyeon_gitmi.myclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.FragmentMyclassMailToProfessorBinding

class MyClassMailToProfessorFragment private constructor(): Fragment() {
    private var _binding: FragmentMyclassMailToProfessorBinding? = null
    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!
    private lateinit var professorList: MutableList<String>
//    private lateinit var spinner: SmartMaterialSpinner<String>

    companion object {
        fun getInstance(): MyClassMailToProfessorFragment {
            return MyClassMailToProfessorFragment()
        }
    }
    // 뷰가 생성되었을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMyclassMailToProfessorBinding.inflate(inflater, container, false)

        initSpinner()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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


        binding.spinnerMyClassMailToProf.setItem(professorList)
    }


}