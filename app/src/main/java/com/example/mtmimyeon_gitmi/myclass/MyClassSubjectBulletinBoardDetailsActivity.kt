package com.example.mtmimyeon_gitmi.myClass

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.chatting.ChattingRoomDetailsActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardDetailsBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBulletinBoardCommentBinding
import com.example.mtmimyeon_gitmi.db.BoardComment
import com.example.mtmimyeon_gitmi.db.BoardPost
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MyClassSubjectBulletinBoardDetailsActivity : AppCompatActivity(), sendMessageClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardDetailsBinding
    private val itemSubjectBulletinBoardCommentList = ArrayList<ItemSubjectBulletinBoardComment>()
    private lateinit var subjectBulletinBoardCommentRecyclerAdapter: SubjectBulletinBoardCommentRecyclerAdapter
    private var isLiked = false
    var database: DatabaseManager = DatabaseManager()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        val pathData: BoardPost = intent.getSerializableExtra("post") as BoardPost
        Log.d("가져오 게시물 제목",pathData.title)
        postDetailDataLoad(pathData)

        database.loadPostComment(pathData.subjectCode,pathData.subjectBoardIndex,
            object : Callback<ArrayList<BoardComment>> {
                override fun onCallback(data: ArrayList<BoardComment>) {
                    if(data != null){

                    }
                }
            })
        for (i in 0..30) {
            this.itemSubjectBulletinBoardCommentList.add(
                ItemSubjectBulletinBoardComment(
                    -1,
                    "60172121 컴퓨터공학과 강성욱",
                    "원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ원래 밤에 올라오는게 국룰인데 벌써 올라오네 ㅋㅋㅋ 화이팅해 친구들 난 다함 ㅎ",
                    "https:~~",
                    "21.05.02 11:40"
                )
            )
        }

        this.subjectBulletinBoardCommentRecyclerAdapter =
            SubjectBulletinBoardCommentRecyclerAdapter(
                this.itemSubjectBulletinBoardCommentList,
                this
            )

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
        binding.buttonMyClassSubjectBulletinBoardWritingPostComment.setOnClickListener{
            var currentUser = auth.currentUser.uid
            var comment = binding.editTextMyClassSubjectBulletinBoardWritingCommentContent.text.toString()
            database.postLeaveComment(pathData.subjectCode,pathData.subjectBoardIndex,comment,currentUser)
        }

        // 게시글 작성자에게 메시지 보내기, 추후 userIdx 같은 값 추자적으로 보낼 것
        binding.imageViewMyClassSubjectBulletinBoardDetailsMessage.setOnClickListener {
            Log.d("클릭리스너 실행", "메시지창 불러오는중")
            database.makeChatRoom(auth.currentUser.uid, "1234")
            Toast.makeText(
                this,
                "채팅방 개설 성공",
                Toast.LENGTH_SHORT
            ).show()

            Intent(
                this,
                ChattingRoomDetailsActivity::class.java
            ).also {
                startActivity(
                    it,
                    ActivityOptions.makeSceneTransitionAnimation(this@MyClassSubjectBulletinBoardDetailsActivity)
                        .toBundle()
                )
            }
        }
    }
    fun postDetailDataLoad(
        BoardPost: BoardPost
    ) {
        binding.textViewMyClassSubjectBulletinBoardDetailsUserName.text = BoardPost.writerName
        binding.textViewMyClassSubjectBulletinBoardDetailsDate.text = BoardPost.day
        binding.textViewMyClassSubjectBulletinBoardDetailsTitle.text = BoardPost.title
        binding.textViewMyClassSubjectBulletinBoardDetailsContent.text = BoardPost.content
    }
    override fun sendMessageClicked() {
        // 댓글 단 사람들 중에 채팅 보낼 때
        Log.d("댓글단사람들 확인중", "메세지보내기")
        database.makeChatRoom(auth.currentUser.uid, "1234")
        Toast.makeText(
            this,
            "채팅방 개설 성공",
            Toast.LENGTH_SHORT
        ).show()

        Intent(
            this,
            ChattingRoomDetailsActivity::class.java
        ).also {
            startActivity(
                it,
                ActivityOptions.makeSceneTransitionAnimation(this@MyClassSubjectBulletinBoardDetailsActivity)
                    .toBundle()
            )
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

interface sendMessageClickInterface { // 댓글 단 사람들과 채팅
    fun sendMessageClicked()
}


// recyclerview item
data class ItemSubjectBulletinBoardComment(
    val idx: Int,
    val name: String,
    val commentContent: String,
    val profileImgUrl: String,
    var date: String
)

class SubjectBulletinBoardCommentRecyclerAdapter(
    private val itemSubjectBulletinBoardCommentList: ArrayList<ItemSubjectBulletinBoardComment>,
    private val sendMessageClickInterface: sendMessageClickInterface
) : RecyclerView.Adapter<SubjectBulletinBoardCommentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
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
    private val sendMessageClickInterface: sendMessageClickInterface
) : RecyclerView.ViewHolder(item.root) {

    fun bind(itemSubjectBulletinBoard: ItemSubjectBulletinBoardComment) {
        // img는 glide로 설정
        item.textViewItemSubjectBulletinBoardCommentUserName.text = itemSubjectBulletinBoard.name
        item.textViewItemSubjectBulletinBoardCommentCommentContent.text =
            itemSubjectBulletinBoard.commentContent
        item.textViewItemSubjectBulletinBoardCommentDate.text = itemSubjectBulletinBoard.date

        item.imageViewItemSubjectBulletinBoardCommentMessage.setOnClickListener {
            this.sendMessageClickInterface.sendMessageClicked()
        }
    }
}