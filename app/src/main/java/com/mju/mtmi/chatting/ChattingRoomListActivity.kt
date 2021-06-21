package com.mju.mtmi.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.mtmi.R
import com.mju.mtmi.databinding.ActivityChattingRoomListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager
import com.mju.mtmi.database.entity.ChattingRoomListForm
import com.mju.mtmi.database.entity.LastChattingMessage
import com.mju.mtmi.database.entity.UserData
import com.mju.mtmi.databinding.ItemChattingRoomBinding
import kotlin.collections.ArrayList

class ChattingRoomListActivity : AppCompatActivity(), ChattingRoomClickInterface {
    private lateinit var binding: ActivityChattingRoomListBinding
    private lateinit var chattingRoomListRecyclerAdapter: ChattingRoomListRecyclerAdapter
    private lateinit var myChattingRoomList: ArrayList<ChattingRoomListForm>
    private val currentUserIdx = FirebaseAuth.getInstance().currentUser!!.uid
    private val TAG = "로그"

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
        // 내가 참여하고 있는 채팅방 리스트 가져오기
        FirebaseManager.getMyChattingRoomList(
            userIdx = currentUserIdx,
            dataBaseCallback = object : DataBaseCallback<ArrayList<ChattingRoomListForm>> {
                override fun onCallback(data: ArrayList<ChattingRoomListForm>) {

                    // 채팅방 리스트 정보 받아오면
                    this@ChattingRoomListActivity.myChattingRoomList = data
                    this@ChattingRoomListActivity.chattingRoomListRecyclerAdapter =
                        ChattingRoomListRecyclerAdapter(
                            currentUserIdx = this@ChattingRoomListActivity.currentUserIdx,
                            itemChattingRoomRoomList = myChattingRoomList,
                            chattingRoomClickInterface = this@ChattingRoomListActivity
                        )
                    this@ChattingRoomListActivity.binding.recyclerviewActivityChattingRoomListChatList.apply {
                        adapter = this@ChattingRoomListActivity.chattingRoomListRecyclerAdapter
                        layoutManager = LinearLayoutManager(
                            this@ChattingRoomListActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                    this@ChattingRoomListActivity.chattingRoomListRecyclerAdapter.notifyDataSetChanged()
                }
            })

    }

    // 특정 채팅방을 클릭했을 때
    override fun chattingRoomClicked(
        chattingRoomIdx: String,
        receiverIdx: String,
    ) { // 채팅 방 번호 <- 이걸로 채팅방 검색(필요 시 탐색 데이터 추가할 것)
        Intent(this, ChattingRoomDetailsActivity::class.java).also {
            it.putExtra("chattingRoomIdx", chattingRoomIdx)
            it.putExtra("receiverIdx", receiverIdx)
            startActivity(it)
        }
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

class ChattingRoomListRecyclerAdapter(
    private val currentUserIdx: String,
    private val itemChattingRoomRoomList: ArrayList<ChattingRoomListForm>,
    private val chattingRoomClickInterface: ChattingRoomClickInterface,
) : RecyclerView.Adapter<ChattingRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingRoomViewHolder {
        val binding =
            ItemChattingRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChattingRoomViewHolder(
            currentUserIdx = currentUserIdx,
            item = binding,
            chattingRoomClickInterface = this.chattingRoomClickInterface
        )
    }

    override fun onBindViewHolder(holder: ChattingRoomViewHolder, position: Int) {
        holder.bind(itemChattingRoomRoomList[position])
    }

    override fun getItemCount(): Int = this.itemChattingRoomRoomList.size
}

// 리사이클러뷰 뷰홀더
class ChattingRoomViewHolder(
    private val currentUserIdx: String,
    private val item: ItemChattingRoomBinding,
    private val chattingRoomClickInterface: ChattingRoomClickInterface,
) :
    RecyclerView.ViewHolder(item.root) {

    fun bind(itemChattingRoomListForm: ChattingRoomListForm) {

        itemChattingRoomListForm.chattingParticipants?.forEach { userIdx ->
            Log.d("로그", "채팅 bind")
            // userIdx가 내가 아니면
            // 채팅방 리스트 정보 bind
            if (userIdx != currentUserIdx) {
                FirebaseManager.getUserData(
                    userIdx = userIdx,
                    dataBaseCallback = object : DataBaseCallback<UserData> {
                        override fun onCallback(data: UserData) {
                            item.textViewItemChattingRoomUserName.text = data.userName // 채팅 상대방 이름
                            item.textViewItemChattingRoomLastMessage.text =
                                (itemChattingRoomListForm.lastChattingMessage as LastChattingMessage).message
                            item.textViewItemChattingRoomTimeStamp.text =
                                itemChattingRoomListForm.lastChattingMessage.timeStamp

                            Log.d("로그", "data.userProfileImageUrl = ${data.userProfileImageUrl}")

                            // 상대방 프로필 이미지 로드
                            FirebaseStorage.getInstance().reference
                                .child("user_profile_images/${data.userProfileImageUrl}").downloadUrl.addOnSuccessListener { opponentProfileImageUrl ->
                                    try {
                                        Log.d("로그", "프로필: $opponentProfileImageUrl")
                                        Glide.with(itemView.context)
                                            .load(opponentProfileImageUrl)
                                            .circleCrop()
                                            .into(item.imageViewItemChattingRoomProfileImg)
                                    } catch (exception: Exception) {
                                        Log.d(
                                            "로그",
                                            "ChattingRoomViewHolder -bind() called / ${exception.stackTrace}"
                                        )
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("로그", "프로필 이미지 로드 exception = $exception")
                                }


                            item.root.setOnClickListener {
                                this@ChattingRoomViewHolder.chattingRoomClickInterface.chattingRoomClicked(
                                    chattingRoomIdx = itemChattingRoomListForm.chattingRoomIdx,
                                    receiverIdx = userIdx
                                )
                            }

                        }
                    })
                return@forEach
            }
        }
    }
}

interface ChattingRoomClickInterface {
    fun chattingRoomClicked(chattingRoomIdx: String, receiverIdx: String)
}
