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


class ChattingRoomDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingRoomDetailsBinding
    private lateinit var chattingRoomDetailsRecyclerAdapter: ChattingRoomDetailsRecyclerAdapter

    private lateinit var roomId : String
    private lateinit var partnerImg: String

    private var database = Firebase.database.getReference("chat")
    private lateinit var currentUser: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChattingRoomDetailsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        //callback 으로 Chat 가져오기 이후 ChatMessage에 채팅 내용 가져오기
        init()
    }

    private fun init() {
        // DB와 auth 초기화
        val DB = DatabaseManager()
        val auth = FirebaseAuth.getInstance()


        //intent값 초기화

        roomId = intent.getStringExtra("chatId")!!
        partnerImg = intent.getStringExtra("partnerImg")!!

        //현재 유저 데이터 초기화
        DB.callUserData(auth.uid.toString(), object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                currentUser = data
            }
        })

        val chattingDataList = ArrayList<ChatMessage>()

        this.chattingRoomDetailsRecyclerAdapter =
            ChattingRoomDetailsRecyclerAdapter(chattingDataList,partnerImg)


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildAddaed@@@@@@@@@@@", snapshot.key.toString())
                val addChatMessage = snapshot.getValue<ChatMessage>()
                if (addChatMessage != null) {
                    Log.d("가져온 데이터", addChatMessage.userId)
                    Log.d("가져온데이터", addChatMessage.name)
                    Log.d("가져온데이터", addChatMessage.message)
                    chattingDataList.add(addChatMessage)
                    binding.recyclerViewActivityChattingRoomDetailsMessageList.apply {
                        adapter = chattingRoomDetailsRecyclerAdapter
                    }
                    binding.recyclerViewActivityChattingRoomDetailsMessageList.scrollToPosition(chattingRoomDetailsRecyclerAdapter.itemCount-1)

                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        database.child(roomId).child("chatting")
            .addChildEventListener(childEventListener)

        binding.recyclerViewActivityChattingRoomDetailsMessageList.apply {
            adapter = chattingRoomDetailsRecyclerAdapter
        }


        // send시 메세지 보냄
        binding.buttonActivityChattingRoomDetailsSend.setOnClickListener {
            val sendMessageContenet =
                binding.editTextActivityChattingRoomDetailsMessage.text.toString()
            if (sendMessageContenet.isNotEmpty()) {
                DB.sendMessage(
                    roomId,
                    currentUser.userName,
                    binding.editTextActivityChattingRoomDetailsMessage.text.toString(),
                    auth.currentUser!!.uid,
                    currentUser.userProfileImageUrl
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
class ChattingRoomDetailsRecyclerAdapter(private val chattingList: ArrayList<ChatMessage>,private val partnerImg: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.MY_CHATTING.viewNum) // 내가 보낸 채팅일 때
            ChattingViewHolder(
                ItemSendChattingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),partnerImg
            )
        else
            ChattingViewHolder(
                ItemReceiveChattingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),partnerImg
            )

    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChattingViewHolder).bind(chattingList[position])

    }

    override fun getItemCount(): Int {
        return this.chattingList.size
    }

    // 메시지 주인 구분
    override fun getItemViewType(position: Int): Int {
        // 여기서 firebase uid로 내가 보낸 채팅인지, 받은 채팅인지 구분
        return if (this.chattingList[position].userId == uid)
            ViewType.MY_CHATTING.viewNum
        else
            ViewType.OTHERS_CHATTING.viewNum
    }

}

class ChattingViewHolder(private val item: ViewBinding,private val partnerImg: String) : RecyclerView.ViewHolder(item.root) {

    fun bind(chattingData: ChatMessage) { // 내가 보낸 채팅
        if (item is ItemSendChattingBinding) {
            item.textViewItemSendChattingSendMsg.text = chattingData.message
            item.textViewItemSendChattingTimeStamp.text = chattingData.timeStamp

        } else { // 내가 받은 채팅
            (item as ItemReceiveChattingBinding).textViewItemReceiveChattingReceiverName.text =
                chattingData.name
            // 이미지는 글라이더로 set
            Log.d("이미지 클라이언트 ",chattingData.userId)
            FirebaseStorage.getInstance().reference.child(partnerImg).downloadUrl.addOnSuccessListener { that ->
                Glide.with(itemView.context).load(that).circleCrop()
                    .into(item.imageViewItemReceiveChattingProfileImg)
            }
            item.textViewItemReceiveChattingReceiveMsg.text = chattingData.message
            item.textViewItemReceiveChattingTimeStamp.text = chattingData.timeStamp
        }

    }
}