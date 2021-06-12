package com.mju.mtmi.myClass

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mju.mtmi.R
import com.mju.mtmi.crawling.LmsAuthenticationDialog
import com.mju.mtmi.crawling.ObserveCrawlingInterface
import com.mju.mtmi.databinding.FragmentMyClassMainBinding
import com.mju.mtmi.util.SharedPrefManager
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment

class MyClassMainFragment : Fragment(), ObserveCrawlingInterface {
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

        Log.d("temp", "MyClassMainFragment -onCreateView() called")
        init()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun init() {

        // 강의별 게시판 액티비티 이동
        binding.textViewMyClassMainSubjectList.setOnClickListener {
            // 로컬에 저장된 LMS 계정 정보가 있을 떄
            if (SharedPrefManager.getUserLmsId().isNotEmpty() && SharedPrefManager.getUserLmsPw()
                    .isNotEmpty()
            ) {
                // 강의별 게시판으로 이동
                Intent(requireContext(), MyClassSubjectListActivity::class.java).also {
                    startActivity(it)
                    requireActivity().overridePendingTransition(
                        R.anim.activity_slide_in,
                        R.anim.activity_slide_out
                    )
                }
            } else {
                val lmsAuthenticationDialog =
                    LmsAuthenticationDialog(
                        requireContext(),
                        MyClassSubjectListActivity::class.java
                    )
                lmsAuthenticationDialog.show()
            }
        }

        // 시간표 & 과제 액티비티 이동
        binding.textViewMyClassMainTimetable.setOnClickListener {
            if (SharedPrefManager.getUserLmsId().isNotEmpty() && SharedPrefManager.getUserLmsPw()
                    .isNotEmpty()
            ) {
//                 시간표, 과제함으로 이동
                Intent(requireContext(), MyClassTimetableActivity::class.java).also {
                    startActivity(it)
                    requireActivity().overridePendingTransition(
                        R.anim.activity_slide_in,
                        R.anim.activity_slide_out
                    )
                }

            } else {
                val lmsAuthenticationDialog =
                    LmsAuthenticationDialog(requireContext(), MyClassTimetableActivity::class.java)
                lmsAuthenticationDialog.show()
            }

        }

        // 교슈님 요청 메일(가이드) 액티비티 이동
        binding.textViewMyClassMainProfessorToMail.setOnClickListener {
            Intent(requireContext(), MyClassMailToProfessorActivity::class.java).also {
                startActivity(it)
                requireActivity().overridePendingTransition(
                    R.anim.activity_slide_in,
                    R.anim.activity_slide_out
                )
            }
        }

        // lms 계정 해지 버튼 클릭
        binding.imageViewMyClassMainRevokeAccount.setOnClickListener {
            if (SharedPrefManager.getUserLmsId() != "") {
                val mDialog = BottomSheetMaterialDialog.Builder(requireActivity())
                    .setTitle("Delete your account?")
                    .setAnimation("question2.json")
                    .setMessage(
                        "등록된 LMS 계정을 삭제하시겠어요?",
                        TextAlignment.CENTER
                    )
                    .setPositiveButton("Yes") { dialogInterface, _ ->
                        SharedPrefManager.clearAllLmsUserData()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
                    .build();

                // Show Dialog
                mDialog.show();
            }
        }
    }

    override suspend fun isCrawlingFinished(activityType: Class<out Activity>, isSuccess: Boolean) {
        if (isSuccess) {
            Intent(requireContext(), activityType).also {
                startActivity(it)
                requireActivity().overridePendingTransition(
                    R.anim.activity_slide_in,
                    R.anim.activity_slide_out
                )
            }
        }
    }
}