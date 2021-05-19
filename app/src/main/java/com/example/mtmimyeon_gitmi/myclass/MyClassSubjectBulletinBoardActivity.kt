package com.example.mtmimyeon_gitmi.myClass

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBulletinBoardBinding
import com.example.mtmimyeon_gitmi.db.Board
import com.example.mtmimyeon_gitmi.db.BoardPost
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MyClassSubjectBulletinBoardActivity : AppCompatActivity(), BulletinBoardClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardBinding
    lateinit var idx: String
    lateinit var subjectName: String
    private var database = Firebase.database.getReference("Board")

    override fun onCreate(savedInstanceState: Bundle?) {

        var intentExtra = getIntent()
        subjectName = intentExtra.getStringExtra("과목이름") // 과목 이름
        idx = intentExtra.getStringExtra("과목코드")// 과목 코드
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarMyClassSubjectBulletinBoardToolbar.title = subjectName
        init()
    }

    private fun init() {
        var DB = DatabaseManager()

        var auth = FirebaseAuth.getInstance()
//        val subjectBulletinBoardList = ArrayList<BoardPost>()
        // 임시 데이터 삽입
//        val subjectBulletinBoardRecyclerAdapter =
//            SubjectBulletinBoardRecyclerAdapter(subjectBulletinBoardList, this)
//

        DB.loadPost(idx, object : Callback<ArrayList<BoardPost>> {
            override fun onCallback(data: ArrayList<BoardPost>) {
                if (data != null) {
                    val subjectBulletinBoardList = data
                    val subjectBulletinBoardRecyclerAdapter =
                        SubjectBulletinBoardRecyclerAdapter(
                            subjectBulletinBoardList,
                            this@MyClassSubjectBulletinBoardActivity
                        )

                    Log.d("포스트 데이터 가져옴 : ", data.count().toString())
                    binding.recyclerviewMyClassSubjectBulletinBoardBoardList.apply {
                        adapter = subjectBulletinBoardRecyclerAdapter
                        layoutManager = LinearLayoutManager(
                            this@MyClassSubjectBulletinBoardActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
            }
        })

//        binding.recyclerviewMyClassSubjectBulletinBoardBoardList.apply {
//            adapter = subjectBulletinBoardRecyclerAdapter
//            layoutManager = LinearLayoutManager(
//                this@MyClassSubjectBulletinBoardActivity,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//        }

        // 글 쓰기 버튼 클릭
        binding.extendFabMyClassSubjectBulletinBoardAddWriting.setOnClickListener {
            var intent = Intent(this, MyClassSubjectBulletinBoardWritingActivity::class.java)
            intent.putExtra("과목코드", idx)
            intent.putExtra("과목이름", subjectName)
            intent.also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }

    }

    override fun itemClicked(idx: Int) {
        // 특정 게시글 클릭 시, 해당 게시글 상세 내용 불러오기
        Intent(this, MyClassSubjectBulletinBoardDetailsActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}


class SubjectBulletinBoardRecyclerAdapter(
    private val itemSubjectBulletinBoardList: ArrayList<BoardPost>,
    private val bulletinBoardClickInterface: BulletinBoardClickInterface
) :
    RecyclerView.Adapter<SubjectBulletinBoardViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubjectBulletinBoardViewHolder {
        val binding = ItemSubjectBulletinBoardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubjectBulletinBoardViewHolder(binding, bulletinBoardClickInterface)
    }

    override fun onBindViewHolder(holder: SubjectBulletinBoardViewHolder, position: Int) {
        holder.bind(itemSubjectBulletinBoardList[position], position)
    }

    override fun getItemCount(): Int {
        return itemSubjectBulletinBoardList.size
    }
}

// recyclerview viewHolder
class SubjectBulletinBoardViewHolder(
    private val item: ItemSubjectBulletinBoardBinding,
    private val bulletinBoardClickInterface: BulletinBoardClickInterface

) :
    RecyclerView.ViewHolder(item.root) {
    private var idx: Int = -1

    init {
        item.root.setOnClickListener {
            this.bulletinBoardClickInterface.itemClicked(this.idx)
        }
    }

    fun bind(BoardPost: BoardPost, position: Int) {
        this.idx = position
        item.textViewItemSubjectBulletinBoardTitle.text = BoardPost.title
        item.textViewItemSubjectBulletinBoardContent.text = BoardPost.content
        item.textViewItemSubjectBulletinBoardDate.text = BoardPost.day
        item.textViewItemSubjectBulletinBoardWriter.text = BoardPost.writerUid
        item.textViewItemSubjectBulletinBoardChatNum.text = BoardPost.subjectBoardIndex
    }
}

// 특정 게시글 클릭 했을 때, 클릭 감지 리스너
interface BulletinBoardClickInterface {
    // DB에서 해당 게시글에 대한 index 정보 넘겨줘야 함
    fun itemClicked(idx: Int)
}