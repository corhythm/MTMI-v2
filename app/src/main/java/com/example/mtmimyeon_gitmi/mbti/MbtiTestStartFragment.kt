package com.example.mtmimyeon_gitmi.mbti

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.HomeActivity
import com.example.mtmimyeon_gitmi.databinding.FragmentMbtiTestStartBinding
import com.marozzi.roundbutton.RoundButton


public class MbtiTestStartFragment: Fragment() {
    private var _binding: FragmentMbtiTestStartBinding? = null

    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!

//    companion object {
//        private var INSTANCE: MbtiTestStartFragment? = null
//        fun getInstance(): MbtiTestStartFragment {
//            if (INSTANCE == null) INSTANCE = MbtiTestStartFragment()
//            return INSTANCE!!
//        }
//    }

    // 뷰가 생성되었을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMbtiTestStartBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun init() {
        binding.buttonTestStartGoToTestStart.setOnClickListener {
            binding.buttonTestStartGoToTestStart.startAnimation()
            Handler().postDelayed({ // delay 후 실행할 코드
                binding.buttonTestStartGoToTestStart.stopAnimation()
                binding.buttonTestStartGoToTestStart.setResultState(RoundButton.ResultState.SUCCESS)
                binding.buttonTestStartGoToTestStart.revertAnimation()
                (requireActivity() as HomeActivity).replaceFragment(MbtiTestHomeFragment())
            }, 1500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}