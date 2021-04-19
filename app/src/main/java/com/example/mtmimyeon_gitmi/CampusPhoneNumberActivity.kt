package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityCampusPhoneNumberBinding
import com.example.mtmimyeon_gitmi.databinding.ItemTelephoneBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemCampusPhoneNumber

class CampusPhoneNumberActivity : AppCompatActivity() {
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
                "자연 캠퍼스 함박관 2층 2401", "www.mju.ac.kr/main.do", "031)898-1127"))
        }
        campusPhoneNumberRecyclerAdapter = CampusPhoneNumberRecyclerAdapter()
        binding.recyclerviewCampusPhoneNumberList.apply {
            adapter = campusPhoneNumberRecyclerAdapter
            layoutManager = LinearLayoutManager(this@CampusPhoneNumberActivity, LinearLayoutManager.VERTICAL, false)
            campusPhoneNumberRecyclerAdapter.submit(itemCampusPhoneNumberList)
        }
    }
}

class CampusPhoneNumberRecyclerAdapter : RecyclerView.Adapter<CampusPhoneNumberViewHolder>() {
    private lateinit var itemCampusPhoneNumberList: ArrayList<ItemCampusPhoneNumber>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampusPhoneNumberViewHolder {
        val binding = ItemTelephoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CampusPhoneNumberViewHolder(binding)
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
    fun bind(itemCampusPhoneNumber: ItemCampusPhoneNumber) {
        item.textViewItemName.text = itemCampusPhoneNumber.name
        item.textViewItemLocation.text = itemCampusPhoneNumber.location
        item.textViewItemSiteUrl.text = itemCampusPhoneNumber.siteUrl
        item.textViewItemPhoneNumber.text = itemCampusPhoneNumber.phoneNumber
    }
}