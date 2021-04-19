package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityMyChatListBinding
import com.example.mtmimyeon_gitmi.databinding.ItemChatBinding
import com.example.mtmimyeon_gitmi.item.ItemChat

class MyChatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyChatListBinding
    private lateinit var myChatListRecyclerAdpater: MyChatListRecyclerAdapter
    private lateinit var myChatList: ArrayList<ItemChat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        myChatList = ArrayList()
        for (i in 1..20) {
            myChatList.add(ItemChat("https://imgurl~~~", "띵무위키", "It was popularised in the 1960s with the release of Letraset sheets"))
        }
        myChatListRecyclerAdpater = MyChatListRecyclerAdapter()
        binding.recyclerviewMyChatListChatList.apply {
            adapter = myChatListRecyclerAdpater
            layoutManager = LinearLayoutManager(this@MyChatListActivity, LinearLayoutManager.VERTICAL, false)
            myChatListRecyclerAdpater.submit(myChatList)
        }
    }
}

class MyChatListRecyclerAdapter : RecyclerView.Adapter<MyChatViewHolder>() {
    private lateinit var itemChatList: ArrayList<ItemChat>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyChatViewHolder, position: Int) {
        holder.bind(itemChatList[position])
    }

    override fun getItemCount(): Int {
        return itemChatList.size
    }

    fun submit(itemChatList: ArrayList<ItemChat>) {
        this.itemChatList = itemChatList
    }

}

// 리사이클러뷰 뷰홀더
class MyChatViewHolder(private val item: ItemChatBinding) : RecyclerView.ViewHolder(item.root) {
    fun bind(itemChat: ItemChat) {
        // glide 같은 라이브러리 사용해서 이미지 로딩
        item.textViewChatUsername.text = itemChat.name
        item.textViewChatContent.text = itemChat.lastChat
    }
}