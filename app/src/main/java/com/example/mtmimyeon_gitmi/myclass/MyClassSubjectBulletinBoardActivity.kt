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
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "파이썬 도와주실 천사를 찾습니다",
                "정말요 제발요 간절해요 너무 어려워요 ㅜㅜㅜ 도와주세요", "2분 전", "익명", "8"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "c언어 ㅈㅅㅎ 교수님 18680",
                "이해가 안가서 그러는데 p스트링 입력받을때 길이도 같이 입력받는거임? 아니면 문자열만 입력받고 길이는 내가 구하는거임?", "10분 전", "급한불꽃", "3"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "한글 파일 설치하는 방법",
                "최대한 싸게 설치하는 방법 알려주세요~~!!!", "31분 전", "하오리", "1"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "와 ㅁㅊ 이제 셤기간 3주 남은거임?",
                "비통하도다.. 시간이 왜 이렇게 빠른 거지?", "1시간 전", "익명", "0"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "[뱃지] 공동구매 폼입니다!",
                "안녕하세요 학우님들💙 뱃지 공동구매 폼을 들고 오랜만에 찾아왔습니다! 제일 처음 시안을 제작했던 굿즈인만큼 애정이 많이 가네요ㅎㅎ", "2시간 전", "익명", "8"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "스승의 날",
                "매년 연락드린 중학교 선생님한테 오늘도 카톡 보냈는데 읽씹당했다... 원래는 항상 답해주셨는데 그만 보내라는 소리인가..?ㅠㅠ", "5/14", "익명", "19"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "영회2 매튜 교수님",
                "다음주 화요일 수업 없나요?? 따로 언급이 없으셔서요..", "5/11", "익명", "11"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "조세형 교수님 과제 18281 하신 분 있으신가요?",
                "비주얼 스튜디오가 getchar를 인식 못하는데 어떡하죠?", "5/10", "익명", "1"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "ㅈㅅㅎ 교수님 강의 이번 주 업로드 됐나요? ",
                "저만 업로드 안 된 건가요...", "5/09", "익명", "1"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "운영체제 책 추천",
                "이번에 강의 들으면서 계속 운영체제 지식 때문에 발목을 많이 잡히는데 관련해서 책 추천해주실 수 있나요??", "5/01", "cs", "2"
            )
        )
        subjectBulletinBoardList.add(
            ItemSubjectBulletinBoard(
                0, "cs 면접 대비",
                "여러분들은 cs 면접 준비 어떻게 하시나요? 아싸라서 어떻게 해야 할지 모르겠어요...", "4/21", "익명", "11"
            )
        )


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