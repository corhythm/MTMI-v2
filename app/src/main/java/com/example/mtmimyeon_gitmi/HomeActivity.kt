package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.mtmimyeon_gitmi.databinding.ActivityHomeBinding
import com.example.mtmimyeon_gitmi.mbti.MbtiTestStartFragment
import com.example.mtmimyeon_gitmi.myclass.MyClassMailToProfessorFragment
import com.example.mtmimyeon_gitmi.myclass.MyClassMainFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBarHome)
        replaceFragment(HomeFragment.getInstance())

        // 원래 이렇게 하면 안 됨. 시각적으로 보기 위해서 임시로 테스트
        binding.floatingActionButtomHome.setOnClickListener {
            replaceFragment(HomeFragment.getInstance())
            // 아이템 선택된 거 초기화
            binding.bottomNavigationViewHome.setItemSelected(-1)
        }

        binding.bottomNavigationViewHome.setOnItemSelectedListener {
            when (it) {
                R.id.menu_mbti -> {
                    replaceFragment(MyClassMailToProfessorFragment.getInstance())
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
        menuInflater.inflate(R.menu.tool_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_toolBar_user -> {
                Log.d("로그", "HomeActivity -onOptionsItemSelected() called")
            }
        }
        return true
    }
}