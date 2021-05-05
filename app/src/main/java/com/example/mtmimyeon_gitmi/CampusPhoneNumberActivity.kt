package com.example.mtmimyeon_gitmi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityCampusPhoneNumberBinding
import com.example.mtmimyeon_gitmi.databinding.ItemTelephoneBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemCampusPhoneNumber

class CampusPhoneNumberActivity : AppCompatActivity(), CampusInfoClickedInterface {
    private lateinit var binding: ActivityCampusPhoneNumberBinding
    private lateinit var campusPhoneNumberRecyclerAdapter: CampusPhoneNumberRecyclerAdapter
    private val itemCampusPhoneNumberList = ArrayList<ItemCampusPhoneNumber>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCampusPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 테스트 데이터 삽입
        for(i in 0..20) {
            itemCampusPhoneNumberList.add(ItemCampusPhoneNumber("[대외협력·홍보위원회]",
                "자연 캠퍼스 함박관 2층 2403", "https://www.mju.ac.kr/sites/mjukr/intro/intro.html", "tel:0318981127"))
        }
        campusPhoneNumberRecyclerAdapter = CampusPhoneNumberRecyclerAdapter(this)
        binding.recyclerviewCampusPhoneNumberList.apply {
            adapter = campusPhoneNumberRecyclerAdapter
            layoutManager = LinearLayoutManager(this@CampusPhoneNumberActivity, LinearLayoutManager.VERTICAL, false)
            campusPhoneNumberRecyclerAdapter.submit(itemCampusPhoneNumberList)
        }
    }

    override fun setPhoneNumberClicked(phoneNumber: String) {
        // 리사이클러 뷰 내의 전화번호 textview 클릭 시, 전화앱으로 연결
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber)))
    }

    override fun setSiteUrlClicked(siteUrl: String) {
        // 리사이클러 뷰 내의 사이트 URL 클릭 시, 디바이스 내 브라우저 앱으려 연결
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl)))
    }
}

interface CampusInfoClickedInterface {
    fun setPhoneNumberClicked(phoneNumber: String) // 전화번호 TextView 클릭했을 때, 전화앱으로 연결
    fun setSiteUrlClicked(siteUrl: String) // 사이트 URL TextView 클릭했을 때, 다른 브라우저 앱으로 연결
}

class CampusPhoneNumberRecyclerAdapter() : RecyclerView.Adapter<CampusPhoneNumberViewHolder>() {
    private lateinit var itemCampusPhoneNumberList: ArrayList<ItemCampusPhoneNumber>
    private lateinit var campusInfoClickedInterface: CampusInfoClickedInterface

    constructor(campusInfoClickedInterface: CampusInfoClickedInterface): this() {
        this.campusInfoClickedInterface = campusInfoClickedInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampusPhoneNumberViewHolder {
        val binding = ItemTelephoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CampusPhoneNumberViewHolder(binding, campusInfoClickedInterface)
    }

    override fun onBindViewHolder(holder: CampusPhoneNumberViewHolder, position: Int) {
        holder.bind(itemCampusPhoneNumberList[position])
    }

    override fun getItemCount(): Int {
        return itemCampusPhoneNumberList.size
    }

    fun submit(itemCampusPhoneNumberList: ArrayList<ItemCampusPhoneNumber>) {
        this.itemCampusPhoneNumberList = itemCampusPhoneNumberList
    }

}

// 리사이클러뷰 뷰홀더
class CampusPhoneNumberViewHolder(private val item: ItemTelephoneBinding) : RecyclerView.ViewHolder(item.root) {
    private lateinit var  campusInfoClickedInterface: CampusInfoClickedInterface

    constructor(item: ItemTelephoneBinding, campusInfoClickedInterface: CampusInfoClickedInterface): this(item) {
        this.campusInfoClickedInterface = campusInfoClickedInterface

        item.textViewItemPhoneNumber.setOnClickListener {
            this.campusInfoClickedInterface.setPhoneNumberClicked(item.textViewItemPhoneNumber.text.toString())
        }

        item.textViewItemSiteUrl.setOnClickListener {
            this.campusInfoClickedInterface.setSiteUrlClicked(item.textViewItemSiteUrl.text.toString())
        }
    }

    fun bind(itemCampusPhoneNumber: ItemCampusPhoneNumber) {
//        val content = SpannableString("content")
//        content.setSpan(UnderlineSpan(), 0, content.length, 0)

        item.textViewItemLocation.text = itemCampusPhoneNumber.location
        item.textViewItemSiteUrl.text = itemCampusPhoneNumber.siteUrl
        item.textViewItemName.text = itemCampusPhoneNumber.name
        item.textViewItemPhoneNumber.text = itemCampusPhoneNumber.phoneNumber
    }
}

