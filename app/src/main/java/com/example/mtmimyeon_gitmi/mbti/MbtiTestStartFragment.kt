package com.example.mtmimyeon_gitmi.mbti

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.HomeActivity
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.FragmentMbtiTestStartBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.marozzi.roundbutton.RoundButton


public class MbtiTestStartFragment : Fragment() {
    private var _binding: FragmentMbtiTestStartBinding? = null

    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!

    // 뷰가 생성되었을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMbtiTestStartBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        if (SharedPrefManager.getMyMbtiType() != "") { // 이미 MBTI 테스트를 한 번이라도 진행했으면
            binding.buttonTestStartGoToTestStart.text = "MBTI 재검사하기"
            binding.buttonTestStartShowMyMbtiResult.visibility = View.VISIBLE
        }

        binding.buttonTestStartGoToTestStart.setOnClickListener {
//            binding.buttonTestStartGoToTestStart.startAnimation()
//            Handler().postDelayed({ // delay 후 실행할 코드
//                binding.buttonTestStartGoToTestStart.stopAnimation()
//                binding.buttonTestStartGoToTestStart.setResultState(RoundButton.ResultState.SUCCESS)
//                binding.buttonTestStartGoToTestStart.revertAnimation()
//
//                Intent(requireContext(), MbtiTestQuestionActivity::class.java).also {
//                    requireActivity().startActivity(it)
//                }
//                requireActivity().overridePendingTransition(
//                    R.anim.activity_slide_in,
//                    R.anim.activity_slide_out
//                )
//            }, 1000)

            Intent(requireContext(), MbtiTestQuestionActivity::class.java).also {
                requireActivity().startActivity(it)
            }
            requireActivity().overridePendingTransition(
                R.anim.activity_slide_in,
                R.anim.activity_slide_out
            )
        }

        binding.buttonTestStartShowMyMbtiResult.setOnClickListener {
            Intent(requireContext(), MbtiResultActivity::class.java).also {
                requireActivity().startActivity(it)
            }
            requireActivity().overridePendingTransition(
                R.anim.activity_slide_in,
                R.anim.activity_slide_out
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        init()
        super.onResume()
    }
}