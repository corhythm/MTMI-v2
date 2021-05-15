package com.example.mtmimyeon_gitmi.chatting

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityChattingRoomListBinding
import com.example.mtmimyeon_gitmi.databinding.ItemChattingRoomBinding

class ChattingRoomListActivity : AppCompatActivity(), ChattingRoomClickInterface {
    private lateinit var binding: ActivityChattingRoomListBinding
    private lateinit var chattingRoomListRecyclerAdapter: ChattingRoomListRecyclerAdapter
    private lateinit var myChattingRoomList: ArrayList<ItemChattingRoom>

    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) { // activity 옆으로 이동 애니메이션
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            // set an slide transition
            enterTransition = Slide(Gravity.END)
            exitTransition = Slide(Gravity.START)
        }

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
                    imgUrl = "https://imgurl~~~",
                    name = "라이인드로스테쭈젠댄마리소피아수인레나테엘리자벳피아루이제",
                    lastChat = "It was popularised in the 1960s with the release of Letraset sheets",
                    timeStamp = "2021-05-21 화"
                )
            )
        }
        this.chattingRoomListRecyclerAdapter =
            ChattingRoomListRecyclerAdapter(myChattingRoomList, this)
        binding.recyclerviewMyChatListChatList.apply {
            adapter = chattingRoomListRecyclerAdapter
            layoutManager =
                LinearLayoutManager(
                    this@ChattingRoomListActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
        }
    }

    // 특정 채팅방을 클릭했을 때
    override fun chattingRoomClicked(chattingRoomIdx: String) { // 채팅 방 번호 <- 이걸로 채팅방 검색(필요 시 탐색 데이터 추가할 것)
        Intent(this, ChattingRoomDetailsActivity::class.java).also {
            startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}

data class ItemChattingRoom(
    var chattingRoomIdx: String = "",
    val imgUrl: String,
    val name: String,
    val lastChat: String,
    val timeStamp: String
)

class ChattingRoomListRecyclerAdapter(
    private val itemChattingRoomList: ArrayList<ItemChattingRoom>,
    private val chattingRoomClickInterface: ChattingRoomClickInterface
) : RecyclerView.Adapter<ChattingRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingRoomViewHolder {
        val binding =
            ItemChattingRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChattingRoomViewHolder(binding, this.chattingRoomClickInterface)
    }

    override fun onBindViewHolder(holder: ChattingRoomViewHolder, position: Int) {
        holder.bind(itemChattingRoomList[position])
    }

    override fun getItemCount(): Int {
        return itemChattingRoomList.size
    }


}

// 리사이클러뷰 뷰홀더
class ChattingRoomViewHolder(
    private val item: ItemChattingRoomBinding,
    private val ChattingRoomClickInterface: ChattingRoomClickInterface
) :
    RecyclerView.ViewHolder(item.root) {

    fun bind(itemChattingRoom: ItemChattingRoom) {
        // glide 같은 라이브러리 사용해서 이미지 로딩
        item.textViewItemChattingRoomUserName.text = itemChattingRoom.name
        item.textViewItemChattingRoomLastMessage.text = itemChattingRoom.lastChat
        item.textViewItemChattingRoomTimeStamp.text = itemChattingRoom.timeStamp

        item.root.setOnClickListener {
            this.ChattingRoomClickInterface.chattingRoomClicked(itemChattingRoom.chattingRoomIdx)
        }
    }
}

interface ChattingRoomClickInterface {
    fun chattingRoomClicked(chattingRoomIdx: String)
}