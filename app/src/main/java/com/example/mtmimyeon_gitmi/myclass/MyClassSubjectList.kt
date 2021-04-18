package com.example.mtmimyeon_gitmi.myclass

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectListBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectBinding
import com.example.mtmimyeon_gitmi.item.ItemSubject

class MyClassSubjectList : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectListBinding
    private lateinit var subjectRecyclerAdapter: SubjectListRecyclerAdapter
    private val itemSubjectList = ArrayList<ItemSubject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 테스트 데이터 삽입
        itemSubjectList.add(ItemSubject("공학수학", "KME02103-0281", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("물리학실험1", "KME02113-0328", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(
            ItemSubject(
                "소프트웨어공학",
                "JEJ02447-0864",
                "금(09:00-1050), 수(10:00-10:50)"
            )
        )
        itemSubjectList.add(
            ItemSubject(
                "시스템클라우드보안",
                "JEJ02473-0868",
                "금(09:00-1050), 수(10:00-10:50)"
            )
        )
        itemSubjectList.add(ItemSubject("운영체제", "JEJ03407-0854", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("인공지능", "JEJ02410-0858", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))
        itemSubjectList.add(ItemSubject("팀프로젝트2", "JEJ02227-0842", "금(09:00-1050), 수(10:00-10:50)"))

        subjectRecyclerAdapter = SubjectListRecyclerAdapter()
        binding.recyclerviewMyClassSubjectListList.apply {
            adapter = subjectRecyclerAdapter
            layoutManager =
                LinearLayoutManager(this@MyClassSubjectList, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SubjectRecyclerDecoration())
            //itemAnimator = DefaultItemAnimator()
            subjectRecyclerAdapter.submit(itemSubjectList, this@MyClassSubjectList)
        }

    }
}

// recyclerview adapter
class SubjectListRecyclerAdapter : RecyclerView.Adapter<SubjectViewHolder>() {
    private lateinit var itemSubjectList: ArrayList<ItemSubject>
    private lateinit var mContext: Context
    private lateinit var gradientList: TypedArray
    private var count = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(itemSubjectList[position], mContext, gradientList.getResourceId(count++ % gradientList.length(), -1))
        Log.d("로그", "${(count-1) % gradientList.length()}")
    }

    override fun getItemCount(): Int {
        return itemSubjectList.size
    }

    fun submit(itemMjuSiteList: ArrayList<ItemSubject>, context: Context) {
        this.itemSubjectList = itemMjuSiteList
        this.mContext = context
        this.gradientList = mContext.resources.obtainTypedArray(R.array.gradientList)
    }
}

// recyclerview viewHolder
class SubjectViewHolder(private val item: ItemSubjectBinding) : RecyclerView.ViewHolder(item.root) {
    @SuppressLint("ResourceType")
    fun bind(itemSubject: ItemSubject, context: Context, drawableId: Int) {
        item.textViewItemSubjectName.text = itemSubject.subjectName
        item.textViewItemSubjectCode.text = itemSubject.subjectCode
        item.textViewItemSubjectTime.text = itemSubject.subjectTime
        item.root.background = ContextCompat.getDrawable(context, drawableId)
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

