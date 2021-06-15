package com.mju.mtmi.myClass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.storage.FirebaseStorage
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
        binding.buttonMyClassSubjectBulletinBoardWritingPostComment.setOnClickListener {
            if (binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.isNotEmpty()) {
                val currentUser = firebaseAuth.currentUser!!.uid
                val comment =
                    binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.toString()
                FirebaseManager.postNewComment(
                    this.nowBoardPost.subjectCode,
                    this.nowBoardPost.subjectBoardIndex,
                    comment,
                    currentUser,
                    object : DataBaseCallback<Boolean> {
                        override fun onCallback(data: Boolean) {
                            if (data)
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
                )
            }
        }

        // 게시글 작성자에게 메시지 보내기
        binding.linearLayoutMyClassSubjectBulletinBoardDetailsChattingContainer.setOnClickListener {
            if (firebaseAuth.uid.toString() == this.nowBoardPost.writerUid) {
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
                val chattingRoomId = createNewChattingRoom(
                    firebaseAuth.currentUser!!.uid,
                    this.nowBoardPost.writerUid
                )

                Intent(this, ChattingRoomDetailsActivity::class.java).also {
                    it.putExtra("chattingRoomId", chattingRoomId)
                    it.putExtra(
                        "partnerImg",
                        "user_profile_images/IMAGE_${this.nowBoardPost.writerUid}.png"
                    )
                    startActivity(it)
                }
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
    }

    private fun createNewChattingRoom(sendUser: String, receiveUser: String): String {
        val chattingRoomId: String = if (sendUser > receiveUser) // 채팅방 구분 키 생성
            "$sendUser-$receiveUser"
        else
            "$receiveUser-$sendUser"

        // 채팅방 중복 체크
        FirebaseManager.checkRedundantChattingRoom(
            chattingRoomId,
            object : DataBaseCallback<Boolean> {
                override fun onCallback(data: Boolean) {
                    if (data) {
                        FirebaseManager.postNewChattingRoom(sendUser, receiveUser, chattingRoomId)
                    }
                }
            })
        return chattingRoomId
    }

    // 게시글 세부 내용 가져오기
    private fun getPostDetailInfo(
        BoardPost: BoardPost,
    ) {
        binding.textViewMyClassSubjectBulletinBoardDetailsUserName.text = BoardPost.writerName
        binding.textViewMyClassSubjectBulletinBoardDetailsDate.text = BoardPost.timeStamp
        binding.textViewMyClassSubjectBulletinBoardDetailsTitle.text = BoardPost.title
        binding.textViewMyClassSubjectBulletinBoardDetailsContent.text = BoardPost.content
        BoardPost.writerUid.let {
            FirebaseManager.getUserDataImageUri(it, object : DataBaseCallback<String> {
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
        FirebaseManager.getNumOfComment(boardPost.subjectCode, boardPost.subjectBoardIndex,
            object : DataBaseCallback<ArrayList<BoardComment>> {
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
            })
    }

    override fun sendMessageClicked(commentIdx: Int, profileImg: String) {
        // 댓글 단 사람들 중에 채팅 보낼 때
        if (firebaseAuth.uid.toString() == itemSubjectBulletinBoardCommentList[commentIdx].commenterUid) {
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
            val chattingRoomId = createNewChattingRoom(
                firebaseAuth.currentUser!!.uid,
                itemSubjectBulletinBoardCommentList[commentIdx].commenterUid
            )

            Intent(this, ChattingRoomDetailsActivity::class.java).also {
                it.putExtra("chattingRoomId", chattingRoomId)
                it.putExtra("partnerImg", profileImg)
                startActivity(it)
            }
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
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
        item.textViewItemSubjectBulletinBoardCommentUserName.text = boardComment.userName
        item.textViewItemSubjectBulletinBoardCommentCommentContent.text = boardComment.content
        item.textViewItemSubjectBulletinBoardCommentDate.text = boardComment.timeStamp

        // 댓글 단 유저들 프로필 이미지 불러오기
        boardComment.commenterUid.let {
            FirebaseManager.getUserDataImageUri(it, object : DataBaseCallback<String> {
                override fun onCallback(data: String) {
                    FirebaseStorage.getInstance().reference.child("user_profile_images/$data").downloadUrl.addOnSuccessListener { that ->
                        Glide.with(itemView.context).load(that).circleCrop()
                            .into(item.imageViewItemSubjectBulletinBoardCommentProfileImg)
                    }

                    // 메시지 보내기 클릭했을 때
                    item.linearLayoutItemSubjectBulletinBoardCommentChattingContainer.setOnClickListener {
                        this@SubjectBulletinBoardCommentViewHolder.SendMessageClickInterface.sendMessageClicked(
                            adapterPosition,
                            "image/$data"
                        )
                    }
                }
            })
        }
    }
}