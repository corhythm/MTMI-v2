package com.example.mtmimyeon_gitmi.myClass

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardDetailsBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBulletinBoardCommentBinding
import com.example.mtmimyeon_gitmi.item.ItemSubjectBulletinBoardComment

class MyClassSubjectBulletinBoardDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardDetailsBinding
    private val itemSubjectBulletinBoardCommentList = ArrayList<ItemSubjectBulletinBoardComment>()
    private lateinit var subjectBulletinBoardCommentRecyclerAdapter: SubjectBulletinBoardCommentRecyclerAdapter
    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

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

        // 좋아요 버튼 클릭했을 때, DB에 count++하고 하이라이트 표시해줘야 함
        binding.lottieButtonMyClassSubjectBulletinBoardDetailsLike.setOnClickListener {
            if (!this.isLiked) { // 좋아요 상태가 아닐 때
                val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1500)
                animator.addUpdateListener {
                    this.binding.lottieButtonMyClassSubjectBulletinBoardDetailsLike.progress =
                        animator.animatedValue as Float
                }
                this.isLiked = true
                animator.start()
            } else { // 좋아요 상태일 때
                val animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(300)
                animator.addUpdateListener {
                    this.binding.lottieButtonMyClassSubjectBulletinBoardDetailsLike.progress =
                        animator.animatedValue as Float
                }
                this.isLiked = false
                animator.start()
            }
        }


        this.subjectBulletinBoardCommentRecyclerAdapter =
            SubjectBulletinBoardCommentRecyclerAdapter()
        binding.recyclerviewMyClassSubjectBulletinBoardCommentList.apply {
            adapter = subjectBulletinBoardCommentRecyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MyClassSubjectBulletinBoardDetailsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(SubjectRecyclerDecoration())
            //itemAnimator = DefaultItemAnimator()
            subjectBulletinBoardCommentRecyclerAdapter.submit(itemSubjectBulletinBoardCommentList)
        }
    }
}

class SubjectBulletinBoardCommentRecyclerAdapter() :
    RecyclerView.Adapter<SubjectBulletinBoardCommentViewHolder>() {
    private lateinit var itemSubjectBulletinBoardCommentList: ArrayList<ItemSubjectBulletinBoardComment>

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubjectBulletinBoardCommentViewHolder {
        val binding = ItemSubjectBulletinBoardCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubjectBulletinBoardCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectBulletinBoardCommentViewHolder, position: Int) {
        holder.bind(itemSubjectBulletinBoardCommentList[position])
    }

    override fun getItemCount(): Int {
        return itemSubjectBulletinBoardCommentList.size
    }

    fun submit(itemSubjectBulletinBoardCommentList: ArrayList<ItemSubjectBulletinBoardComment>) {
        this.itemSubjectBulletinBoardCommentList = itemSubjectBulletinBoardCommentList
    }
}

// recyclerview viewHolder
class SubjectBulletinBoardCommentViewHolder(private val item: ItemSubjectBulletinBoardCommentBinding) :
    RecyclerView.ViewHolder(item.root) {
    private var ind = -1

    fun bind(itemSubjectBulletinBoard: ItemSubjectBulletinBoardComment) {
        // img는 glider로 설정
        item.textViewItemSubjectBulletinBoardCommentUserName.text = itemSubjectBulletinBoard.name
        item.textViewItemSubjectBulletinBoardCommentCommentContent.text = itemSubjectBulletinBoard.commentContent
        item.textViewItemSubjectBulletinBoardCommentDate.text = itemSubjectBulletinBoard.date

    }
}