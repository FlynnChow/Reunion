package com.example.reunion.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.ItemUserBinding
import com.example.reunion.repostory.bean.User
import com.example.reunion.util.StringDealerUtil
import com.lljjcoder.style.citylist.sortlistview.CharacterParser
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(
    private val listener:(String)->Unit
):RecyclerView.Adapter<BaseViewHolder<ItemUserBinding>>() {
    private val characterParser: CharacterParser = CharacterParser.getInstance()

    val users:ArrayList<User.Data> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemUserBinding> {
        val mBinding:ItemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_user,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemUserBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.user = users[position]

        if (position == 0||users[position -1].namePY[0]!=users[position].namePY[0]){
            mBinding.isFirst = true
            mBinding.text = users[position].namePY[0].toString()
        }else{
            mBinding.isFirst = false
        }

        mBinding.item.setOnClickListener {
            listener.invoke(users[position].uId)
        }

        mBinding.executePendingBindings()
    }


    private fun sortUsers(){
        users.sortWith(Comparator { o1, o2 ->
            val o1Str = o1?.namePY?:"#"
            val o2Str = o2?.namePY?:"#"
            if (o1Str[0] == '#'){
                    1
            }else{
                o1Str.compareTo(o2Str)
            }
        })
        notifyDataSetChanged()
    }

    fun addNewUser(users: ArrayList<User.Data>){
        for (index in 0 until users.size){
            val py = characterParser.getSelling(users[index].uName).toUpperCase(Locale.ROOT)
            if (py[0] in 'A'..'Z'){
                users[index].namePY = py
            }else{
                users[index].namePY = "#$py"
            }
        }
        this.users.addAll(users)
        sortUsers()
    }

    fun findPingYinPosition(str:String):Int{
        val selectChar = str[0]
        for (index in 0 until users.size){
            val target = users[index].namePY[0]
            if (target == selectChar)
                return index
        }
        return 0
    }

}