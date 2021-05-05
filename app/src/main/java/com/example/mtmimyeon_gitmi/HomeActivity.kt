package com.example.mtmimyeon_gitmi

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.Log

import android.view.Gravity

import android.view.Menu
import android.view.MenuItem
import android.view.Window

import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.databinding.ActivityHomeBinding
import com.example.mtmimyeon_gitmi.mbti.MbtiTestStartFragment
import com.example.mtmimyeon_gitmi.myclass.MyClassMainFragment
import com.example.mtmimyeon_gitmi.account.MyProfileActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

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
        setSupportActionBar(binding.topAppBarHome)
        replaceFragment(HomeFragment.getInstance())
    }

    private fun init() {

        // 원래 이렇게 하면 안 됨. 시각적으로 보기 위해서 임시로 테스트
        binding.floatingActionButtonHome.setOnClickListener {
            replaceFragment(HomeFragment.getInstance())
            // 아이템 선택된 거 초기화
            binding.bottomNavigationViewHome.setItemSelected(-1)
        }

        binding.bottomNavigationViewHome.setOnItemSelectedListener {
            when (it) {
                R.id.menu_mbti -> {
                    replaceFragment(MbtiTestStartFragment.getInstance())
                }
                R.id.menu_classlist -> {
                    Log.d("로그", "HomeActivity -init() called / ${MyClassMainFragment.getInstance()}")
                    replaceFragment(MyClassMainFragment.getInstance())
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
                }
            }
        }
        return true
    }
}