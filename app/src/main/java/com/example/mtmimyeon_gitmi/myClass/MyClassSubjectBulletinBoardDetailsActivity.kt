package com.example.mtmimyeon_gitmi.myClass

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.chatting.ChattingRoomDetailsActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardDetailsBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBulletinBoardCommentBinding
import com.example.mtmimyeon_gitmi.db.BoardComment
import com.example.mtmimyeon_gitmi.db.BoardPost
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import www.sanju.motiontoast.MotionToast

class MyClassSubjectBulletinBoardDetailsActivity : AppCompatActivity(), sendMessageClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardDetailsBinding
    private val itemSubjectBulletinBoardCommentList = ArrayList<BoardComment>()
    private lateinit var subjectBulletinBoardCommentRecyclerAdapter: SubjectBulletinBoardCommentRecyclerAdapter
    private var isLiked = false
    lateinit var pathData: BoardPost
    var database: DatabaseManager = DatabaseManager()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        pathData = intent.getSerializableExtra("post") as BoardPost
        Log.d("가져오 게시물 제목", pathData.title)
        postDetailDataLoad(pathData)
        postDetailCommentLoad(pathData)
        Log.d("댓글 가져옴", "성공")
        this.subjectBulletinBoardCommentRecyclerAdapter =
            SubjectBulletinBoardCommentRecyclerAdapter(
                this.itemSubjectBulletinBoardCommentList,
                this
            )
        Log.d("연결중", "성공")


        // 코멘트 작성
        binding.buttonMyClassSubjectBulletinBoardWritingPostComment.setOnClickListener {
            if (binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.isNotEmpty()) {
                var currentUser = auth.currentUser.uid
                var comment =
                    binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.toString()
                database.postLeaveComment(
                    pathData.subjectCode,
                    pathData.subjectBoardIndex,
                    comment,
                    currentUser,
                    object : Callback<Boolean> {
                        override fun onCallback(data: Boolean) {
                            if (data)
                                database.postViewCount(pathData)
                            // 코멘트 갱신화작업
                            subjectBulletinBoardCommentRecyclerAdapter.notifyItemRangeRemoved(
                                0,
                                itemSubjectBulletinBoardCommentList.size - 1
                            )
                            subjectBulletinBoardCommentRecyclerAdapter.notifyDataSetChanged()
                            itemSubjectBulletinBoardCommentList.clear()
                            postDetailCommentLoad(pathData)
                            binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.clear()
                        }
                    }
                )

            }
        }

        // 게시글 작성자에게 메시지 보내기
        binding.imageViewMyClassSubjectBulletinBoardDetailsMessage.setOnClickListener {
            if (auth.uid.toString() == pathData.writerUid) {
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
                Log.d("클릭리스너 실행", "메시지창 불러오는중")
                var chatId = makeChat(auth.currentUser.uid, pathData.writerUid.toString())

                Intent(this, ChattingRoomDetailsActivity::class.java).also {
                    it.putExtra("chatId", chatId)
                    it.putExtra("partnerImg", "image/IMAGE_${pathData.writerUid}.png")
                    startActivity(it)
                }
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
    }

    private fun makeChat(sendUser: String, receiveUser: String): String {
        var chatId: String = if (sendUser > receiveUser) {
            "$sendUser-$receiveUser"
        } else {
            "$receiveUser-$sendUser"
        }
        database.checkChat(chatId, object : Callback<Boolean> {
            override fun onCallback(data: Boolean) {
                if (data) {
                    database.makeChatRoom(sendUser, receiveUser, chatId)
                }
            }
        })
        return chatId
    }

    private fun postDetailDataLoad(
        BoardPost: BoardPost,
    ) {
        binding.textViewMyClassSubjectBulletinBoardDetailsUserName.text = BoardPost.writerName
        binding.textViewMyClassSubjectBulletinBoardDetailsDate.text = BoardPost.day
        binding.textViewMyClassSubjectBulletinBoardDetailsTitle.text = BoardPost.title
        binding.textViewMyClassSubjectBulletinBoardDetailsContent.text = BoardPost.content
        BoardPost.writerUid?.let {
            database.callUserDataImageUri(it, object : Callback<String> {
                override fun onCallback(data: String) {
                    Log.d("data 값", data)
                    FirebaseStorage.getInstance().reference.child("image/$data").downloadUrl.addOnSuccessListener { that ->
                        Glide.with(applicationContext).load(that).circleCrop()
                            .into(binding.imageViewMyClassSubjectBulletinBoardDetailsProfileImg)
                    }
                }
            })
        }
    }

    private fun postDetailCommentLoad(boardPost: BoardPost) {
        Log.d("코멘트 로딩", "체크")
        database.loadPostComment(boardPost.subjectCode, boardPost.subjectBoardIndex,
            object : Callback<ArrayList<BoardComment>> {
                override fun onCallback(data: ArrayList<BoardComment>) {
                    if (data != null) {
                        Log.d("callback", "코멘트추가완료")
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
                            //itemAnimator = DefaultItemAnimator()
                        }

                    } else {
                        Log.d("load comment", "error")
                    }
                }

            })
        Log.d("postDetailCommentLoad", "post comment 로드 종료")
    }

    override fun sendMessageClicked(commentIdx: Int, profileImg: String) {
        // 댓글 단 사람들 중에 채팅 보낼 때
        if (auth.uid.toString() == itemSubjectBulletinBoardCommentList[commentIdx].commenterUid) {
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
            Log.d("댓글단사람들 확인중", "메세지보내기")
            Log.d("클릭리스너 실행", "메시지창 불러오는중")
            var chatId = makeChat(
                auth.currentUser.uid,
                itemSubjectBulletinBoardCommentList[commentIdx].commenterUid
            )

            Intent(this, ChattingRoomDetailsActivity::class.java).also {
                it.putExtra("chatId", chatId)
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

interface sendMessageClickInterface { // 댓글 단 사람들과 채팅
    fun sendMessageClicked(commentIdx: Int, profileImg: String)
}

class SubjectBulletinBoardCommentRecyclerAdapter(
    private val itemSubjectBulletinBoardCommentList: ArrayList<BoardComment>,
    private val sendMessageClickInterface: sendMessageClickInterface,
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
        return SubjectBulletinBoardCommentViewHolder(binding, this.sendMessageClickInterface)
    }

    override fun onBindViewHolder(holder: SubjectBulletinBoardCommentViewHolder, position: Int) {
        holder.bind(itemSubjectBulletinBoardCommentList[position])
    }

    override fun getItemCount(): Int {
        return itemSubjectBulletinBoardCommentList.size
    }

}

// recyclerview viewHolder
class SubjectBulletinBoardCommentViewHolder(
    private val item: ItemSubjectBulletinBoardCommentBinding,
    private val sendMessageClickInterface: sendMessageClickInterface,
) : RecyclerView.ViewHolder(item.root) {

    fun bind(boardComment: BoardComment) {
        // img는 glide로 설정
        item.textViewItemSubjectBulletinBoardCommentUserName.text = boardComment.userName
        item.textViewItemSubjectBulletinBoardCommentCommentContent.text =
            boardComment.content
        item.textViewItemSubjectBulletinBoardCommentDate.text = boardComment.day
        var database = DatabaseManager()
        boardComment.commenterUid?.let {
            database.callUserDataImageUri(it, object : Callback<String> {
                override fun onCallback(data: String) {
                    Log.d("data 값", data)
                    FirebaseStorage.getInstance().reference.child("image/$data").downloadUrl.addOnSuccessListener { that ->
                        Glide.with(itemView.context).load(that).circleCrop()
                            .into(item.imageViewItemSubjectBulletinBoardCommentProfileImg)
                    }
                    item.imageViewItemSubjectBulletinBoardCommentMessage.setOnClickListener {
                        this@SubjectBulletinBoardCommentViewHolder.sendMessageClickInterface.sendMessageClicked(
                            adapterPosition,
                            "image/$data")
                        Log.d("클릭한 아이템", adapterPosition.toString())
                    }
                }
            })
        }


    }
}