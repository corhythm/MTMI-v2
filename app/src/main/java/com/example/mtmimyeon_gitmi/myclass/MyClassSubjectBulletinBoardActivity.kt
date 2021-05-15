package com.example.mtmimyeon_gitmi.myClass

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBulletinBoardBinding

class MyClassSubjectBulletinBoardActivity : AppCompatActivity(), BulletinBoardClickInterface {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {


        val subjectBulletinBoardList = ArrayList<ItemSubjectBulletinBoard>()
        // 임시 데이터 삽입
        for (i in 0..20) {
            subjectBulletinBoardList.add(
                ItemSubjectBulletinBoard(
                    0, "파이썬 도와주실 천사를 찾습니다",
                    "정말요 제발요 간절해요 너무 어려워요 ㅜㅜㅜ 도와주세요", "2분 전", "익명", "8"
                )
            )
        }

        val subjectBulletinBoardRecyclerAdapter =
            SubjectBulletinBoardRecyclerAdapter(subjectBulletinBoardList, this)
        binding.recyclerviewMyClassSubjectBulletinBoardBoardList.apply {
            adapter = subjectBulletinBoardRecyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MyClassSubjectBulletinBoardActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        // 글 쓰기 버튼 클릭
        binding.extendFabMyClassSubjectBulletinBoardAddWriting.setOnClickListener {
            Intent(this, MyClassSubjectBulletinBoardWritingActivity::class.java).also {
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

data class ItemSubjectBulletinBoard(
    val idx: Int,
    val title: String,
    val content: String,
    val date: String,
    val writer: String,
    val chatNum: String
)


class SubjectBulletinBoardRecyclerAdapter(
    private val itemSubjectBulletinBoardList: ArrayList<ItemSubjectBulletinBoard>,
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
        holder.bind(itemSubjectBulletinBoardList[position])
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

    fun bind(itemSubjectBulletinBoard: ItemSubjectBulletinBoard) {
        this.idx = itemSubjectBulletinBoard.idx
        item.textViewItemSubjectBulletinBoardTitle.text = itemSubjectBulletinBoard.title
        item.textViewItemSubjectBulletinBoardContent.text = itemSubjectBulletinBoard.content
        item.textViewItemSubjectBulletinBoardDate.text = itemSubjectBulletinBoard.date
        item.textViewItemSubjectBulletinBoardWriter.text = itemSubjectBulletinBoard.writer
        item.textViewItemSubjectBulletinBoardChatNum.text = itemSubjectBulletinBoard.chatNum
    }
}

// 특정 게시글 클릭 했을 때, 클릭 감지 리스너
interface BulletinBoardClickInterface {
    // DB에서 해당 게시글에 대한 index 정보 넘겨줘야 함
    fun itemClicked(idx: Int)
}