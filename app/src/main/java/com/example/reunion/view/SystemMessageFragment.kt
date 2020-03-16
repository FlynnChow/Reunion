package com.example.reunion.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.DialogImListBinding
import com.example.reunion.databinding.DialogSysListBinding
import com.example.reunion.databinding.FragmentMineBinding
import com.example.reunion.databinding.ViewRecyclerView2Binding
import com.example.reunion.repostory.bean.SystemMessageBean
import com.example.reunion.repostory.server.WebSocketServer
import com.example.reunion.view.adapter.SystemMessageAdapter
import com.example.reunion.view_model.HomeViewModel
import com.example.reunion.view_model.MessageViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import com.google.gson.Gson

class SystemMessageFragment:BaseFragment() {
    private lateinit var mBinding:ViewRecyclerView2Binding
    private lateinit var deleteDialogBinding: DialogSysListBinding
    private val mViewModel by lazy { setViewModel(activity as BaseActivity,MessageViewModel::class.java) }
    val dialog by lazy {
        initDeleteDialog()
    }
    private val adapter = SystemMessageAdapter{ index,bean ->
        when(index){
            0 ->{
                mViewModel.updateIsRead(bean.tableId,bean.isRead)
                startActivity(Intent(activity,MyTopicActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("uid",bean.targetUid)
                })
            }
            1 ->{
                mViewModel.deleteSystemMessage = bean
                dialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycler_view2,container,false)
        mBinding.lifecycleOwner = this

        deleteDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_sys_list,null,false)
        deleteDialogBinding.fragment = this
        deleteDialogBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = setViewModel(activity as BaseActivity,HomeViewModel::class.java)

        initView()
        mViewModel.onRefreshSystemMessage()
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
                mViewModel.onRefreshSystemMessage()
            }
        })

        mViewModel.sysRefresh.observe(this, androidx.lifecycle.Observer {
            if (!it)
                mBinding.newsRefresh.isRefreshing = false
            else{
                adapter.list.clear()
                adapter.notifyDataSetChanged()
            }
        })

        WebSocketServer.systemMessage.observe(this, Observer {
            if (it != null){
                for (index in 0 until adapter.list.size){
                    if (it.tableId == adapter.list[index].tableId){
                        adapter.list.remove(adapter.list[index])
                        adapter.notifyItemRemoved(index)
                        break
                    }
                }
                insertAndUpdateIndexItem(it)
            }
        })

        WebSocketServer.systemMessageArray.observe(this, Observer {
            if (it != null){
                for (item in it){
                    for (index in 0 until adapter.list.size){
                        if (item.tableId == adapter.list[index].tableId){
                            adapter.list.remove(adapter.list[index])
                            adapter.notifyItemRemoved(index)
                            break
                        }
                    }
                    insertAndUpdateIndexItem(item)
                }
            }
        })

        mViewModel.sysMessages.observe(this, androidx.lifecycle.Observer {
            adapter.list.clear()
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })

        mViewModel.deleteMessage.observe(this, Observer {
            if (it != null){
                for (index in 0 until adapter.list.size){
                    if (adapter.list[index].tableId == it.tableId){
                        adapter.list.remove(adapter.list[index])
                        adapter.notifyItemRemoved(index)
                        break
                    }
                }
            }
        })

        mViewModel.clearMessage.observe(this, Observer {
            if (it == 1){
                adapter.list.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun insertAndUpdateIndexItem(newData:SystemMessageBean){
        adapter.list.add(0,newData)
        adapter.notifyItemInserted(0)
    }

    override fun onDestroy() {
        WebSocketServer.exitSystem()
        super.onDestroy()
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
        mViewModel.deleteSystemMessage()
    }
}