package com.example.mtmimyeon_gitmi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.FragmentHomeBinding
import com.example.mtmimyeon_gitmi.databinding.ItemMjuSiteBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemMjuSite

class HomeFragment private constructor() : Fragment(), MjuSiteRecyclerViewInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 나중에 firebase에서 데이터를 가지고 올 것이므로 임시로 사용
        val mjuSiteImageList = requireContext().resources.obtainTypedArray(R.array.mjuSiteImageList)
        val mjuSiteTextList = requireContext().resources.getStringArray(R.array.mjuSiteTextList)
        val itemMjuSiteList = ArrayList<ItemMjuSite>()

        // 데이터 삽입
        for (i in mjuSiteTextList.indices) {
            itemMjuSiteList.add(ItemMjuSite(mjuImage = mjuSiteImageList.getResourceId(i, -1), mjuText = mjuSiteTextList[i]))
        }

        val mjuSiteRecyclerAdapter = MjuSiteRecyclerAdapter(this)
        binding.recyclerviewMainUnvInfo.apply {
            adapter = mjuSiteRecyclerAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            mjuSiteRecyclerAdapter.submit(itemMjuSiteList)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // 메모리 릭 방지
    }

    override fun onItemClicked(item: String) {
        when(item) {
            "학교홈" -> { }
            "학과홈" -> { }
            "library" -> { }
            "eclass" -> { }
            "lms" -> { }
            "myiweb" -> { }
            "myicap" -> { }
            "phone" -> {
                // test code 추후에 수정
                (requireContext() as HomeActivity).startActivity(Intent(requireContext(), CampusPhoneNumberActivity::class.java))
            }
            "ucheck" -> { }
            "기숙사" -> { }
            "문진표" -> { }
            "수강신청" -> { }
            else -> { }
        }
    }
}


// 원래는 파일을 나누는 게 원칙이나 가시성을 위해서 일시적으로 한 파일에서 관리
// 리사이클러뷰 어댑터
class MjuSiteRecyclerAdapter() : RecyclerView.Adapter<MjuSiteViewHolder>() {
    private lateinit var itemMjuSiteList: ArrayList<ItemMjuSite>
    private lateinit var mjuSiteRecyclerViewInterface: MjuSiteRecyclerViewInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MjuSiteViewHolder {
        val binding = ItemMjuSiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MjuSiteViewHolder(binding, mjuSiteRecyclerViewInterface)
    }

    constructor(mjuSiteRecyclerViewInterface: MjuSiteRecyclerViewInterface): this() {
        this.mjuSiteRecyclerViewInterface = mjuSiteRecyclerViewInterface
    }

    override fun onBindViewHolder(holder: MjuSiteViewHolder, position: Int) {
        holder.bind(itemMjuSiteList[position])
    }

    override fun getItemCount(): Int {
        return itemMjuSiteList.size
    }

    fun submit(itemMjuSiteList: ArrayList<ItemMjuSite>) {
        this.itemMjuSiteList = itemMjuSiteList
    }

}

// 리사이클러뷰 뷰홀더
class MjuSiteViewHolder(private val item: ItemMjuSiteBinding) : RecyclerView.ViewHolder(item.root) {
    private lateinit var mjuSiteRecyclerViewInterface: MjuSiteRecyclerViewInterface

    constructor(item: ItemMjuSiteBinding, mjuSiteRecyclerViewInterface: MjuSiteRecyclerViewInterface): this(item) {
        this.mjuSiteRecyclerViewInterface = mjuSiteRecyclerViewInterface
    }

    init {
        item.root.setOnClickListener {
            mjuSiteRecyclerViewInterface.onItemClicked(item.textViewItemMjuText.text.toString())
            Log.d("로그", item.textViewItemMjuText.text.toString())
        }
    }
    fun bind(itemMjuSite: ItemMjuSite) {
        item.imageViewItemMjuSiteImg.setImageResource(itemMjuSite.mjuImage)
        item.textViewItemMjuText.text = itemMjuSite.mjuText
    }
}

// 리사이클러뷰 내 아이템 클릭 이벤트
interface MjuSiteRecyclerViewInterface {
    fun onItemClicked(item: String)
}

