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
import com.mju.mtmi.database.entity.ChattingMessage
import com.mju.mtmi.database.entity.ChattingRoomListForm
import com.mju.mtmi.database.entity.LastChatting
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ChattingRoomListActivity : AppCompatActivity(), ChattingRoomClickInterface {
    private lateinit var binding: ActivityChattingRoomListBinding
//    private lateinit var chattingRoomListRecyclerAdapter: ChattingRoomListRecyclerAdapter
    private val myChattingRoomList = ArrayList<ChattingRoomListForm>()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        init()
        super.onResume()
    }

    private fun init() {
        this.myChattingRoomList.clear()

//        FirebaseManager.getMyChattingRoomList(
//            userId = firebaseAuth.currentUser!!.uid,
//            dataBaseCallback = object : DataBaseCallback<ArrayList<ChattingRoomListForm>> {
//                override fun onCallback(data: ArrayList<ChattingRoomListForm>) {
//                    for (i in 0 until data.count()) {
//                        FirebaseManager.getLastChattingMessage(
//                            data[i].chattingRoomId,
//                            object : DataBaseCallback<LastChatting> {
//                                override fun onCallback(data: LastChatting) {
//                                    myChattingRoomList.add(data)
//                                    binding.recyclerviewActivityChattingRoomListChatList.apply {
//                                        adapter = chattingRoomListRecyclerAdapter
//                                        layoutManager =
//                                            LinearLayoutManager(
//                                                this@ChattingRoomListActivity,
//                                                LinearLayoutManager.VERTICAL,
//                                                false
//                                            )
//                                    }
//                                }
//                            })
//                    }
//
//                }
//            })
//
//        chattingRoomListRecyclerAdapter =
//            ChattingRoomListRecyclerAdapter(myChattingRoomList, this)
//
//        FirebaseManager.getMyChattingRoomList(
//            firebaseAuth.currentUser!!.uid,
//            object : DataBaseCallback<ArrayList<ChattingRoomListForm>> {
//                override fun onCallback(data: ArrayList<ChattingRoomListForm>) {
//                    Log.d("로그", "chattingRoomForm: $data ")
//                }
//            })
    }

    // 특정 채팅방을 클릭했을 때
    override fun chattingRoomClicked(
        chattingRoomIdx: String,
        imageUrl: String,
    ) { // 채팅 방 번호 <- 이걸로 채팅방 검색(필요 시 탐색 데이터 추가할 것)
        Intent(this, ChattingRoomDetailsActivity::class.java).also {
            it.putExtra("chattingRoomId", chattingRoomIdx)
            it.putExtra("partnerImg", imageUrl)
            startActivity(it)
        }
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

//class ChattingRoomListRecyclerAdapter(
//    private val itemChattingRoomRoomList: ArrayList<ChattingRoomListForm>,
//    private val chattingRoomClickInterface: ChattingRoomClickInterface,
//) : RecyclerView.Adapter<ChattingRoomViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingRoomViewHolder {
//        val binding =
//            ItemChattingRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ChattingRoomViewHolder(binding, this.chattingRoomClickInterface)
//    }
//
//    override fun onBindViewHolder(holder: ChattingRoomViewHolder, position: Int) {
//        holder.bind(itemChattingRoomRoomList[position])
//    }
//
//    override fun getItemCount(): Int = this.itemChattingRoomRoomList.size
//}
//
//// 리사이클러뷰 뷰홀더
//class ChattingRoomViewHolder(
//    private val item: ItemChattingRoomBinding,
//    private val ChattingRoomClickInterface: ChattingRoomClickInterface,
//) :
//    RecyclerView.ViewHolder(item.root) {
//
//    fun bind(itemChattingRoomRoom: ChattingRoomListForm) {
//        // glide 같은 라이브러리 사용해서 이미지 로딩
//        item.textViewItemChattingRoomUserName.text = itemChattingRoomRoom.opponentUserName
//        item.textViewItemChattingRoomLastMessage.text = itemChattingRoomRoom.lastChatting
//        item.textViewItemChattingRoomTimeStamp.text = itemChattingRoomRoom.timeStamp
//        FirebaseStorage.getInstance().reference.child(itemChattingRoomRoom.opponentUserProfileImageUrl).downloadUrl.addOnSuccessListener { that ->
//            try {
//                Glide.with(itemView.context).load(that).circleCrop()
//                    .into(item.imageViewItemChattingRoomProfileImg)
//            } catch (exception: Exception) {
//                Log.d("로그", "ChattingRoomViewHolder -bind() called / ${exception.stackTrace}")
//            }
//        }
//
//        item.root.setOnClickListener {
//            this.ChattingRoomClickInterface.chattingRoomClicked(
//                itemChattingRoomRoom.chattingRoomId,
//                itemChattingRoomRoom.opponentUserProfileImageUrl
//            )
//        }
//    }
//}

interface ChattingRoomClickInterface {
    fun chattingRoomClicked(chattingRoomIdx: String, imageUrl: String)
}
