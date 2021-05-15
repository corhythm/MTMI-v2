package com.example.mtmimyeon_gitmi.myClass

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    lateinit var idx: String
    lateinit var subjectName: String

    override fun onCreate(savedInstanceState: Bundle?) {

        // activity 옆으로 이동 애니메이션
        // 이 코드는 반드시 onCreate에서 super.onCreate(savedInstanceState) 위에 있어야 함
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            // set an slide transition
            enterTransition = Slide(Gravity.END)
            exitTransition = Slide(Gravity.START)
        }

        var intentExtra = getIntent()
        subjectName = intentExtra.getStringExtra("과목이름") // 과목 이름
        idx = intentExtra.getStringExtra("과목코드")// 과목 코드
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarMyClassSubjectBulletinBoardToolbar.setTitle(subjectName)
        init()
    }

    private fun init() {

        val subjectBulletinBoardRecyclerAdapter = SubjectBulletinBoardRecyclerAdapter(this)
        val subjectBulletinBoardList = ArrayList<ItemSubjectBulletinBoard>()
        // 임시 데이터 삽입
        for (i in 0..20) {
            subjectBulletinBoardList.add(
                ItemSubjectBulletinBoard(0, "파이썬 도와주실 천사를 찾습니다",
                "정말요 제발요 간절해요 너무 어려워요 ㅜㅜㅜ 도와주세요", "2분 전", "익명", "8")
            )
        }

        binding.recyclerviewMyClassSubjectBulletinBoardBoardList.apply {
            adapter = subjectBulletinBoardRecyclerAdapter
            layoutManager = LinearLayoutManager(this@MyClassSubjectBulletinBoardActivity, LinearLayoutManager.VERTICAL, false)
            subjectBulletinBoardRecyclerAdapter.submit(subjectBulletinBoardList)
        }

        // 글 쓰기 버튼 클릭
        binding.extendFabMyClassSubjectBulletinBoardAddWriting.setOnClickListener {
            var intent = Intent(this, MyClassSubjectBulletinBoardWritingActivity::class.java)
            intent.putExtra(idx,"과목코드")
            intent.putExtra(subjectName,"과목이름")
            intent.also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
    }

    override fun itemClicked(idx: Int) {
        // 특정 게시글 클릭 시, 해당 게시글 상세 내용 불러오기
        Intent(this, MyClassSubjectBulletinBoardDetailsActivity::class.java).also {
                    startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
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


class SubjectBulletinBoardRecyclerAdapter() : RecyclerView.Adapter<SubjectBulletinBoardViewHolder>() {
    private lateinit var itemSubjectBulletinBoardList: ArrayList<ItemSubjectBulletinBoard>
    private lateinit var bulletinBoardClickInterface: BulletinBoardClickInterface

    constructor(bulletinBoardClickInterface: BulletinBoardClickInterface): this() {
        this.bulletinBoardClickInterface = bulletinBoardClickInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectBulletinBoardViewHolder {
        val binding = ItemSubjectBulletinBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectBulletinBoardViewHolder(binding, bulletinBoardClickInterface)
    }

    override fun onBindViewHolder(holder: SubjectBulletinBoardViewHolder, position: Int) {
        holder.bind(itemSubjectBulletinBoardList[position])
    }

    override fun getItemCount(): Int {
        return itemSubjectBulletinBoardList.size
    }

    fun submit(itemSubjectBulletinBoardList: ArrayList<ItemSubjectBulletinBoard>) {
        this.itemSubjectBulletinBoardList = itemSubjectBulletinBoardList
    }
}

// recyclerview viewHolder
class SubjectBulletinBoardViewHolder(private val item: ItemSubjectBulletinBoardBinding) : RecyclerView.ViewHolder(item.root) {
    private lateinit var bulletinBoardClickInterface: BulletinBoardClickInterface
    private var idx: Int = -1

    constructor(item: ItemSubjectBulletinBoardBinding, bulletinBoardClickInterface: BulletinBoardClickInterface): this(item) {
        this.bulletinBoardClickInterface = bulletinBoardClickInterface
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