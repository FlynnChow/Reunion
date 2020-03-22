package com.example.reunion.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.DialogImListBinding
import com.example.reunion.databinding.FragmentMineBinding
import com.example.reunion.databinding.ViewRecyclerView2Binding
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.server.WebSocketServer
import com.example.reunion.view.adapter.UserMessageAdapter
import com.example.reunion.view_model.HomeViewModel
import com.example.reunion.view_model.MessageViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class ImMessageFragment():BaseFragment() {
    private lateinit var mBinding:ViewRecyclerView2Binding
    private lateinit var deleteDialogBinding:DialogImListBinding
    private val mViewModel by lazy { setViewModel(activity as BaseActivity,MessageViewModel::class.java) }
    private val adapter = UserMessageAdapter{ data,index->
        when(index){
            0 ->{
                mViewModel.setAlreadyRead(data)
                startImActivity(data.toUid)
            }
            else ->{
                mViewModel.deleteIndex = data
                dialog.show()
            }
        }
    }

    val dialog by lazy {
        initDeleteDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycler_view2,container,false)
        mBinding.lifecycleOwner = this

        deleteDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_im_list,null,false)
        deleteDialogBinding.fragment = this
        deleteDialogBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = setViewModel(activity as BaseActivity,HomeViewModel::class.java)

        initView()
        initViewModel()
        mViewModel.onRefreshMessageIndex()
    }

    private fun initView(){
        val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

        mBinding.newsRefresh.isTargetScrollWithLayout = false
        mBinding.newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        mBinding.newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                //下拉距离是否满足刷新
            }

            override fun onPullDistance(p0: Int) {
                //下拉距离
            }

            override fun onRefresh() {
                mViewModel.onRefreshMessageIndex()
            }
        })

        mViewModel.imRefresh.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mBinding.newsRefresh.isRefreshing = false
            }
        })
    }

    private fun initViewModel(){
        mViewModel.imMessages.observe(this, Observer {
            adapter.list.clear()
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })

        mViewModel.imMessage.observe(this, Observer {
            adapter.list.add(0,it)
            adapter.notifyItemInserted(0)
        })

        mViewModel.imUpdateMessage.observe(this, Observer {
            for (index in 0 until adapter.list.size){
                if (adapter.list[index].tableId == it.tableId){
                    adapter.list[index] = it
                    adapter.notifyItemChanged(index)
                    break
                }
            }
        })

        mViewModel.imDeleteMessage.observe(this, Observer {
            for (index in 0 until adapter.list.size){
                if (adapter.list[index].tableId == it.tableId){
                    adapter.list.remove(adapter.list[index])
                    adapter.notifyItemRemoved(index)
                    break
                }
            }
        })

        mViewModel.updateMessage.observe(this, Observer {
            if (it == 0){
                adapter.list.clear()
                adapter.notifyDataSetChanged()
            }else if (it == 3){
                mViewModel.onRefreshMessageIndex()
            }
        })

        WebSocketServer.imIndex.observe(this, Observer {
            if (it != null){
                insertAndUpdateIndexItem(it)
            }
        })

        WebSocketServer.imIndexArray.observe(this, Observer {
            if (it != null){
                for (index in it){
                    insertAndUpdateIndexItem(index)
                }
            }
        })

    }

    private fun initDeleteDialog(): Dialog {
        val dialog =  Dialog(activity!!,R.style.CustomizeDialog)
        dialog.setContentView(deleteDialogBinding.root)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.dismiss()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun closeDialog(view:View) = dialog.dismiss()

    fun onDeleteIndex(view:View) {
        dialog.dismiss()
        mViewModel.deleteIndex()
    }

    private fun startImActivity(uid:String){
        startActivity(Intent(activity,ImActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("uid",uid)
        })
    }

    override fun onDestroy() {
        WebSocketServer.exitIndex()
        super.onDestroy()
    }

    private fun insertAndUpdateIndexItem(newData:ImMessageIndex){
        for (index in 0 until adapter.list.size){
            if (newData.tableId == adapter.list[index].tableId){
                newData.num = adapter.list[index].num + newData.num
                adapter.list.remove(adapter.list[index])
                adapter.notifyItemRemoved(index)
                adapter.list.add(0,newData)
                adapter.notifyItemInserted(0)
                return
            }
        }
        adapter.list.add(0,newData)
        adapter.notifyItemInserted(0)
    }
}