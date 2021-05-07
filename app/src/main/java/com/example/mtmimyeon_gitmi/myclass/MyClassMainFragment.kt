package com.example.mtmimyeon_gitmi.myClass

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.databinding.FragmentMyClassMainBinding

class MyClassMainFragment : Fragment() {
    private var _binding: FragmentMyClassMainBinding? = null

    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!

//    companion object {
//        private var INSTANCE: MyClassMainFragment? = null
//        fun getInstance(): MyClassMainFragment {
//            Log.d("로그", "MyClassMainFragment -getInstance() called / $INSTANCE")
//            if (INSTANCE == null) {
//                INSTANCE = MyClassMainFragment() // 프래그먼트 인스턴스 없을 떄, 인스턴스 생성
//            }
//            return INSTANCE!!
//        }
//    }

    // 뷰가 생성되었을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //return super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMyClassMainBinding.inflate(inflater, container, false)

        init()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun init() {

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("userId", 0)

        // 강의별 게시판
        binding.textViewMyClassMainSubjectList.setOnClickListener {

            startActivity(
                Intent(context, MyClassSubjectListActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle()
            )
        }

        // 시간표/과제
        binding.textViewMyClassMainTimetable.setOnClickListener {
            startActivity(
                Intent(context, MyClassTimeTableActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle()
            )
        }

        // 교슈님 요청 메일(가이드)
        binding.textViewMyClassMainProfessorToMail.setOnClickListener {
            startActivity(
                Intent(context, MyClassMailToProfessorActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle()
            )
        }
    }
}