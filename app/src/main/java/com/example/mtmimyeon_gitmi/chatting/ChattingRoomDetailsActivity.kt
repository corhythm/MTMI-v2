package com.example.mtmimyeon_gitmi.chatting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityChattingRoomDetailsBinding
import com.example.mtmimyeon_gitmi.databinding.ItemReceiveChattingBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSendChattingBinding
import com.example.mtmimyeon_gitmi.db.Chat
import com.example.mtmimyeon_gitmi.db.ChatMessage
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ChattingRoomDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingRoomDetailsBinding
    private lateinit var chattingRoomDetailsRecyclerAdapter: ChattingRoomDetailsRecyclerAdapter
    private var roomId = "CXG1SrIoS4Mn96vTLqsWPnnUUwO2-1234"
    private var database = Firebase.database.getReference("chat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChattingRoomDetailsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        //callback 으로 Chat 가져오기 이후 ChatMessage에 채팅 내용 가져오기
        init()

    }

    private fun init() {
<<<<<<< HEAD
        var DB = DatabaseManager()
        var auth = FirebaseAuth.getInstance()
        val chattingDataList = ArrayList<ChatMessage>()
=======
        val chattingDataList = ArrayList<ChattingData>()
>>>>>>> f1edf149101ad1cf37d28439ad267db91b98de02
        this.chattingRoomDetailsRecyclerAdapter =
            ChattingRoomDetailsRecyclerAdapter(chattingDataList)
        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildAddaed@@@@@@@@@@@",snapshot.getKey().toString())
                val addChatMessage = snapshot.getValue<ChatMessage>()
                if (addChatMessage != null) {
                    Log.d("가져온 데이터" , addChatMessage.userId)
                    Log.d("가져온데이터", addChatMessage.name)
                    Log.d("가져온데이터", addChatMessage.message)
                    chattingDataList.add(addChatMessage)
                    binding.recyclerViewActivityChattingRoomMessageList.apply {
                        adapter = chattingRoomDetailsRecyclerAdapter
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildChanged@@@@@@@@@@",snapshot.getKey().toString())
                val addChatMessage = snapshot.getValue(ChatMessage::class.java)
                if (addChatMessage != null) {
                    chattingDataList.add(addChatMessage)
                    binding.recyclerViewActivityChattingRoomMessageList.apply {
                        adapter = chattingRoomDetailsRecyclerAdapter
                    }
                }
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

<<<<<<< HEAD
        }
        database.child("CXG1SrIoS4Mn96vTLqsWPnnUUwO2-1234").child("chatting").addChildEventListener(childEventListener)

        binding.recyclerViewActivityChattingRoomMessageList.apply {
=======
        // insert test data
        chattingDataList.add(
            ChattingData(
                "tempUser",
                "강성욱",
                "w",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "tempUser",
                "강성욱",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "other user",
                "강성욱",
                "Helloworld",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "other user",
                "강성욱",
                "g",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "other user",
                "강성욱",
                "Helloworld",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "other user",
                "강성욱",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "tempUser",
                "강성욱",
                "Helloworld",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "other user",
                "강성욱",
                "Helloworld",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )
        chattingDataList.add(
            ChattingData(
                "other user",
                "강성욱",
                "Helloworld",
                imgUrl = "",
                timeStamp = "2021-03-21 화"
            )
        )

        binding.recyclerViewActivityChattingRoomDetailsMessageList.apply {
>>>>>>> f1edf149101ad1cf37d28439ad267db91b98de02
            adapter = chattingRoomDetailsRecyclerAdapter
        }
        binding.buttonActivityChattingRoomSend.setOnClickListener {
            var sendMessageContenet = binding.editTextActivityChattingRoomMessage.text.toString()
            if(sendMessageContenet.isNotEmpty()){
                DB.sendMessage(
                    roomId,
                    "정현",
                    binding.editTextActivityChattingRoomMessage.text.toString(),
                    auth.currentUser.uid,
                    ""
                )
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

// recyclerView item
data class ChattingData(
    val uid: String,
    val name: String, // 이름
    val message: String, // 채팅 메시지
    var imgUrl: String = "", // 프로필 이미지 url
    val timeStamp: String // 채팅 시간
)

// ViewHolder 타입 구분 enum class
enum class ViewType(val viewNum: Int) {
    MY_CHATTING(0), OTHERS_CHATTING(1)
}

// recyclerview adapter
class ChattingRoomDetailsRecyclerAdapter(private val chattingList: ArrayList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser.uid// 임시 유저, 유저 구별 id (나중에 firebase에서 받아올 것)
    private val receiceUser = "1234"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.MY_CHATTING.viewNum) // 내가 보낸 채팅일 때
            ChattingViewHolder(
                ItemSendChattingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        else
            ChattingViewHolder(
                ItemReceiveChattingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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

class ChattingViewHolder(private val item: ViewBinding) : RecyclerView.ViewHolder(item.root) {

    fun bind(chattingData: ChatMessage) { // 내가 보낸 채팅
        if (item is ItemSendChattingBinding) {
            item.textViewItemSendChattingSendMsg.text = chattingData.message
            item.textViewItemSendChattingTimeStamp.text = chattingData.timeStamp
        } else { // 내가 받은 채팅
            (item as ItemReceiveChattingBinding).textViewItemReceiveChattingReceiverName.text =
                chattingData.name
            // 이미지는 글라이더로 set
            item.textViewItemReceiveChattingReceiveMsg.text = chattingData.message
            item.textViewItemReceiveChattingTimeStamp.text = chattingData.timeStamp
        }
    }
}



