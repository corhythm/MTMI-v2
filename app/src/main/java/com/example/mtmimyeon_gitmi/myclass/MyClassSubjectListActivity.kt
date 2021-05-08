package com.example.mtmimyeon_gitmi.myClass

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectListBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectInfoBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemSubjectInfo
import com.example.mtmimyeon_gitmi.util.SharedPrefManager

class MyClassSubjectListActivity : AppCompatActivity(), SubjectClickedInterface {
    private lateinit var binding: ActivityMyClassSubjectListBinding
    private lateinit var subjectRecyclerAdapter: SubjectListRecyclerAdapter
    private var itemSubjectInfoList = ArrayList<ItemSubjectInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 테스트 데이터 삽입
//        itemSubjectInfoList.add(ItemSubjectInfo("code1", "공학수학", "소순태", "금(09:00-1050), 수(10:00-10:50)"))
//        itemSubjectInfoList.add(ItemSubjectInfo("code2", "물리학실험1", "이광호", "금(09:00-1050), 수(10:00-10:50)"))
//        itemSubjectInfoList.add(ItemSubjectInfo("code3", "소프트웨어공학", "장희정", "금(09:00-1050), 수(10:00-10:50)"))
//        itemSubjectInfoList.add(ItemSubjectInfo("code4", "시스템클라우드보안", "조민경", "금(09:00-1050), 수(10:00-10:50)"))
//        itemSubjectInfoList.add(ItemSubjectInfo("code5", "운영체제", "류연승", "금(09:00-1050), 수(10:00-10:50)"))
//        itemSubjectInfoList.add(ItemSubjectInfo("code6", "인공지능", "조민경", "금(09:00-1050), 수(10:00-10:50)"))
//        itemSubjectInfoList.add(ItemSubjectInfo("code7", "팀프로젝트2", "류연승", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectInfoList = SharedPrefManager.getUserLmsSubjectInfoList() as ArrayList<ItemSubjectInfo>

        subjectRecyclerAdapter = SubjectListRecyclerAdapter(this)
        binding.recyclerviewMyClassSubjectListList.apply {
            adapter = subjectRecyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MyClassSubjectListActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(SubjectRecyclerDecoration())
            //itemAnimator = DefaultItemAnimator()
            subjectRecyclerAdapter.submit(itemSubjectInfoList, this@MyClassSubjectListActivity)
        }

    }

    override fun itemClicked(idx: String, subjectName: String) {
        startActivity(Intent(this, MyClassSubjectBulletinBoardActivity::class.java))
    }
}

interface SubjectClickedInterface {
    fun itemClicked(idx: String, subjectName: String)
}

// recyclerview adapter
class SubjectListRecyclerAdapter(private val subjectClickedInterface: SubjectClickedInterface) :
    RecyclerView.Adapter<SubjectViewHolder>() {
    private lateinit var itemSubjectInfoList: ArrayList<ItemSubjectInfo>
    private lateinit var mContext: Context
    private lateinit var gradientList: TypedArray
    private var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding, this.subjectClickedInterface)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(
            itemSubjectInfoList[position],
            mContext,
            gradientList.getResourceId(count++ % gradientList.length(), -1)
        )
        Log.d("로그", "${(count - 1) % gradientList.length()}")
    }

    override fun getItemCount(): Int {
        return itemSubjectInfoList.size
    }

    fun submit(itemMjuSiteListInfo: ArrayList<ItemSubjectInfo>, context: Context) {
        this.itemSubjectInfoList = itemMjuSiteListInfo
        this.mContext = context
        this.gradientList = mContext.resources.obtainTypedArray(R.array.gradientList)
    }
}

// recyclerview viewHolder
class SubjectViewHolder(
    private val item: ItemSubjectInfoBinding,
    private val subjectClickedInterface: SubjectClickedInterface
) : RecyclerView.ViewHolder(item.root) {

    @SuppressLint("ResourceType")
    fun bind(itemSubjectInfo: ItemSubjectInfo, context: Context, drawableId: Int) {
        item.textViewItemSubjectName.text = itemSubjectInfo.subjectName
        item.textViewItemSubjectCode.text = itemSubjectInfo.professor
        item.textViewItemSubjectTime.text = itemSubjectInfo.lectureTime
        item.root.background = ContextCompat.getDrawable(context, drawableId)
        Log.d("로그", "itemSubjectInto.subjectCode = ${itemSubjectInfo.subjectCode}")

        // 게시판 뷰 클릭했을 때, 해당 과목에 해당하는 게시판으로 이동
        item.root.setOnClickListener {
            this.subjectClickedInterface.itemClicked(
                itemSubjectInfo.subjectCode,
                itemSubjectInfo.subjectName
            )
        }
    }
}

// recyclerview decoration - add vertical spacing between each view
class SubjectRecyclerDecoration : RecyclerView.ItemDecoration() {
    private val verticalSpaceHeight = 30

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}

