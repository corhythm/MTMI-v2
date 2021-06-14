package com.mju.mtmi.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.mtmi.R
import com.mju.mtmi.databinding.ActivityChattingRoomListBinding
import com.mju.mtmi.databinding.ItemChattingRoomBinding
import com.mju.mtmi.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.database.entity.Chat
import com.mju.mtmi.database.entity.ChatListForm
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ChattingRoomListActivity : AppCompatActivity(), ChattingRoomClickInterface {
    private lateinit var binding: ActivityChattingRoomListBinding
    private lateinit var chattingRoomListRecyclerAdapter: ChattingRoomListRecyclerAdapter
    private var myChattingRoomList = ArrayList<ChatListForm>()
    var firebase: FirebaseManager = FirebaseManager()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun init() {
        this.myChattingRoomList.clear()
        firebase.loadChatList(auth.currentUser!!.uid, object : DataBaseCallback<ArrayList<Chat>> {
            override fun onCallback(data: ArrayList<Chat>) {
                for (i in 0 until data.count()) {
                    firebase.loadLastChat(data[i].chatRoomId, object : DataBaseCallback<ChatListForm> {
                        override fun onCallback(data: ChatListForm) {
                            myChattingRoomList.add(data)
                            Log.d("data", data.timeStamp)
                            binding.recyclerviewActivityChattingRoomListChatList.apply {
                                adapter = chattingRoomListRecyclerAdapter
                                layoutManager =
                                    LinearLayoutManager(
                                        this@ChattingRoomListActivity,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                            }
                        }
                    })
                }

            }
        })

        chattingRoomListRecyclerAdapter =
            ChattingRoomListRecyclerAdapter(myChattingRoomList, this)
    }

    override fun onResume() {
        init()
        super.onResume()
    }

    // 특정 채팅방을 클릭했을 때
    override fun chattingRoomClicked(chattingRoomIdx: String, imageUrl: String, ) { // 채팅 방 번호 <- 이걸로 채팅방 검색(필요 시 탐색 데이터 추가할 것)
        Intent(this, ChattingRoomDetailsActivity::class.java).also {
            it.putExtra("chatId", chattingRoomIdx).putExtra("partnerImg", imageUrl)
            startActivity(it)
        }
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

//data class ItemChattingRoom(
//    var chattingRoomIdx: String = "",
//    val imgUrl: String,
//    val name: String,
//    val lastChat: String,
//    val timeStamp: String
//)

class ChattingRoomListRecyclerAdapter(
    private val itemChattingRoomList: ArrayList<ChatListForm>,
    private val chattingRoomClickInterface: ChattingRoomClickInterface,
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
    private val ChattingRoomClickInterface: ChattingRoomClickInterface,
) :
    RecyclerView.ViewHolder(item.root) {

    fun bind(itemChattingRoom: ChatListForm) {
        // glide 같은 라이브러리 사용해서 이미지 로딩
        item.textViewItemChattingRoomUserName.text = itemChattingRoom.name
        item.textViewItemChattingRoomLastMessage.text = itemChattingRoom.lastChat
        item.textViewItemChattingRoomTimeStamp.text = itemChattingRoom.timeStamp
        FirebaseStorage.getInstance().reference.child(itemChattingRoom.imgUrl).downloadUrl.addOnSuccessListener { that ->
            try {
                Glide.with(itemView.context).load(that).circleCrop()
                    .into(item.imageViewItemChattingRoomProfileImg)
            } catch (exception: Exception) {
                Log.d("팅김", "ChattingRoomViewHolder -bind() called / 팅김")
            }

        }

        item.root.setOnClickListener {
            Log.d("채팅방 아이디 확인", itemChattingRoom.chatRoomId)
            this.ChattingRoomClickInterface.chattingRoomClicked(itemChattingRoom.chatRoomId,
                itemChattingRoom.imgUrl)
        }
    }
}

interface ChattingRoomClickInterface {
    fun chattingRoomClicked(chattingRoomIdx: String, imgurl: String)
}
