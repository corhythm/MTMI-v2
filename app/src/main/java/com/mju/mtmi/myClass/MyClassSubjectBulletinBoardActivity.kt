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
import com.mju.mtmi.databinding.ActivityMyClassSubjectBulletinBoardBinding
import com.mju.mtmi.databinding.ItemSubjectBulletinBoardBinding
import com.mju.mtmi.database.BoardPost
import com.mju.mtmi.database.Callback
import com.mju.mtmi.database.DatabaseManager


class MyClassSubjectBulletinBoardActivity : AppCompatActivity(), BulletinBoardClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardBinding
    lateinit var subjectCode: String
    lateinit var subjectName: String
    lateinit var subjectBulletinBoardRecyclerAdapter: SubjectBulletinBoardRecyclerAdapter
    lateinit var subjectBulletinBoardList: ArrayList<BoardPost>
    var database = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentExtra = intent
        subjectName = intentExtra.getStringExtra("과목이름")!! // 과목 이름
        subjectCode = intentExtra.getStringExtra("과목코드")!! // 과목 코드

        binding.toolbarMyClassSubjectBulletinBoardToolbar.title = subjectName
        init()
    }

    override fun onResume() {
        super.onResume()
        postListLoad()
    }

    private fun init() {

        // 글 쓰기 버튼 클릭
        binding.extendFabMyClassSubjectBulletinBoardAddWriting.setOnClickListener {
            val intent = Intent(this, MyClassSubjectBulletinBoardWritingActivity::class.java)
            intent.putExtra("과목코드", subjectCode)
            intent.putExtra("과목이름", subjectName)
            intent.also {
                startActivityForResult(it, 100)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
        binding.swipeRefreshLayoutMyClassBoardRefresh.setOnRefreshListener {
            subjectBulletinBoardRecyclerAdapter.notifyItemRangeRemoved(
                0,
                subjectBulletinBoardList.size - 1
            )
            subjectBulletinBoardRecyclerAdapter.notifyDataSetChanged()
            subjectBulletinBoardList.clear()
            postListLoad()
            binding.swipeRefreshLayoutMyClassBoardRefresh.isRefreshing = false
        }
    }

    private fun postListLoad() {
        database.loadPostList(subjectCode, object : Callback<ArrayList<BoardPost>> {
            override fun onCallback(data: ArrayList<BoardPost>) {
                subjectBulletinBoardList = data
                subjectBulletinBoardRecyclerAdapter =
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
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d("데이터받기", "성공")
            subjectBulletinBoardRecyclerAdapter.notifyItemRangeRemoved(
                0,
                subjectBulletinBoardList.size - 1
            )
            subjectBulletinBoardRecyclerAdapter.notifyDataSetChanged()
            subjectBulletinBoardList.clear()
            postListLoad()
        }
    }

    override fun itemClicked(idx: Int, BoardPost: BoardPost) {
        Log.d("클릭한 item :$idx", " 클릭한 포스트" + BoardPost.title)
        // 특정 게시글 클릭 시, 해당 게시글 상세 내용 불러오기
        val intent = Intent(this, MyClassSubjectBulletinBoardDetailsActivity::class.java)
        intent.putExtra("과목코드", subjectCode)
        intent.putExtra("post", BoardPost).also {
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
    lateinit var postDetail: BoardPost

    init {
        item.root.setOnClickListener {
            this.bulletinBoardClickInterface.itemClicked(this.idx, this.postDetail)
        }
    }

    fun bind(BoardPost: BoardPost, position: Int) {
        this.idx = position
        this.postDetail = BoardPost
        item.textViewItemSubjectBulletinBoardTitle.text = BoardPost.title
        item.textViewItemSubjectBulletinBoardContent.text = BoardPost.content
        item.textViewItemSubjectBulletinBoardDate.text = BoardPost.day
        item.textViewItemSubjectBulletinBoardWriter.text = BoardPost.writerName
        item.textViewItemSubjectBulletinBoardChatNum.text = BoardPost.view.toString()

    }
}

// 특정 게시글 클릭 했을 때, 클릭 감지 리스너
interface BulletinBoardClickInterface {
    // DB에서 해당 게시글에 대한 index 정보 넘겨줘야 함
    fun itemClicked(idx: Int, BoardPost: BoardPost)
}