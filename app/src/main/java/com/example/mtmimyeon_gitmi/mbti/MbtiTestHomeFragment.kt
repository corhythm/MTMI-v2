package com.example.mtmimyeon_gitmi.mbti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.databinding.FragmentMbtiTestHomeBinding

class MbtiTestHomeFragment private constructor(): Fragment() {
    private var _binding: FragmentMbtiTestHomeBinding? = null
    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!

    companion object {
        private var INSTANCE: MbtiTestHomeFragment? = null
        fun getInstance(): MbtiTestHomeFragment {
            if(INSTANCE == null) INSTANCE = MbtiTestHomeFragment()
            return INSTANCE!!
        }
    }
    // 뷰가 생성되었을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMbtiTestHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}