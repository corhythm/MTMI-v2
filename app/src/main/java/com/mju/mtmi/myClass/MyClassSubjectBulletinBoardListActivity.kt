package com.mju.mtmi.myClass

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.mtmi.R
import com.mju.mtmi.databinding.ActivityMyClassSubjectBulletinBoardListBinding
import com.mju.mtmi.databinding.ItemSubjectBulletinBoardBinding
import com.mju.mtmi.database.entity.BoardPost
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager

// 게시글 리스트
class MyClassSubjectBulletinBoardListActivity : AppCompatActivity(), BulletinBoardClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardListBinding
    private lateinit var subjectCode: String
    private lateinit var subjectBulletinBoardRecyclerAdapter: SubjectBulletinBoardRecyclerAdapter
    private lateinit var subjectBulletinBoardList: ArrayList<BoardPost>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.subjectCode = intent.getStringExtra("과목코드")!! // 과목 코드

        Log.d("로그", "MyClassSubjectBulletinBoardListActivity -onCreate() called / subjectCode = $subjectCode")

        binding.toolbarMyClassSubjectBulletinBoardToolbar.title = this.subjectCode
        init()
    }

    override fun onResume() {
        super.onResume()
        getListOfPosts()
    }

    private fun init() {
        // 글 쓰기 버튼 클릭
        binding.extendFabMyClassSubjectBulletinBoardAddWriting.setOnClickListener {
            val intent = Intent(this, MyClassSubjectBulletinBoardWritingActivity::class.java)
            intent.putExtra("과목코드", this.subjectCode)
            intent.also {
                startActivityForResult(it, 100)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }

        // 화면 스와이프 시 새로고침
        binding.swipeRefreshLayoutMyClassBoardRefresh.setOnRefreshListener {
            subjectBulletinBoardRecyclerAdapter.notifyItemRangeRemoved(
                0,
                subjectBulletinBoardList.size - 1
            )
            subjectBulletinBoardRecyclerAdapter.notifyDataSetChanged()
            subjectBulletinBoardList.clear()
            getListOfPosts()
            binding.swipeRefreshLayoutMyClassBoardRefresh.isRefreshing = false
        }
    }

    // 게시글 리스트 가져오기
    private fun getListOfPosts() {
        FirebaseManager.getListOfPosts(
            this.subjectCode,
            object : DataBaseCallback<ArrayList<BoardPost>> {
                override fun onCallback(data: ArrayList<BoardPost>) {
                    subjectBulletinBoardList = data
                    subjectBulletinBoardRecyclerAdapter =
                        SubjectBulletinBoardRecyclerAdapter(
                            subjectBulletinBoardList,
                            this@MyClassSubjectBulletinBoardListActivity
                        )
                    binding.recyclerviewMyClassSubjectBulletinBoardBoardList.apply {
                        adapter = subjectBulletinBoardRecyclerAdapter
                        layoutManager = LinearLayoutManager(
                            this@MyClassSubjectBulletinBoardListActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            subjectBulletinBoardRecyclerAdapter.notifyItemRangeRemoved(
                0,
                subjectBulletinBoardList.size - 1
            )
            subjectBulletinBoardRecyclerAdapter.notifyDataSetChanged()
            subjectBulletinBoardList.clear()
            getListOfPosts()
        }
    }

    override fun itemClicked(idx: Int, boardPost: BoardPost) {
        Log.d("클릭한 item :$idx", " 클릭한 포스트" + boardPost.title)
        // 특정 게시글 클릭 시, 해당 게시글 상세 내용 불러오기
        Intent(this, MyClassSubjectBulletinBoardDetailsActivity::class.java).also {
            it.putExtra("boardPost", boardPost)
            startActivity(it)
        }
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}


class SubjectBulletinBoardRecyclerAdapter(
    private val itemSubjectBulletinBoardList: ArrayList<BoardPost>,
    private val bulletinBoardClickInterface: BulletinBoardClickInterface,
) :
    RecyclerView.Adapter<SubjectBulletinBoardViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
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
    private val bulletinBoardClickInterface: BulletinBoardClickInterface,

    ) :
    RecyclerView.ViewHolder(item.root) {
    private var idx: Int = -1
    private lateinit var boardPost: BoardPost

    init {
        item.root.setOnClickListener {
            this.bulletinBoardClickInterface.itemClicked(this.idx, this.boardPost)
        }
    }

    fun bind(BoardPost: BoardPost, position: Int) {
        this.idx = position
        this.boardPost = BoardPost
        item.textViewItemSubjectBulletinBoardTitle.text = BoardPost.title
        item.textViewItemSubjectBulletinBoardContent.text = BoardPost.content
        item.textViewItemSubjectBulletinBoardDate.text = BoardPost.timeStamp
        item.textViewItemSubjectBulletinBoardWriter.text = BoardPost.writerName
        item.textViewItemSubjectBulletinBoardChatNum.text = BoardPost.commentCount.toString()
    }
}

// 특정 게시글 클릭 했을 때, 클릭 감지 리스너
interface BulletinBoardClickInterface {
    // DB에서 해당 게시글에 대한 index 정보 넘겨줘야 함
    fun itemClicked(idx: Int, boardPost: BoardPost)
}