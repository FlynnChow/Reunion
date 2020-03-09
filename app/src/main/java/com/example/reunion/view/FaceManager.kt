package com.example.reunion.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseViewModel
import com.example.reunion.databinding.ActivityFaceManagerBinding
import com.example.reunion.databinding.DialogConfirmDeleteBinding
import com.example.reunion.databinding.DialogLoadingBinding
import com.example.reunion.databinding.DialogRegisterFaceBinding
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.view.adapter.FaceItemAdapter
import com.example.reunion.view_model.FaceMgViewModel
import kotlinx.android.synthetic.main.activity_face_manager.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FaceManager : BaseActivity() {
    private lateinit var mBinding:ActivityFaceManagerBinding
    private lateinit var dialogBinding:DialogRegisterFaceBinding
    private lateinit var deleteDialogBinding:DialogConfirmDeleteBinding
    private lateinit var loadDialogBinding:DialogLoadingBinding
    private val adapter by lazy { FaceItemAdapter() }
    private val dialog by lazy {
        initDialog()
    }

    private val deleteDialog by lazy {
        initDeleteDialog()
    }

    private val loadDialog by lazy {
        initLoadDialog()
    }

    private val mViewModel by lazy {
        setViewModel(this,FaceMgViewModel::class.java)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_face_manager)
        dialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_register_face,null,false)
        dialogBinding.activity = this
        dialogBinding.lifecycleOwner = this
        deleteDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_confirm_delete,null,false)
        deleteDialogBinding.activity = this
        deleteDialogBinding.viewModel = mViewModel
        deleteDialogBinding.lifecycleOwner = this
        loadDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_loading,null,false)
        loadDialogBinding.text = resources.getString(R.string.face_load_delete)
        loadDialogBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this
        mBinding.faceDelete.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP)
                onClickDeleteFace()
            false
        }

        initView()
        mViewModel.initFaceList()
    }

    private fun initView(){
        val layoutManager = GridLayoutManager(this,3)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        mViewModel.isShowDelete.observe(this, Observer {
            adapter.setShowDelete(it)
        })
        adapter.listener = {
            mViewModel.deleteCount.value = it
        }
        mViewModel.deleteSuccess.observe(this, Observer {
            if (it){
                adapter.remoteDeleteItem()
                if (mViewModel.isShowDelete.value == true){
                    onClickDeleteFace()
                    faceDelete.performClick()
                }
            }
        })

        mViewModel.newFace.observe(this, Observer {
            adapter.list.add(it)
            adapter.notifyItemInserted(adapter.list.size - 1)
        })

        mViewModel.faceList.observe(this, Observer {
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })

        mViewModel.isShowLoading.observe(this, Observer {
            GlobalScope.launch(Dispatchers.Main) {
                if (it){
                    loadDialog.show()
                }else{
                    loadDialog.hide()
                }
            }
        })
    }

    private fun initDialog():Dialog{
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(dialogBinding.root)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.hide()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    private fun initDeleteDialog():Dialog{
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(deleteDialogBinding.root)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20f,resources.displayMetrics).toInt()
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.hide()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    private fun initLoadDialog():Dialog{
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(loadDialogBinding.root)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.dismiss()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun startRegisterFaceFromCamera(view:View){
        startActivityForResult(Intent(this,RegisterFaceActivity::class.java),RegisterFaceActivity.QUEQUEST_CODE)
        cancelDialog()
    }

    fun startRegisterFaceFromPhoto(view:View){
        startActivityForResult(Intent(this,RegisterFaceActivity::class.java).apply {
            putExtra("usePhoto",true)
        },RegisterFaceActivity.QUEQUEST_CODE)
        cancelDialog()
    }

    fun cancelDialog(view:View? = null){
        dialog.dismiss()
    }

    fun showDialog(view:View? = null){
        if (adapter.itemCount >=5){
            toast(resources.getString(R.string.face_on_click_message))
        }else{
            dialog.show()
        }
    }

    fun onBack(view:View){
        onBackPressed()
    }

    override fun onBackPressed() {
        if (mViewModel.isShowDelete.value == true){
            mViewModel.isShowDelete.value = false
            mBinding.faceDelete.performClick()
        }else if(mViewModel.isShowDialog.value == true){
            cancelDialog()
        }else{
            super.onBackPressed()
        }
    }

    private fun onClickDeleteFace(){
        mViewModel.isShowDelete.value = !(mViewModel.isShowDelete.value?:true)
    }

    fun onClickAllSelect(view: View){
        mViewModel.isAllSelect.value = !(mViewModel.isAllSelect.value?:true)
        adapter.onAllSelect(mViewModel.isAllSelect.value?:true)
    }

    fun showDeleteDialog(view:View){
        if (mViewModel.deleteCount.value?:0 > 0){
            deleteDialog.show()
        }
    }

    fun confirmDelete(view:View){
        deleteDialog.dismiss()
        mViewModel.deleteFace(adapter.getDeleteItem())
    }

    fun cancelDelete(view:View){
        deleteDialog.dismiss()
    }

    fun getDeleteTitle(count:Int):String{
        if (count == 0)
            return "选择项目"
        else
            return "已选择 $count 张图片"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RegisterFaceActivity.QUEQUEST_CODE && resultCode == Activity.RESULT_OK){
            val newBean = data?.getParcelableExtra<FaceBean>("newFace")?:return
            mViewModel.newFace.value = newBean
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getDeleteDialog(count:Int):String{
        return "是否将 $count 张人脸认证图片删除"
    }
}
