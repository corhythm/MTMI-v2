package com.example.mtmimyeon_gitmi.chatting

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityChattingRoomListBinding
import com.example.mtmimyeon_gitmi.databinding.ItemChattingRoomBinding

class ChattingRoomListActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChattingRoomListBinding
    private lateinit var chattingRoomRecyclerAdpater: ChattingRoomRecyclerAdapter
    private lateinit var myChattingRoomList: ArrayList<ItemChattingRoom>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        myChattingRoomList = ArrayList()
        for (i in 1..20) {
            myChattingRoomList.add(
                ItemChattingRoom(
                    "https://imgurl~~~",
                    "라이인드로스테쭈젠댄마리소피아수인레나테엘리자벳피아루이제",
                    "It was popularised in the 1960s with the release of Letraset sheets",
                    "2021-05-21 화"
                )
            )
        }
        chattingRoomRecyclerAdpater = ChattingRoomRecyclerAdapter()
        binding.recyclerviewMyChatListChatList.apply {
            adapter = chattingRoomRecyclerAdpater
            layoutManager =
                LinearLayoutManager(this@ChattingRoomListActivity, LinearLayoutManager.VERTICAL, false)
            chattingRoomRecyclerAdpater.submit(myChattingRoomList)
        }
    }
}

data class ItemChattingRoom(
    val imgUrl: String,
    val name: String,
    val lastChat: String,
    val timeStamp: String
)

class ChattingRoomRecyclerAdapter : RecyclerView.Adapter<ChattingRoomViewHolder>() {
    private lateinit var itemChattingRoomList: ArrayList<ItemChattingRoom>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingRoomViewHolder {
        val binding =
            ItemChattingRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChattingRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChattingRoomViewHolder, position: Int) {
        holder.bind(itemChattingRoomList[position])
    }

    override fun getItemCount(): Int {
        return itemChattingRoomList.size
    }

    fun submit(itemChattingRoomList: ArrayList<ItemChattingRoom>) {
        this.itemChattingRoomList = itemChattingRoomList
    }

}

// 리사이클러뷰 뷰홀더
class ChattingRoomViewHolder(private val item: ItemChattingRoomBinding) :
    RecyclerView.ViewHolder(item.root) {
    fun bind(itemChattingRoom: ItemChattingRoom) {
        // glide 같은 라이브러리 사용해서 이미지 로딩
        item.textViewItemChattingRoomUserName.text = itemChattingRoom.name
        item.textViewItemChattingRoomLastMessage.text = itemChattingRoom.lastChat
        item.textViewItemChattingRoomTimeStamp.text = itemChattingRoom.timeStamp
    }
}
