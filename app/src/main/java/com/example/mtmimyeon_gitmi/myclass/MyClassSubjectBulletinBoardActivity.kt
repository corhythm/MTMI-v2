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
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBulletinBoardBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemSubjectBulletinBoard

class MyClassSubjectBulletinBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // activity 옆으로 이동 애니메이션
        // 이 코드는 반드시 onCreate에서 super.onCreate(savedInstanceState) 위에 있어야 함
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            // set an slide transition
            enterTransition = Slide(Gravity.END)
            exitTransition = Slide(Gravity.START)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        val subjectBulletinBoardRecyclerAdapter = SubjectBulletinBoardRecyclerAdapter()
        val subjectBulletinBoardList = ArrayList<ItemSubjectBulletinBoard>()
        // 임시 데이터 삽입
        for (i in 0..20) {
            subjectBulletinBoardList.add(
                ItemSubjectBulletinBoard("파이썬 도와주실 천사를 찾습니다",
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
            Intent(this, MyClassSubjectBulletinBoardWritingActivity::class.java).also {
                    startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }
        }
    }
}

class SubjectBulletinBoardRecyclerAdapter() : RecyclerView.Adapter<SubjectBulletinBoardViewHolder>() {
    private lateinit var itemSubjectBulletinBoardList: ArrayList<ItemSubjectBulletinBoard>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectBulletinBoardViewHolder {
        val binding = ItemSubjectBulletinBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectBulletinBoardViewHolder(binding)
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

    fun bind(itemSubjectBulletinBoard: ItemSubjectBulletinBoard) {
        item.textViewItemSubjectBulletinBoardTitle.text = itemSubjectBulletinBoard.title
        item.textViewItemSubjectBulletinBoardContent.text = itemSubjectBulletinBoard.content
        item.textViewItemSubjectBulletinBoardDate.text = itemSubjectBulletinBoard.date
        item.textViewItemSubjectBulletinBoardWriter.text = itemSubjectBulletinBoard.writer
        item.textViewItemSubjectBulletinBoardChatNum.text = itemSubjectBulletinBoard.chatNum
    }
}