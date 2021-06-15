package com.mju.mtmi.chatting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.mju.mtmi.R
import com.mju.mtmi.databinding.ActivityChattingRoomDetailsBinding
import com.mju.mtmi.databinding.ItemReceiveChattingBinding
import com.mju.mtmi.databinding.ItemSendChattingBinding
import com.mju.mtmi.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.database.entity.ChattingMessage
import com.mju.mtmi.database.entity.UserData


class ChattingRoomDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingRoomDetailsBinding
    private lateinit var chattingRoomDetailsRecyclerAdapter: ChattingRoomDetailsRecyclerAdapter
    private lateinit var chattingRoomId: String
    private lateinit var partnerImg: String
    private var database = Firebase.database.getReference(FirebaseManager.CHATTING_ROOM_PATH)
    private lateinit var currentUserData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChattingRoomDetailsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        init()
    }

    private fun init() {
        // DB와 auth 초기화
        val auth = FirebaseAuth.getInstance()

        this.chattingRoomId = intent.getStringExtra("chattingRoomId")!!
        this.partnerImg = intent.getStringExtra("partnerImg")!!

        //현재 유저 데이터 초기화
        FirebaseManager.getUserData(auth.uid.toString(), object : DataBaseCallback<UserData> {
            override fun onCallback(data: UserData) {
                currentUserData = data
            }
        })

        val chattingDataList = ArrayList<ChattingMessage>()

        this.chattingRoomDetailsRecyclerAdapter =
            ChattingRoomDetailsRecyclerAdapter(chattingDataList, partnerImg)


        // 채팅 메시지 보내거나 올 때마다 리사이클러뷰 업데이트
        database.child(this.chattingRoomId).child(FirebaseManager.CHATTING_MESSAGE_PATH)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val addChattingMessage = snapshot.getValue<ChattingMessage>()
                    if (addChattingMessage != null) {
                        chattingDataList.add(addChattingMessage)
                        binding.recyclerViewActivityChattingRoomDetailsMessageList.apply {
                            adapter = chattingRoomDetailsRecyclerAdapter
                        }
                        binding.recyclerViewActivityChattingRoomDetailsMessageList.scrollToPosition(
                            chattingRoomDetailsRecyclerAdapter.itemCount - 1
                        )
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })


        binding.recyclerViewActivityChattingRoomDetailsMessageList.apply {
            adapter = chattingRoomDetailsRecyclerAdapter
        }

        // 메세지 보내기
        binding.buttonActivityChattingRoomDetailsSend.setOnClickListener {
            if (binding.editTextActivityChattingRoomDetailsMessage.text.toString()
                    .isNotEmpty()
            ) { // 메시지 내용이 빈 문자열이 아닐 경우
                FirebaseManager.postNewChattingMessage(
                    chattingRoomId,
                    currentUserData.userName,
                    binding.editTextActivityChattingRoomDetailsMessage.text.toString(),
                    auth.currentUser!!.uid,
                    currentUserData.userProfileImageUrl
                )
                binding.editTextActivityChattingRoomDetailsMessage.text.clear()
            }
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

// ViewHolder 타입 구분 enum class
enum class ViewType(val viewNum: Int) {
    MY_CHATTING(0), OTHERS_CHATTING(1)
}

// recyclerview adapter
class ChattingRoomDetailsRecyclerAdapter(
    private val chattingList: ArrayList<ChattingMessage>,
    private val partnerImg: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.MY_CHATTING.viewNum) // 내가 보낸 채팅일 때
            ChattingViewHolder(
                ItemSendChattingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), partnerImg
            )
        else
            ChattingViewHolder(
                ItemReceiveChattingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), partnerImg
            )

    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChattingViewHolder).bind(chattingList[position])
    }

    override fun getItemCount(): Int = this.chattingList.size

    // 메시지 주인 구분
    override fun getItemViewType(position: Int): Int {
        // 여기서 firebase uid로 내가 보낸 채팅인지, 받은 채팅인지 구분
        return if (this.chattingList[position].userId == this.currentUserUid)
            ViewType.MY_CHATTING.viewNum
        else
            ViewType.OTHERS_CHATTING.viewNum
    }

}

class ChattingViewHolder(private val item: ViewBinding, private val partnerImg: String) :
    RecyclerView.ViewHolder(item.root) {
    fun bind(chattingData: ChattingMessage) { // 내가 보낸 채팅
        if (item is ItemSendChattingBinding) {
            item.textViewItemSendChattingSendMsg.text = chattingData.message
            item.textViewItemSendChattingTimeStamp.text = chattingData.timeStamp
        } else { // 내가 받은 채팅
            Log.d("로그", "ChattingViewHolder -bind() called / partnerImg = ${this.partnerImg}")
            (item as ItemReceiveChattingBinding).textViewItemReceiveChattingReceiverName.text =
                chattingData.name
            FirebaseStorage.getInstance().reference.child(partnerImg).downloadUrl.addOnSuccessListener { that ->
                Glide.with(itemView.context).load(that).circleCrop()
                    .into(item.imageViewItemReceiveChattingProfileImg)
            }
            item.textViewItemReceiveChattingReceiveMsg.text = chattingData.message
            item.textViewItemReceiveChattingTimeStamp.text = chattingData.timeStamp
        }
    }
}