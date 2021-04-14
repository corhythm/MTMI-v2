package com.example.mtmimyeon_gitmi

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
import com.example.mtmimyeon_gitmi.item.ItemMjuSite
import kotlin.reflect.typeOf

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        val myRecyclerAdapter = MyRecyclerAdapter()
        binding.recyclerviewMainUnvInfo.apply {
            adapter = myRecyclerAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            myRecyclerAdapter.submit(itemMjuSiteList)
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null // 메모리 릭 방지
    }
}


// 원래는 파일을 나누는 게 원칙이나 가시성을 위해서 일시적으로 한 파일에서 관리
// 리사이클러뷰 어댑터
class MyRecyclerAdapter : RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var itemMjuSiteList: ArrayList<ItemMjuSite>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMjuSiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
class MyViewHolder(private val item: ItemMjuSiteBinding) : RecyclerView.ViewHolder(item.root) {
    fun bind(itemMjuSite: ItemMjuSite) {
        item.imageViewItemMjuSiteImg.setImageResource(itemMjuSite.mjuImage)
        item.textViewItemMjuText.text = itemMjuSite.mjuText
    }
}

