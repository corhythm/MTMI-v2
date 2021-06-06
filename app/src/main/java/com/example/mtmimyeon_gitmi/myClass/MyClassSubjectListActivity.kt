package com.example.mtmimyeon_gitmi.myClass

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.mtmimyeon_gitmi.crawling.CrawlingLmsInfo
import com.example.mtmimyeon_gitmi.crawling.ObserveCrawlingInterface
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectListBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectInfoBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyClassSubjectListActivity : AppCompatActivity(), SubjectClickedInterface,
    ObserveCrawlingInterface {
    private lateinit var binding: ActivityMyClassSubjectListBinding
    private lateinit var subjectRecyclerAdapter: SubjectListRecyclerAdapter

    // SubjectInfo에 더 많은 정보가 있고, 당장은 이 액티비티에서 사용하는 값은 3개밖에 안 되지만,
    // 추후에 더 사용할 수 있고, SubjectInfo를 이 액티비티에서만 사용하는 게 아니므로 우선은 이 리사이클러뷰 아이템도
    // SubjectInfo의 정보를 이용해서 사용(별도의 아이템 클래스 생성 시, 중복되는 정보가 많으므로)
    // 아이템이 많이 생성되면 퍼포먼스에 무리가 가겠지만, 수강 과목이 많아봐야 10개 내외이므로 퍼포먼스에는 크게 무리가 없다.
    private var itemSubjectInfoList = ArrayList<ItemSubjectInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        itemSubjectInfoList =
            SharedPrefManager.getUserLmsSubjectInfoList() as ArrayList<ItemSubjectInfo>

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

        binding.swipeRefreshLayoutMyClassSubjectListRefresh.setOnRefreshListener {
            val crawlingLmsInfo = CrawlingLmsInfo(
                activityType = MyClassSubjectListActivity::class.java,
                observeCrawlingInterface = this,
                mContext = this,
                myId = SharedPrefManager.getUserLmsId(),
                myPw = SharedPrefManager.getUserLmsPw()
            )

            CoroutineScope(Dispatchers.IO).launch {
                val test = crawlingLmsInfo.getLmsData()
            }
        }
    }

    // 과목 게시판으로 이동
    override fun itemClicked(idx: String, subjectName: String) {
        Intent(this, MyClassSubjectBulletinBoardActivity::class.java).also {
            it.putExtra("과목코드", idx)
            it.putExtra("과목이름", subjectName)
            startActivity(it)
        }
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    // 새로고침 끝났을 때
    override suspend fun isCrawlingFinished(activityType: Class<out Activity>, isSuccess: Boolean) {
        withContext(Dispatchers.Main) {
            try {
                init()
                onResume()
                binding.swipeRefreshLayoutMyClassSubjectListRefresh.isRefreshing = false
            } catch (exception: Exception) {

            }
        }
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
        val binding =
            ItemSubjectInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun submit(mjuSiteListInfoItem: ArrayList<ItemSubjectInfo>, context: Context) {
        this.itemSubjectInfoList = mjuSiteListInfoItem
        this.mContext = context
        this.gradientList = mContext.resources.obtainTypedArray(R.array.gradientList)
    }
}

// recyclerview viewHolder
class SubjectViewHolder(
    private val item: ItemSubjectInfoBinding,
    private val subjectClickedInterface: SubjectClickedInterface,
) : RecyclerView.ViewHolder(item.root) {

    @SuppressLint("ResourceType")
    fun bind(itemSubjectInfo: ItemSubjectInfo, context: Context, drawableId: Int) {
        item.textViewItemSubjectName.text = itemSubjectInfo.subjectName
        item.textViewItemProfessor.text = itemSubjectInfo.professorName
        item.textViewItemLectureTime.text = itemSubjectInfo.lectureTime
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
        state: RecyclerView.State,
    ) {
        //super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}

