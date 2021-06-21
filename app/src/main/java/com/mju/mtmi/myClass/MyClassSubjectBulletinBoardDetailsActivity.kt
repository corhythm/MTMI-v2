package com.mju.mtmi.myClass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.mtmi.R
import com.mju.mtmi.chatting.ChattingRoomDetailsActivity
import com.mju.mtmi.databinding.ActivityMyClassSubjectBulletinBoardDetailsBinding
import com.mju.mtmi.databinding.ItemSubjectBulletinBoardCommentBinding
import com.mju.mtmi.database.entity.BoardComment
import com.mju.mtmi.database.entity.BoardPost
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.database.entity.UserData
import www.sanju.motiontoast.MotionToast

// 특정 게시글 보기
class MyClassSubjectBulletinBoardDetailsActivity : AppCompatActivity(), SendMessageClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardDetailsBinding
    private val itemSubjectBulletinBoardCommentList = ArrayList<BoardComment>()
    private lateinit var subjectBulletinBoardCommentRecyclerAdapter: SubjectBulletinBoardCommentRecyclerAdapter
    private lateinit var nowBoardPost: BoardPost
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        this.nowBoardPost = intent.getSerializableExtra("boardPost") as BoardPost // 게시판 기본 정보 가져오기

        getPostDetailInfo(this.nowBoardPost) // 게시글 세부 내용 가져오기
        getCommentList(this.nowBoardPost) // 게시글에 달린 댓글 가져오기
        this.subjectBulletinBoardCommentRecyclerAdapter =
            SubjectBulletinBoardCommentRecyclerAdapter(
                this.itemSubjectBulletinBoardCommentList,
                this
            )

        // 댓글 작성
        binding.buttonMyClassSubjectBulletinBoardWritingPostComment.setOnClickListener { // 댓글 입력 버튼 클릭 시
            if (binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.isNotEmpty()) { // 댓글이 빈 문자열이 아니면
                val writerIdx = firebaseAuth.currentUser!!.uid
                val commentContent =
                    binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.toString()

                FirebaseManager.postNewComment(
                    boardIdx = this.nowBoardPost.boardIdx,
                    postIdx = this.nowBoardPost.postIdx,
                    commentContent = commentContent,
                    writerIdx = writerIdx,
                    dataBaseCallback = object : DataBaseCallback<Boolean> {
                        override fun onCallback(data: Boolean) {
                            if (data) {
                                FirebaseManager.patchCommentCount(this@MyClassSubjectBulletinBoardDetailsActivity.nowBoardPost)

                                // 코멘트 갱신화작업
                                subjectBulletinBoardCommentRecyclerAdapter.notifyItemRangeRemoved(
                                    0,
                                    itemSubjectBulletinBoardCommentList.size - 1
                                )
                                subjectBulletinBoardCommentRecyclerAdapter.notifyDataSetChanged()
                                itemSubjectBulletinBoardCommentList.clear()
                                getCommentList(this@MyClassSubjectBulletinBoardDetailsActivity.nowBoardPost)
                                binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.clear()
                            }
                        }
                    }
                )

            }
        }

        // 게시글 작성자에게 메시지 보내기 클릭 시 -> 채팅방 개설
        binding.linearLayoutMyClassSubjectBulletinBoardDetailsChattingContainer.setOnClickListener {
            // 게시글 작성한 사람이 본인일 경우
            if (firebaseAuth.uid.toString() == this.nowBoardPost.writerIdx) {
                MotionToast.createColorToast(
                    this,
                    "Error",
                    "본인에게 메시지를 보낼 수 없습니다",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, R.font.maple_story_bold)
                )
            } else {
                val myIdx = firebaseAuth.currentUser!!.uid
                val receiverIdx = this.nowBoardPost.writerIdx
                val chattingRoomIdx = createNewChattingRoom(myIdx = myIdx, receiverIdx = receiverIdx)

                Intent(this, ChattingRoomDetailsActivity::class.java).also {
                    it.putExtra("chattingRoomIdx", chattingRoomIdx)
                    it.putExtra("receiverIdx", receiverIdx)
                    startActivity(it)
                }
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
    }

    // 댓글 단 사람들 중에 채팅 보낼 때
    override fun sendMessageClicked(commentIdx: Int, profileImg: String) {
        // 댓글 단 사람이 본인일 경우
        if (firebaseAuth.uid.toString() == itemSubjectBulletinBoardCommentList[commentIdx].writerIdx) {
            MotionToast.createColorToast(
                this,
                "Error",
                "본인에게 메시지를 보낼 수 없습니다",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this, R.font.maple_story_bold)
            )
        } else {
            val myIdx = firebaseAuth.currentUser!!.uid // 내 uid
            val receiverIdx = itemSubjectBulletinBoardCommentList[commentIdx].writerIdx // 상대방 uid
            val chattingRoomIdx = createNewChattingRoom(myIdx = myIdx, receiverIdx = receiverIdx) // 나와 상대방이 대화할 채팅방 id

            Intent(this, ChattingRoomDetailsActivity::class.java).also {
                it.putExtra("chattingRoomIdx", chattingRoomIdx)
                it.putExtra("receiverIdx", receiverIdx)
                startActivity(it)
            }
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
    }

    private fun createNewChattingRoom(myIdx: String, receiverIdx: String): String {
        val chattingRoomIdx: String = if (myIdx > receiverIdx) // 채팅방 구분 키 생성
            "$myIdx-$receiverIdx"
        else
            "$receiverIdx-$myIdx"

        // 채팅방 중복 체크
        FirebaseManager.checkRedundantChattingRoom(
            chattingRoomIdx = chattingRoomIdx,
            dataBaseCallback = object : DataBaseCallback<Boolean> {
                override fun onCallback(data: Boolean) {
                    if (data) {
                        FirebaseManager.postNewChattingRoom(myIdx, receiverIdx, chattingRoomIdx)
                    }
                }
            })
        return chattingRoomIdx
    }

    // 게시글 세부 내용 가져오기
    private fun getPostDetailInfo(
        boardPost: BoardPost,
    ) {
        FirebaseManager.getUserData(
            userIdx = boardPost.writerIdx,
            dataBaseCallback = object : DataBaseCallback<UserData> { // userIdx를 이용해 user 이름 받아오기
                override fun onCallback(data: UserData) {
                    binding.textViewMyClassSubjectBulletinBoardDetailsUserName.text = data.userName
                    binding.textViewMyClassSubjectBulletinBoardDetailsDate.text = boardPost.timeStamp
                    binding.textViewMyClassSubjectBulletinBoardDetailsTitle.text = boardPost.title
                    binding.textViewMyClassSubjectBulletinBoardDetailsContent.text = boardPost.content
                }
            })

        boardPost.writerIdx.let { userIdx ->
            FirebaseManager.getUserDataImageUri(
                userIdx = userIdx,
                dataBaseCallback = object : DataBaseCallback<String> {
                    override fun onCallback(data: String) {
                        FirebaseStorage.getInstance().reference.child("user_profile_images/$data").downloadUrl.addOnSuccessListener { imageUrl ->
                            Glide.with(applicationContext).load(imageUrl).circleCrop()
                                .into(binding.imageViewMyClassSubjectBulletinBoardDetailsProfileImg)
                        }
                    }
                })
        }
    }

    private fun getCommentList(boardPost: BoardPost) {
        FirebaseManager.getNumOfComment(boardIdx = boardPost.boardIdx,
            postIdx = boardPost.postIdx,
            dataBaseCallback = object : DataBaseCallback<ArrayList<BoardComment>> {
                override fun onCallback(data: ArrayList<BoardComment>) {
                    for (i in 0 until data.count())
                        itemSubjectBulletinBoardCommentList.add(data[i])
                    binding.recyclerviewMyClassSubjectBulletinBoardCommentList.apply {
                        adapter = subjectBulletinBoardCommentRecyclerAdapter
                        layoutManager = LinearLayoutManager(
                            this@MyClassSubjectBulletinBoardDetailsActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        addItemDecoration(SubjectRecyclerDecoration())
                    }
                }
            }
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

interface SendMessageClickInterface { // 댓글 단 사람들과 채팅
    fun sendMessageClicked(commentIdx: Int, profileImg: String)
}

class SubjectBulletinBoardCommentRecyclerAdapter(
    private val itemSubjectBulletinBoardCommentList: ArrayList<BoardComment>,
    private val SendMessageClickInterface: SendMessageClickInterface,
) : RecyclerView.Adapter<SubjectBulletinBoardCommentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SubjectBulletinBoardCommentViewHolder {
        val binding = ItemSubjectBulletinBoardCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubjectBulletinBoardCommentViewHolder(binding, this.SendMessageClickInterface)
    }

    override fun onBindViewHolder(holder: SubjectBulletinBoardCommentViewHolder, position: Int) {
        holder.bind(itemSubjectBulletinBoardCommentList[position])
    }

    override fun getItemCount(): Int = itemSubjectBulletinBoardCommentList.size
}

// recyclerview viewHolder
class SubjectBulletinBoardCommentViewHolder(
    private val item: ItemSubjectBulletinBoardCommentBinding,
    private val SendMessageClickInterface: SendMessageClickInterface,
) : RecyclerView.ViewHolder(item.root) {

    fun bind(boardComment: BoardComment) {

        var userData: UserData?

        // get UserData using UserUid
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(boardComment.writerIdx)
            .get()
            .addOnSuccessListener { document ->
                userData = document.toObject(UserData::class.java)!!
                Log.d("로그", "댓글 사용자 데이터 = $userData")

                item.textViewItemSubjectBulletinBoardCommentUserName.text = userData!!.userName
                item.textViewItemSubjectBulletinBoardCommentCommentContent.text =
                    boardComment.content
                item.textViewItemSubjectBulletinBoardCommentDate.text = boardComment.timeStamp
            }
            .addOnFailureListener { exception ->
                Log.d("로그", "exception = $exception")
            }


        // 댓글 단 유저들 프로필 이미지 불러오기
        FirebaseManager.getUserDataImageUri(
            userIdx = boardComment.writerIdx,
            dataBaseCallback = object : DataBaseCallback<String> {
                override fun onCallback(data: String) {
                    FirebaseStorage.getInstance().reference.child("user_profile_images/$data").downloadUrl.addOnSuccessListener { profileImageUrl ->
                        Glide.with(itemView.context).load(profileImageUrl).circleCrop()
                            .into(item.imageViewItemSubjectBulletinBoardCommentProfileImg)
                    }

                    // 메시지 보내기 클릭했을 때
                    item.linearLayoutItemSubjectBulletinBoardCommentChattingContainer.setOnClickListener {
                        this@SubjectBulletinBoardCommentViewHolder.SendMessageClickInterface.sendMessageClicked(
                            this@SubjectBulletinBoardCommentViewHolder.adapterPosition,
                            "image/$data"
                        )
                    }
                }
            })
    }
}