package com.example.mtmimyeon_gitmi

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView

import android.widget.Toast

import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.databinding.ActivityHomeBinding
import com.example.mtmimyeon_gitmi.mbti.MbtiTestStartFragment
import com.example.mtmimyeon_gitmi.myClass.MyClassMainFragment
import com.example.mtmimyeon_gitmi.account.MyProfileActivity
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import org.w3c.dom.Text

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var nowFragment: Fragment
    private var backKeyPressedTime: Long = 0 // 마지막으로 back key를 눌렀던 시간
    private lateinit var toast: Toast // toast 메시지

    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) { // activity 옆으로 이동 애니메이션
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            // set an slide transition
            enterTransition = Slide(Gravity.END)
            exitTransition = Slide(Gravity.START)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        nowFragment = HomeFragment()

        setSupportActionBar(binding.topAppBarHome)
        replaceFragment(nowFragment)

        // 원래 이렇게 하면 안 됨. 시각적으로 보기 위해서 임시로 테스트
        // 홈 버튼 클릭됐을 떄
        binding.floatingActionButtonHome.setOnClickListener {
            if (nowFragment !is HomeFragment) {
                nowFragment = HomeFragment()
                replaceFragment(nowFragment)
                Log.d("로그", "HomeActivity -init() called nowFragment")
            }

            // 바텀 네비게이션뷰 아이템 선택된 거 초기화
            binding.bottomNavigationViewHome.setItemSelected(-1)
        }

        binding.bottomNavigationViewHome.setOnItemSelectedListener {
            when (it) {
                R.id.menu_mbti -> { // mbti tab click
                    if (nowFragment !is MbtiTestStartFragment) {
                        nowFragment = MbtiTestStartFragment()
                        replaceFragment(nowFragment)
                    }
                }
                R.id.menu_classlist -> { // my class tab click
                    if (nowFragment !is MyClassMainFragment) {
                        nowFragment = MyClassMainFragment()
                        replaceFragment(nowFragment)
                    }
                }
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout_home_container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_toolBar_user -> {
//                startActivity(Intent(this, MyProfileActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                Intent(this, MyProfileActivity::class.java).also {
                    startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                    overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
                }
            }
            R.id.menu_toolBar_deleteAccount -> {
                SharedPrefManager.clearAllData()
            }
        }
        return true
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
            ((toast.view as ViewGroup).getChildAt(0) as TextView).setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                15F
            ) // 토스트 메시지 폰트 사이즈 변경
            toast.show()
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }

    }
}