package com.example.mtmimyeon_gitmi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mtmimyeon_gitmi.databinding.*
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.example.mtmimyeon_gitmi.db.UserData
import com.example.mtmimyeon_gitmi.mbti.MbtiTestStartFragment
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class HomeFragment : Fragment(), MjuSiteClickedInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isFirstStart = true // 처음 시작할 때만 db에서 유저 데이터 get
    private lateinit var mTimer: Timer
    private lateinit var customTimerTask: CustomTimerTask
    private var isFromSchoolToDestination = true // 학교 -> 도착지 || 도착지 학교

    // 아래 변수들은 CustomTimer inner class 에서 사용하기 위해 클래스 멤버 변수로 선언
    // 기흥역 방향 셔틀 출발시각, 기흥역 도착 예정시각, 학교 도착 예정시각
    private lateinit var gihuengStationDepartureTime: Array<String> // 학교 -> 기흥역
    private lateinit var gihuengStationExpectationTime: Array<String> // 기흥역 -> 학교

    // 시내 방향 셔틀 출발시각, 진입로 도착 예정 시각
    private lateinit var downtownDepartureTime: Array<String> // 학교 -> 시내
    private lateinit var downtownExpectationTime: Array<String> // 진입로 -> 학교

    // 진입로 방향 셔틀 출발시각, 진입로 도착 예정 시각
    private lateinit var accessRoadDepartureTime: Array<String> // 학교 -> 진입로
    private lateinit var accessRoadExpectationTime: Array<String> // 진입로 -> 학교

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()

        return binding.root
    }

    private fun init() {

        // db에서 유저 데이터 가져와서 SharedPrefernces 저장
        if (this.isFirstStart) {
            val myUid = FirebaseAuth.getInstance().currentUser.uid
            val db = DatabaseManager()
            Log.d("로그", "HomeFragment -init() called // DB에서 처음으로 데이터 get")
            this.isFirstStart = false

            db.callUserData(myUid, object : Callback<UserData> {
                override fun onCallback(data: UserData) {
                    Log.d("로그", "HomeFragment -onCallback() called / data = $data")
                    SharedPrefManager.setUserData(data)
                }
            })
        }

        // main banner init
        val myMbtiResult = SharedPrefManager.getMyMbtiType()

        if (myMbtiResult != "") { // mbti 테스트를 이미 했으면
            binding.linearLayoutFragmentHomeGoToMbtiContainer.visibility = View.GONE

            val mbtiImgList = resources.obtainTypedArray(R.array.mbti_img)
            // 해당 mbti에 해당하는 values.xml index 매칭 HashMap
            val mbtiIndex = hashMapOf(
                "INTJ" to 0,
                "INFJ" to 1,
                "ISTJ" to 2,
                "ISTP" to 3,
                "INTP" to 4,
                "INFP" to 5,
                "ISFJ" to 6,
                "ISFP" to 7,
                "ENTJ" to 8,
                "ENFJ" to 9,
                "ESTJ" to 10,
                "ESTP" to 11,
                "ENTP" to 12,
                "ENFP" to 13,
                "ESFJ" to 14,
                "ESFP" to 15
            )

            val bannerColorList = arrayListOf(
                R.drawable.bg_rounded_banner1,
                R.drawable.bg_rounded_banner2,
                R.drawable.bg_rounded_banner3,
                R.drawable.bg_rounded_banner4,
            )

            // MBTI 이미지 설정
            binding.imageViewFragmentHomeMbtiImg.setImageResource(
                mbtiImgList.getResourceId(
                    mbtiIndex[myMbtiResult]!!,
                    -1
                )
            )
            // MBTI 배경 설정
            binding.constraintLayoutFragmentHomeMyMbtiContainer.background =
                ContextCompat.getDrawable(requireContext(),
                    bannerColorList[mbtiIndex[myMbtiResult]!! % bannerColorList.size])
            // MBTI Title 설정
            binding.textViewFragmentHomeMbtiTypeTitle.text =
                requireContext().resources.getStringArray(R.array.main_banner_mbti_type_title)[mbtiIndex[myMbtiResult]!!]
            // MBTI Subtitle 설정
            binding.textViewFragmentHomeMbtiTypeSubTitle.text =
                requireContext().resources.getStringArray(R.array.main_banner_mbti_type_subtitle)[mbtiIndex[myMbtiResult]!!]

        } else { // mbti 테스트를 한 번도 안 했으면
            binding.constraintLayoutFragmentHomeMyMbtiContainer.visibility = View.GONE
            // main banner 클릭 시, mbti 메뉴로 이동
            binding.textviewMainGoToMbti.setOnClickListener {
                (requireActivity() as HomeActivity).binding.bottomNavigationViewHome.setItemSelected(
                    R.id.menu_mbti,
                    true
                )
            }
        }


        // 홈 명지대 아이콘 데이터 초기화
        val mjuSiteImageList =
            requireContext().resources.obtainTypedArray(R.array.mjuSiteImageList) // mju image list
        val mjuSiteTextList =
            requireContext().resources.getStringArray(R.array.mjuSiteTextList)  // mju string list
        val itemMjuSiteList = ArrayList<ItemMjuSite>()

        // 명지대 아이콘 데이터 삽입
        for (i in mjuSiteTextList.indices) {
            itemMjuSiteList.add(
                ItemMjuSite(
                    mjuImage = mjuSiteImageList.getResourceId(i, -1),
                    mjuText = mjuSiteTextList[i]
                )
            )
        }

        // init recycler view(명지대 아이콘)
        val mjuSiteRecyclerAdapter = MjuSiteRecyclerAdapter(itemMjuSiteList, this)
        binding.recyclerviewFragmentHomeUnvInfo.apply {
            adapter = mjuSiteRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // init recyclerView (기흥역 방향 버스 경유지 목록)
        val gihuengStationStopoverAdapter =
            StopoverAdapter(requireContext().resources.getStringArray(R.array.gihueng_station_bus_stopover))
        binding.recyclerViewFragmentHomeGihuengStopoverList.apply {
            adapter = gihuengStationStopoverAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // 도착지 -> 학교 || 학교 -> 도착지 버튼 클릭 시
        binding.textViewFragmentHomeChangeDestination.setOnClickListener {
            if (isFromSchoolToDestination) { // 학교 -> 도착지
                binding.textViewFragmentHomeChangeDestination.text = "도착지→학교"
                isFromSchoolToDestination = false
            } else {
                binding.textViewFragmentHomeChangeDestination.text = "학교→도착지"
                isFromSchoolToDestination = true
            }
            this.customTimerTask.run()
        }


        // init recyclerView (진입로 방향 버스 경유지 목록)
        val roadAccessStopoverAdapter =
            StopoverAdapter(requireContext().resources.getStringArray(R.array.access_road_bus_stopover))
        binding.recyclerViewFragmentHomeAccessRoadStopoverList.apply {
            adapter = roadAccessStopoverAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // init recyclerView (시내 방향 버스 경유지 목록)
        val downtownStopoverAdapter =
            StopoverAdapter(requireContext().resources.getStringArray(R.array.downtown_bus_stopover))
        binding.recyclerViewFragmentHomeDowntownStopoverList.apply {
            adapter = downtownStopoverAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // 기흥역 셔틀 배열 init
        gihuengStationDepartureTime =
            requireContext().resources.getStringArray(R.array.gihueng_station_departure_time)
        gihuengStationExpectationTime =
            requireContext().resources.getStringArray(R.array.gihueng_station_expectation_time)

        // 시내 셔틀 배열 init
        downtownDepartureTime =
            requireContext().resources.getStringArray(R.array.downtown_departure_time) // 시내행 셔틀버스 출발시간
        downtownExpectationTime =
            requireContext().resources.getStringArray(R.array.downtown_expectation_time) // 시내생 셔틀버스 진입로 도착 예정 시간

        // 진입로 셔틀 시간 배열 init
        accessRoadDepartureTime =
            requireContext().resources.getStringArray(R.array.access_road_departure_time) // 진입로행 셔틀버스 출발시간
        accessRoadExpectationTime =
            requireContext().resources.getStringArray(R.array.access_road_expectation_time) // 진입로행 셔틀버스 진입로 도착 예정 시간

        // 추가 정보 버튼 클릭 시, 버스 노선 리사이킄러뷰 보이기
        binding.imageViewFragmentHomeInfo.setOnClickListener {
            if (binding.recyclerViewFragmentHomeAccessRoadStopoverList.visibility == View.GONE) {
                binding.recyclerViewFragmentHomeGihuengStopoverList.visibility = View.VISIBLE
                binding.recyclerViewFragmentHomeDowntownStopoverList.visibility = View.VISIBLE
                binding.recyclerViewFragmentHomeAccessRoadStopoverList.visibility = View.VISIBLE
                binding.nestedScrollViewFragmentHomeRootScroll.post {
                    binding.nestedScrollViewFragmentHomeRootScroll.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                binding.recyclerViewFragmentHomeGihuengStopoverList.visibility = View.GONE
                binding.recyclerViewFragmentHomeDowntownStopoverList.visibility = View.GONE
                binding.recyclerViewFragmentHomeAccessRoadStopoverList.visibility = View.GONE
            }
        }
    }

    // 주기적으로 시간 체크
    inner class CustomTimerTask : TimerTask() {
        override fun run() {
            CoroutineScope(Dispatchers.Default).launch {
                val simpleDateFormat = SimpleDateFormat("HH:mm")
                val currentTime = simpleDateFormat.parse(simpleDateFormat.format(Date()))
                var gihuengIndex = -1
                var downtownIndex = -1
                var accessRoadIndex = -1
                val gihuengBusTime = if (isFromSchoolToDestination)
                    gihuengStationDepartureTime // 학교 -> 기흥역
                else
                    gihuengStationExpectationTime // 기흥역 -> 학교
                val downtownBusTime = if (isFromSchoolToDestination)
                    downtownDepartureTime // 학교 -> 시내
                else
                    downtownExpectationTime // 시내 -> 힉교
                val accessRoadBusTime = if (isFromSchoolToDestination)
                    accessRoadDepartureTime // 학교 -> 진입로
                else
                    accessRoadExpectationTime // 진입로 -> 학교


                // 기흥역 행 셔틀버스 가장 빠른 시간대 탐색
                for (i in gihuengBusTime.indices) {
                    val fastestTime = simpleDateFormat.parse(gihuengBusTime[i])
                    // Log.d("로그", "fastesTime = $fastestTime, currentTime = $currentTime")
                    if (fastestTime!!.after(currentTime)) {
                        gihuengIndex = i
                        break
                    }
                }

                // 용인시내 행 셔틀버스 가장 빠른 시간대 탐색
                for (i in downtownBusTime.indices) {
                    val fastestTime = simpleDateFormat.parse(downtownBusTime[i])
                    if (fastestTime!!.after(currentTime)) {
                        downtownIndex = i
                        break
                    }
                }

                // 진입로 행 셔틀버스 가장 빠른 시간대 탐색
                for (i in accessRoadBusTime.indices) {
                    val fastestTime = simpleDateFormat.parse(accessRoadBusTime[i])
                    if (fastestTime!!.after(currentTime)) {
                        accessRoadIndex = i
                        break
                    }
                }

                Log.d("TAG", "$gihuengIndex, $downtownIndex, $accessRoadIndex")


                // 시간 뷰 최신화
                withContext(Dispatchers.Main) {

                    if (gihuengIndex == -1) { // 기흥역 금일 더 이상 남은 시간대 버스가 없을 때
                        binding.textViewFragmentHomeGihuengStationBusTime.text = "No Bus"
                        binding.textViewFragmentHomeGihuengStationBusTime.setTextColor(Color.RED)
                    } else { // 기흥역 방향 남은 시간 설정
                        binding.textViewFragmentHomeGihuengStationBusTime.text =
                            gihuengBusTime[gihuengIndex]
                    }

                    if (downtownIndex == -1) { // 시내 금일 더 이상 남은 시간대 버스가 없을 때
                        binding.textViewFragmentHomeDowntownBusTime.text = "No Bus"
                        binding.textViewFragmentHomeDowntownBusTime.setTextColor(Color.RED)
                    } else { // 용인 시내 방향 남은 시간 설정
                        binding.textViewFragmentHomeDowntownBusTime.text =
                            downtownBusTime[downtownIndex]
                    }


                    if (accessRoadIndex == -1) { // 진입로 금일 더 이상 남은 시간대 버스가 없을 때
                        binding.textViewFragmentHomeRoadAccessBusTime.text = "No Bus"
                        binding.textViewFragmentHomeRoadAccessBusTime.setTextColor(Color.RED)
                    } else { // 진입로 방향 남은 시간 설정
                        binding.textViewFragmentHomeRoadAccessBusTime.text =
                            accessRoadBusTime[accessRoadIndex]
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // 메모리 릭 방지
    }

    override fun onStart() {
        super.onStart()
        this.mTimer = Timer()
        this.customTimerTask = CustomTimerTask()
        this.mTimer.schedule(customTimerTask, 0, 5000)
    }

    override fun onPause() {
        Log.d("로그", "HomeFragment -onPause() called")
        this.customTimerTask.cancel()
        this.mTimer.cancel()
        this.mTimer.purge()
        super.onPause()
    }

    override fun onItemClicked(item: String) {
        var url = ""

        when (item) {
            "phone" -> {
                Intent(requireContext(), CampusPhoneNumberActivity::class.java).also {
                    requireActivity().startActivity(it)
                }
                requireActivity().overridePendingTransition(
                    R.anim.activity_slide_in,
                    R.anim.activity_slide_out
                )
                return
            }
            "건물찾기" -> {
                if (isLocationPermissionGranted()) {
                    Intent(requireContext(), MapDetailsActivity::class.java).also {
                        startActivity(it)
                    }
                    requireActivity().overridePendingTransition(
                        R.anim.activity_slide_in,
                        R.anim.activity_slide_out
                    )
                }
                return
            }

            "학교홈" -> url = "https://www.mju.ac.kr/mjukr/index.do"
            "학과홈" -> {
                val major = SharedPrefManager.getUserData().major
                var majorIndex = -1
                val majorList = requireContext().resources.getStringArray(R.array.major)
                for (i in majorList.indices) { // index get
                    if (majorList[i] == major) {
                        majorIndex = i
                        break
                    }
                }
                url = requireContext().resources.getStringArray(R.array.major_site)[majorIndex]
            }
            "library" -> url = "https://lib.mju.ac.kr/index.ax"
            "eclass" -> url = "https://eclass.mju.ac.kr/user/index.action"
            "lms" -> url = "https://lms.mju.ac.kr/ilos/main/main_form.acl"
            "myiweb" -> url = "https://myiweb.mju.ac.kr/servlet/security/MySecurityStart"
            "myicap" -> url = "https://myicap.mju.ac.kr/"
            "ucheck" -> url =
                "https://ucheck.mju.ac.kr/;jsessionid=F35A5F3F48644210CEB08183C2E8D492#"
            "기숙사" -> url = "https://jw4.mju.ac.kr/user/dorm/index.action"
            "문진표" -> url = "http://www.mjuqr.kr/view/4b46ea5151336b2f7668b67551b1a511"
            "수강신청" -> url = "http://class.mju.ac.kr/"
            else -> url = "https://www.mju.ac.kr/mjukr/index.do"
        }
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
            startActivity(it)
            requireActivity().overridePendingTransition(
                R.anim.activity_slide_in,
                R.anim.activity_slide_out
            )
        }

    }

    // 위치 정보 권한 처리
    private fun isLocationPermissionGranted(): Boolean {
        val isFirstCheck = SharedPrefManager.getPermissionAccessFineLocation()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // 거부만 한 경우 사용자에게 왜 필요한지 이유를 설명해주는게 좋다
                val snackBar = Snackbar.make(
                    binding.root,
                    "학교 건물을 찾기 위해서는 위치 권한이 필요합니다",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // 처음 물었는지 여부를 저장
                    SharedPrefManager.setPermissionAccessFineLocation(false)
                    // 권한요청
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                } else {
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동한다.
                    val snackBar = Snackbar.make(
                        binding.root,
                        "위치 권한이 필요합니다. 확인를 누르면 설정 화면으로 이동합니다.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.setAction("확인") {
                        Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", requireActivity().packageName, null)
                            requireActivity().startActivity(this)
                        }
                    }
                    snackBar.show()
                }
            }
            return false
        } else {
            return true
        }
    }
}

data class ItemMjuSite(val mjuImage: Int, val mjuText: String)

// 원래는 파일을 나누는 게 원칙이나 가시성을 위해서 일시적으로 한 파일에서 관리
// 리사이클러뷰 어댑터
class MjuSiteRecyclerAdapter(
    private val itemMjuSiteList: ArrayList<ItemMjuSite>,
    private val mjuSiteClickedInterface: MjuSiteClickedInterface,
) : RecyclerView.Adapter<MjuSiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MjuSiteViewHolder {
        val binding = ItemMjuSiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MjuSiteViewHolder(binding, mjuSiteClickedInterface)
    }

    override fun onBindViewHolder(holder: MjuSiteViewHolder, position: Int) {
        holder.bind(itemMjuSiteList[position])
    }

    override fun getItemCount(): Int = itemMjuSiteList.size


}

// 리사이클러뷰 뷰홀더
class MjuSiteViewHolder(
    private val item: ItemMjuSiteBinding,
    private val mjuSiteClickedInterface: MjuSiteClickedInterface,
) : RecyclerView.ViewHolder(item.root) {

    init {
        item.root.setOnClickListener {
            mjuSiteClickedInterface.onItemClicked(item.textViewItemMjuText.text.toString())
        }
    }

    fun bind(itemMjuSite: ItemMjuSite) {
        item.imageViewItemMjuSiteImg.setImageResource(itemMjuSite.mjuImage)
        item.textViewItemMjuText.text = itemMjuSite.mjuText
    }
}

// 리사이클러뷰 내 아이템 클릭 이벤트
interface MjuSiteClickedInterface {
    fun onItemClicked(item: String)
}


// 경유지 목록 리사이클러뷰 어댑터
class StopoverAdapter(private val stopoverList: Array<String>) :
    RecyclerView.Adapter<StopoverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopoverViewHolder {
        return StopoverViewHolder(
            ItemStopoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StopoverViewHolder, position: Int) {
        if (position == stopoverList.lastIndex)
            holder.bind(stopoverList[position], true)
        else
            holder.bind(stopoverList[position], false)
    }

    override fun getItemCount(): Int = stopoverList.size

}

// 리사이클러뷰 뷰홀더
class StopoverViewHolder(private val item: ItemStopoverBinding) :
    RecyclerView.ViewHolder(item.root) {
    fun bind(stopoverName: String, isLastNode: Boolean) {
        item.textViewItemStopoverStopoverName.text = stopoverName
        if (isLastNode)
            item.viewItemConnectLineConnectLine.visibility = View.GONE
    }
}




