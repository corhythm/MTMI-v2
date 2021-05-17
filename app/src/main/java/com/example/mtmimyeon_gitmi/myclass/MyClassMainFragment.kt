package com.example.mtmimyeon_gitmi.myClass

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.crawling.LmsAuthenticationDialog
import com.example.mtmimyeon_gitmi.databinding.FragmentMyClassMainBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager

class MyClassMainFragment : Fragment() {
    private var _binding: FragmentMyClassMainBinding? = null

    // This property is only valid between onCreateView and OnDestroyView
    private val binding get() = _binding!!

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
        // 강의별 게시판
        binding.textViewMyClassMainSubjectList.setOnClickListener {
            // 로컬에 저장된 LMS 계정 정보가 있을 떄
            if (SharedPrefManager.getUserLmsId().isNotEmpty() && SharedPrefManager.getUserLmsPw()
                    .isNotEmpty()
            ) {
                // 강의별 게시판으로 이동
                Intent(context, MyClassSubjectListActivity::class.java).also {
                    startActivity(it)
                    requireActivity().overridePendingTransition(
                        R.anim.activity_slide_in,
                        R.anim.activity_slide_out
                    )
                }
            } else {
                val lmsAuthenticationDialog = LmsAuthenticationDialog(requireContext())
                lmsAuthenticationDialog.show()
            }
        }

        // 시간표/과제
        binding.textViewMyClassMainTimetable.setOnClickListener {
            if (SharedPrefManager.getUserLmsId().isNotEmpty() && SharedPrefManager.getUserLmsPw()
                    .isNotEmpty()
            ) {
                // 시간표, 과제함으로 이동
                Intent(context, MyClassTimetableActivity::class.java).also {
                    startActivity(it)
                    requireActivity().overridePendingTransition(
                        R.anim.activity_slide_in,
                        R.anim.activity_slide_out
                    )
                }
            } else {
                val lmsAuthenticationDialog = LmsAuthenticationDialog(requireContext())
                lmsAuthenticationDialog.show()
            }
        }

        // 교슈님 요청 메일(가이드)
        binding.textViewMyClassMainProfessorToMail.setOnClickListener {

            Intent(context, MyClassMailToProfessorActivity::class.java).also {
                startActivity(it)
                requireActivity().overridePendingTransition(
                    R.anim.activity_slide_in,
                    R.anim.activity_slide_out
                )
            }
        }

        // lms 계정 해지
        binding.imageViewMyClassMainRevokeAccount.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle("저장된 LMS 계정을 삭제하시겠습니까?")
            dialogBuilder.setIcon(R.drawable.ic_warning)
            dialogBuilder.setPositiveButton("Yes") { _, _ -> // 사용하지 않는 매개변수 _ 처리
                SharedPrefManager.clearAllData()
            }
            dialogBuilder.setNegativeButton("No") { _, _ ->
                // Nothing
            }
            dialogBuilder.show()
        }
    }

}