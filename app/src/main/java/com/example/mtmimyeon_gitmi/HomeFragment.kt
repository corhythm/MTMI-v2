package com.example.mtmimyeon_gitmi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.FragmentHomeBinding
import com.example.mtmimyeon_gitmi.databinding.ItemMjuSiteBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class HomeFragment : Fragment(), MjuSiteClickedInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()

        return binding.root
    }

    private fun init() {

        val mjuSiteImageList =
            requireContext().resources.obtainTypedArray(R.array.mjuSiteImageList) // mju image list
        val mjuSiteTextList =
            requireContext().resources.getStringArray(R.array.mjuSiteTextList)  // mju string list
        val itemMjuSiteList = ArrayList<ItemMjuSite>()

        // 데이터 삽입
        for (i in mjuSiteTextList.indices) {
            itemMjuSiteList.add(
                ItemMjuSite(
                    mjuImage = mjuSiteImageList.getResourceId(i, -1),
                    mjuText = mjuSiteTextList[i]
                )
            )
        }

        // init recycler view
        val mjuSiteRecyclerAdapter = MjuSiteRecyclerAdapter(itemMjuSiteList, this)
        binding.recyclerviewMainUnvInfo.apply {
            adapter = mjuSiteRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // 메모리 릭 방지
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
                SharedPrefManager.clearAllPermissionData()
                return
            }
            "주변건물" -> {
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
            "학과홈" -> url = "https://cs.mju.ac.kr"
            "library" -> url = "https://lib.mju.ac.kr/index.ax"
            "eclass" -> url = "https://eclass.mju.ac.kr/user/index.action"
            "lms" -> url = "https://lms.mju.ac.kr/ilos/main/main_form.acl"
            "myiweb" -> url = "https://myiweb.mju.ac.kr/servlet/security/MySecurityStart"
            "myicap" -> url = "https://myicap.mju.ac.kr/"
            "ucheck" -> url =
                "https://ucheck.mju.ac.kr/;jsessionid=F35A5F3F48644210CEB08183C2E8D492#"
            "기숙사" -> url = "https://jw4.mju.ac.kr/user/dorm/index.action"
            "문진표" -> url = "http://www.mjuqr.kr/view/4b46ea5151336b2f7668b67551b1a511"
            "수강신청" -> url = "http://http://class.mju.ac.kr/"
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
    private val mjuSiteClickedInterface: MjuSiteClickedInterface
) : RecyclerView.Adapter<MjuSiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MjuSiteViewHolder {
        val binding = ItemMjuSiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MjuSiteViewHolder(binding, mjuSiteClickedInterface)
    }

    override fun onBindViewHolder(holder: MjuSiteViewHolder, position: Int) {
        holder.bind(itemMjuSiteList[position])
    }

    override fun getItemCount(): Int {
        return itemMjuSiteList.size
    }

}

// 리사이클러뷰 뷰홀더
class MjuSiteViewHolder(
    private val item: ItemMjuSiteBinding,
    private val mjuSiteClickedInterface: MjuSiteClickedInterface
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

