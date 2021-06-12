package com.mju.mtmi

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat

import androidx.fragment.app.Fragment
import com.mju.mtmi.databinding.ActivityHomeBinding
import com.mju.mtmi.mbti.MbtiTestStartFragment
import com.mju.mtmi.myClass.MyClassMainFragment
import com.mju.mtmi.account.MyProfileActivity
import www.sanju.motiontoast.MotionToast
import java.security.MessageDigest

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var nowFragment: Fragment
    private var backKeyPressedTime: Long = 0 // 마지막으로 back key를 눌렀던 시간

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun init() {
        nowFragment = HomeFragment()

        // toolbar appbar로 지정
        setSupportActionBar(binding.topAppBarHome)
        replaceFragment(nowFragment)

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

         try {
            val info =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = info.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val key = String(Base64.encode(md.digest(), 0))
                Log.d("해시값", key)
            }
        } catch (e: Exception) {
            Log.d("name not found", e.toString())
        }


        // 바텀네비게이션 뷰 메뉴 클릭했을 때
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


    private fun replaceFragment(fragment: Fragment) {
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
                Intent(this, MyProfileActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
                }
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }

    }

    // 권한 승인 체크
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) { // 위치 정보 권한
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 맵 액티비티 실행
                Log.d("로그", "사용자가 권한 승인 완료")
                Intent(this, MapDetailsActivity::class.java).also {
                    startActivity(it)
                }
                overridePendingTransition(
                    R.anim.activity_slide_in,
                    R.anim.activity_slide_out
                )
            } else {
                Log.d("로그", "${grantResults.size}")
                MotionToast.createColorToast(
                    this,
                    "권한 요청 거부됨",
                    "학교 건물 길찾기를 하기 위해서는 권한 동의가 필요해요",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            }
        }
    }
}