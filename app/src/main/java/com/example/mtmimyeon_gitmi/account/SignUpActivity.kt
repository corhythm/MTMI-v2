package com.example.mtmimyeon_gitmi.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivitySignUpBinding
import com.royrodriguez.transitionbutton.TransitionButton
import www.sanju.motiontoast.MotionToast
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    var db = DatabaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val majorList = resources.getStringArray(R.array.major).toMutableList()
        binding.spinnerSignUpMajorList.setItem(majorList)

        // 비밀번호, 비밀번호 확인 체크
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) { // 텍스트 변경 감지
                if (binding.editTextSignUpPw.text.toString() == binding.editTextSignUpConfirmPw.text.toString()) { // 비밀번호가 일치할 때
                    binding.editTextSignUpConfirmPw.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        ContextCompat.getDrawable(this@SignUpActivity,
                            R.drawable.drawable_end_pw_check),
                        null)
                } else { // 비밀번호가 일치하지 않을 때
                    binding.editTextSignUpConfirmPw.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        ContextCompat.getDrawable(this@SignUpActivity, R.drawable.drawable_end_x),
                        null)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // 비밀번호, 비밀번호 체크 EditTextView 리스너 등록
        binding.editTextSignUpPw.addTextChangedListener(textWatcher)
        binding.editTextSignUpConfirmPw.addTextChangedListener(textWatcher)


        // 회원가입 버튼 클릭 시
        binding.buttonSignUpGoToSignUp.setOnClickListener {
            binding.buttonSignUpGoToSignUp.startAnimation()

            // Do your networking task or background work here.
            Handler().postDelayed({

                var isSuccessful: Boolean

                if (checkValidateSignUp()) { // 기입한 회원정보가 모두 유효하면 회원가입 진행
                    //유저가 기입한 내용
                    val signUpUserId = binding.editTextSignUpId.text.toString()
                    val signUpUserPw = binding.editTextSignUpPw.text.toString()
                    val signUpUserStudentId = binding.editTextSignUpStudentId.text.toString()
                    val signUpUserBirth = binding.editTextSignUpDateOfBirth.text.toString()
                    val signUpUserName = binding.editTextSignUpName.text.toString()
                    val signUpUserMajor = binding.spinnerSignUpMajorList.selectedItem.toString()
                    var signUpUserGender = "남성" // 젠더 초기값
                    binding.radioButtonSignUpGenderGroup.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.radioButton_signUp_man -> {
                                signUpUserGender = "남성"
                            }
                            R.id.radioButton_signUp_woman -> {
                                signUpUserGender = "여성"
                            }
                        }
                    }

                    // interface로 callback 처리함 일단 임시방편용 이후에 문제가 생길 경우 다른코드 대안 찾을 것.
                    db.createEmail(
                        id = signUpUserId,
                        pw = signUpUserPw,
                        name = signUpUserName,
                        studentId = signUpUserStudentId,
                        major = signUpUserMajor,
                        birth = signUpUserBirth,
                        gender = signUpUserGender,
                        context = this,
                        object : Callback<Boolean> {
                            override fun onCallback(data: Boolean) {
                                isSuccessful = data
                                Log.d("callback method", ": in")
                                Log.d("boolean check", isSuccessful.toString())
                                if (isSuccessful) { // 회원가입 성공
                                    binding.buttonSignUpGoToSignUp.stopAnimation(
                                        TransitionButton.StopAnimationStyle.EXPAND
                                    ) {
                                        Log.d("로그", binding.editTextSignUpConfirmPw.text.toString())
                                        finish()
                                        Log.d("callback method", ": out")
                                    }
                                } else { // 회원가입 실패
                                    binding.buttonSignUpGoToSignUp.stopAnimation(
                                        TransitionButton.StopAnimationStyle.SHAKE,
                                        null
                                    )
                                    MotionToast.createColorToast(
                                        this@SignUpActivity,
                                        "SignUp Error",
                                        "회원가입 도중 오류가 발생했습니다.",
                                        MotionToast.TOAST_ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(this@SignUpActivity,
                                            R.font.maple_story_bold)
                                    )
                                    Log.d("callback method", ": create auth faied")
                                }
                            }
                        })
                    Log.d("After callback  : ", "Create method exit")
                } else {
                    binding.buttonSignUpGoToSignUp.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE,
                        null)
                    return@postDelayed
                }
            }, 300)
        }
    }

    private fun checkValidateSignUp(): Boolean { // 회원가입 전에 양식 유효한지 체크

        // id(email) validation check
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextSignUpId.text.trim()).matches()) {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "이메일 양식을 준수해주세요!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }

        // password validation check
        if (binding.editTextSignUpPw.text.toString()
                .trim() == "" || binding.editTextSignUpConfirmPw.text.toString().trim() == ""
            || binding.editTextSignUpPw.text.toString()
                .trim() != binding.editTextSignUpConfirmPw.text.toString().trim()
        ) {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "비밀번호가 일치하지 않습니다!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }


        // 이름 validation check
        if (binding.editTextSignUpName.text.toString().trim() == "") {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "이름을 입력해주세요!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }

        // 학번 validation check
        val studentIdPattern = Pattern.compile("60\\d{6}")
        if (!studentIdPattern.matcher(binding.editTextSignUpStudentId.text.trim()).matches()) {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "유효하지 않은 학번이에요!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }

        // 전공 validation check
        if (binding.spinnerSignUpMajorList.selectedItemPosition == -1) {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "전공을 선택해주세요!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }

        // 생년월일이 validation check (e.g. 961125)
        if (binding.editTextSignUpDateOfBirth.text.toString().trim() == "") {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "생년월일을 입력해주세요!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }

        val birthPattern = Pattern.compile("\\d{6}")
        if (birthPattern.matcher(binding.editTextSignUpDateOfBirth.text.trim()).matches()) {
            val month = binding.editTextSignUpDateOfBirth.text.substring(2, 4).toInt()
            val day = binding.editTextSignUpDateOfBirth.text.substring(4).toInt()

            Log.d("로그", "SignUpActivity -checkValidateSignUp() called / month = $month, day = $day")

            if (month !in 1..12 || day !in 1..31) {
                MotionToast.createColorToast(
                    this,
                    "SignUp Error",
                    "유효하지 않은 생년월일입니다. 다시 확인해주세요.",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this,
                        R.font.maple_story_bold)
                )
                return false
            }
        } else {
            MotionToast.createColorToast(
                this,
                "SignUp Error",
                "유효하지 않은 생년월일입니다. 다시 확인해주세요.",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,
                    R.font.maple_story_bold)
            )
            return false
        }

        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }


}