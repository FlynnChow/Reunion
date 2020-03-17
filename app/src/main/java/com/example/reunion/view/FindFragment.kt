package com.example.reunion.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentFindBinding
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.TopicFragViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import kotlinx.android.synthetic.main.view_recycler_view.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FindFragment(private val type:String = ""):BaseFragment() {
    private val mCityPicker by lazy { CityPickerView() }
    private lateinit var mAgePicker: OptionsPickerView<*>
    private lateinit var mTimePicker: TimePickerView

    private lateinit var mBinding:FragmentFindBinding
    private val adapter = TopicItemAdapter{
        startActivity(Intent(activity,TopicActivity::class.java).apply {
            putExtra("data",it)
        })
    }
    private val mViewModel by lazy { setViewModel(this,TopicFragViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_find,container,false)
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.recyclerView
        mBinding.viewModel = mViewModel
        if (type == "people"){
            mViewModel.type.value = 0
            mViewModel.ageView.value = activity?.resources?.getString(R.string.send_find_people_age)
            mViewModel.time.value = activity?.resources?.getString(R.string.send_find_people_time)
            mViewModel.areaView.value = activity?.resources?.getString(R.string.send_find_people_area)
        }else{
            mViewModel.type.value = 1
            mViewModel.ageView.value= activity?.resources?.getString(R.string.send_find_body_age)
            mViewModel.time.value = activity?.resources?.getString(R.string.send_find_body_time)
            mViewModel.areaView.value= activity?.resources?.getString(R.string.send_find_body_area)
        }

        initView()
        initPicker()
        initViewModel()

        mViewModel.updateItems(type,true)
    }

    private fun initView(){
        val manager = GridLayoutManager(context,2)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

        initRefreshView()
    }

    private fun initViewModel(){
        mViewModel.newData.observe(this, androidx.lifecycle.Observer {
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun initRefreshView(){
        val footView:View = View.inflate(context,R.layout.view_more_load,null)
        newsRefresh.isTargetScrollWithLayout = false
        newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                //下拉距离是否满足刷新
            }

            override fun onPullDistance(p0: Int) {
                //下拉距离
            }

            override fun onRefresh() {
                mViewModel.onRefresh(type)
            }
        })
        mViewModel.refreshing.observe(this, androidx.lifecycle.Observer {
            if (!it)
                newsRefresh.isRefreshing = false
            else{
                adapter.list.clear()
                adapter.notifyDataSetChanged()
            }
        })

        newsRefresh.setOnPushLoadMoreListener(object : SuperSwipeRefreshLayout.OnPushLoadMoreListener{
            override fun onPushDistance(p0: Int) {
            }

            override fun onPushEnable(isLoad: Boolean) {
                if (isLoad){
                    setFootViewState(1,footView)
                }else{
                    setFootViewState(0,footView)
                }
            }

            override fun onLoadMore() {
                setFootViewState(2,footView)
                mViewModel.updateItems(type)
            }
        })
        newsRefresh.setFooterView(footView)
        mViewModel.loading.observe(this, androidx.lifecycle.Observer {
            if (!it){
                newsRefresh.setLoadMore(false)
                setFootViewState(3,footView)
            }
        })
    }

    private fun setFootViewState(state:Int,footView:View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }

    @SuppressLint("SimpleDateFormat")
    private fun initPicker(){
        mCityPicker.init(activity)
        val config = CityConfig.Builder().confirTextColor("#FF6868").build()
        mCityPicker.setConfig(config)
        mCityPicker.setOnCityItemClickListener(object : OnCityItemClickListener(){
            override fun onSelected(
                province: ProvinceBean?,
                city: CityBean?,
                district: DistrictBean?
            ) {
                mViewModel.province.value = province?.name?:""
                mViewModel.city.value = city?.name?:""
                mViewModel.district.value = district?.name?:""
                var area = StringBuilder()
                    .append(province?.name?:"")
                    .append(",")
                    .append(city?.name?:"")
                    .append(",")
                    .append(district?.name?:"")
                    .toString()

                mViewModel.area.value = area
                if (area.length >= 6){
                    area = area.substring(0,6) + ".."
                }
                mViewModel.areaView.value = area

                mViewModel.onRefresh(type)
            }
        })

        val item: ArrayList<ProvinceBean> = ArrayList()
        item.apply {
            for (index in 0 until  20){
                val range = "${index*5}岁-${index*5+5}岁"
                add(ProvinceBean().apply { name = range })
            }
        }

        mAgePicker = OptionsPickerBuilder(activity,
            OnOptionsSelectListener { options,_,_,_->
                mViewModel.ageView.value = item[options].name
                mViewModel.age.value = "${options*5}-${options*5+5}"

                mViewModel.onRefresh(type)
            })
            .setTitleText("选择${ if (type == "people") 
                activity?.resources?.getString(R.string.send_find_people_age)
            else activity?.resources?.getString(R.string.send_find_body_age)
            }")
            .setSubmitColor(resources.getColor(R.color.picker_color))
            .setCancelColor(resources.getColor(R.color.picker_color))
            .build<Any>().also {
                it.setPicker(item as List<ProvinceBean>)
            }

        val date = Date()
        var dataFormat = SimpleDateFormat("yyyy")
        val year = dataFormat.format(date).toInt()
        dataFormat = SimpleDateFormat("MM")
        val month = dataFormat.format(date).toInt()
        dataFormat = SimpleDateFormat("dd")
        val day = dataFormat.format(date).toInt()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.set(1900,0,0)
        endDate.set(year,month-1,day)
        mTimePicker = TimePickerBuilder(activity,
            OnTimeSelectListener { time, _ ->
                val format = SimpleDateFormat("yyyy-MM-dd")
                mViewModel.time.value = format.format(time)
                mViewModel.timeSelected = true
                mViewModel.onRefresh(type)
            })
            .setTitleText("选择${ if (type == "people")
                activity?.resources?.getString(R.string.send_find_people_time)
            else activity?.resources?.getString(R.string.send_find_body_time)
            }")
            .setSubmitColor(resources.getColor(R.color.picker_color))
            .setCancelColor(resources.getColor(R.color.picker_color))
            .setDate(endDate)
            .setRangDate(startDate,endDate)
            .build()
    }

    fun onClickAge(view:View){
        mAgePicker.show()
    }

    fun onClickTime(view:View){
        mTimePicker.show()
    }

    fun onClickArea(view:View){
        mCityPicker.showCityPicker()
    }

}