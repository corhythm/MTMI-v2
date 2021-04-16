package com.example.mtmimyeon_gitmi.mbti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.HomeActivity
import com.example.mtmimyeon_gitmi.databinding.FragmentMbtiTestStartBinding


public class MbtiTestStartFragment private constructor() : Fragment() {
    private var _binding: FragmentMbtiTestStartBinding? = null
    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!

    companion object {
        fun getInstance(): MbtiTestStartFragment {
            return MbtiTestStartFragment()
        }
    }

    // 뷰가 생성되었을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMbtiTestStartBinding.inflate(inflater, container, false)
        binding.buttonTeststartTeststart.setOnClickListener {
            (requireActivity() as HomeActivity).replaceFragment(MbtiTestHomeFragment.getInstance())
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}