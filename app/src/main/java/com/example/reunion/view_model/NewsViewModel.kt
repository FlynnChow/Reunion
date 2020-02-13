package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.remote_resource.NewsRemoteModel
import com.example.reunion.view.NewsContentFragment

class NewsViewModel: BaseViewModel() {
    private val remoteModel:NewsRemoteModel by lazy { NewsRemoteModel() }
    var contentTypeIndex = 0
    var currentIndex = 0
    val news:MutableLiveData<ArrayList<NewsBean.News>> = MutableLiveData(ArrayList())
    val isRefreshEnd:MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoadEnd:MutableLiveData<Boolean> = MutableLiveData(true)
    val clearList:MutableLiveData<Boolean> = MutableLiveData(false)
    private var page:Int = 0

    fun onLoadNews(view: View? = null){
        if (isLoadEnd.value == false)
            return
        if ((contentTypeIndex == NewsContentFragment.PUBLIC_WELFARE && page != 0)||page == 440){
            toast.value = MyApplication.app.resources.getString(R.string.load_all)
            isLoadEnd.value = true
            return
        }
        launch ({
            isLoadEnd.value = false
            val bean:NewsBean = when(contentTypeIndex){
                NewsContentFragment.HEALTHY->{
                    remoteModel.getHealthyNews(page)
                }
                NewsContentFragment.Child->{
                    remoteModel.getChildNews(page)
                }
                else->{
                    remoteModel.getPublicNews()
                }
            }
            when(bean.status){
                0->{
                    page+=40
                    for (news in (bean.result as NewsBean.Result).list!!){
                        news.src = news.src.replace(" ","")
                        news.src = news.src.replace("\n","")
                    }
                    news.value = (bean.result as NewsBean.Result).list!!
                }
            }
            isLoadEnd.value = true
        },{
            isLoadEnd.value = true
            toast.value = MyApplication.resource().getString(R.string.fail_load)
        })
    }

    fun onRefresh(view: View? = null){
        if (isRefreshEnd.value == false)
            return
        clearList.value = true
        page = 0
        launch ({
            isRefreshEnd.value = false
            val bean:NewsBean = when(contentTypeIndex){
                NewsContentFragment.HEALTHY->{
                    remoteModel.getHealthyNews(page)
                }
                NewsContentFragment.Child->{
                    remoteModel.getChildNews(page)
                }
                else->{
                    remoteModel.getPublicNews()
                }
            }
            when(bean.status){
                0->{
                    page+=40
                    news.value = (bean.result as NewsBean.Result).list!!
                }
            }
            isRefreshEnd.value = true
        },{
            isRefreshEnd.value = true
            toast.value = MyApplication.resource().getString(R.string.fail_refresh)
        })
    }
}