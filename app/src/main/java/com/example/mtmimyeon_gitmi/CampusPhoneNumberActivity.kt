package com.example.mtmimyeon_gitmi

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityCampusPhoneNumberBinding
import com.example.mtmimyeon_gitmi.databinding.ItemTelephoneBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemCampusPhoneNumber

class CampusPhoneNumberActivity : AppCompatActivity(), CampusInfoClickInterface {
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
                "자연 캠퍼스 함박관 2층 2401", "www.mju.ac.kr/main.do", "tel:0318981127"))
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
}

interface CampusInfoClickInterface {
    fun setPhoneNumberClicked(phoneNumber: String) // 전화번호 클릭했을 때, 전화앱으로 연결
}

class CampusPhoneNumberRecyclerAdapter() : RecyclerView.Adapter<CampusPhoneNumberViewHolder>() {
    private lateinit var itemCampusPhoneNumberList: ArrayList<ItemCampusPhoneNumber>
    private lateinit var campusInfoClickInterface: CampusInfoClickInterface

    constructor(campusInfoClickInterface: CampusInfoClickInterface): this() {
        this.campusInfoClickInterface = campusInfoClickInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampusPhoneNumberViewHolder {
        val binding = ItemTelephoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CampusPhoneNumberViewHolder(binding, campusInfoClickInterface)
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
    private lateinit var  campusInfoClickInterface: CampusInfoClickInterface

    constructor(item: ItemTelephoneBinding, campusInfoClickInterface: CampusInfoClickInterface): this(item) {
        this.campusInfoClickInterface = campusInfoClickInterface

        item.textViewItemPhoneNumber.setOnClickListener {
            this.campusInfoClickInterface.setPhoneNumberClicked(item.textViewItemPhoneNumber.text.toString())
        }
    }

    fun bind(itemCampusPhoneNumber: ItemCampusPhoneNumber) {
//        val content = SpannableString("content")
//        content.setSpan(UnderlineSpan(), 0, content.length, 0)

        item.textViewItemLocation.text = itemCampusPhoneNumber.location
        item.textViewItemName.text = itemCampusPhoneNumber.name
        item.textViewItemPhoneNumber.text = itemCampusPhoneNumber.phoneNumber
    }
}

