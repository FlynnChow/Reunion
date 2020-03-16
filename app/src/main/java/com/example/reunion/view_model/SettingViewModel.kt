package com.example.reunion.view_model

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.bean.FeedBack
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.repostory.local_resource.HomePageSt
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.SettingRemoteModel
import com.example.reunion.util.NormalUtil
import com.google.gson.Gson
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.lang.StringBuilder
import java.net.UnknownHostException

class SettingViewModel:BaseViewModel() {

    private val remoteModel: SettingRemoteModel by lazy { SettingRemoteModel() }
    companion object {
        val PAGE_START = 0
        val PAGE_ACCOUNT = 1//0<-
        val PAGE_ADVICE = 2//3<-
        val PAGE_HELP = 3//0<-
        val PAGE_HOME_MANAGER = 4//0<-
        val PAGE_NORMAL = 5//0<-
        val PAGE_MESSAGE = 6//0<-
    }

    var currentPage = 0

    val onBack = MutableLiveData<Boolean>()
    /**
     * 首页管理
     */
    val checkRecommend = MutableLiveData(true)
    val checkNearby = MutableLiveData(true)
    val checkNews = MutableLiveData(true)
    val checkFollw = MutableLiveData(true)

    fun onSelectRecommend(view: View) {
        checkRecommend.value = !checkRecommend.value!!
    }

    fun onSelectNearby(view: View) {
        checkNearby.value = !checkNearby.value!!
    }

    fun onSelectNews(view: View) {
        checkNews.value = !checkNews.value!!
    }

    fun onSelectFollw(view: View) {
        checkFollw.value = !checkFollw.value!!
    }

    fun getLabelColor(check: Boolean) =
        if (check) MyApplication.resource().getDrawable(R.drawable.viewpage_mg_bg_red)
        else MyApplication.resource().getDrawable(R.drawable.viewpage_mg_bg_white)

    fun getTextColor(check: Boolean) =
        if (check) MyApplication.resource().getColor(R.color.home_page_1)
        else MyApplication.resource().getColor(R.color.home_page_0)

    fun initHomePage() {
        checkRecommend.value = HomePageSt.instance.getRecommendStatus()
        checkNearby.value = HomePageSt.instance.getNearbyStatus()
        checkNews.value = HomePageSt.instance.getNewsStatus()
        checkFollw.value = HomePageSt.instance.getFollowStatus()
    }

    fun saveHomePage() {
        HomePageSt.instance.save(
            checkRecommend.value!!,
            checkNearby.value!!,
            checkNews.value!!,
            checkFollw.value!!
        )
    }

    //用户设置

    var path = MutableLiveData("")
    //目前需要保存的
    var realName = MutableLiveData("")
    var name = MutableLiveData("")
    var signature = MutableLiveData("")
    var province = MutableLiveData("")
    var city = MutableLiveData("")
    var district = MutableLiveData("")
    var sex = MutableLiveData(0)
    var birthday = MutableLiveData("")

    var header = MutableLiveData("")

    //目前只显示，不保存的
    var uid = MutableLiveData("")
    var qq = MutableLiveData("")
    var weChat = MutableLiveData("")
    var phone = MutableLiveData("")

    //反馈
    val fdUri = MutableLiveData<Uri>(null)
    val fdPath = MutableLiveData<String>()
    val fdContent = MutableLiveData("")
    val fdPhoneNumber = MutableLiveData("")

    fun getAreaString(province: String?, city: String?, district: String?): String {
        val builder = StringBuilder()
        if (province!=null&&province.isNotEmpty()) builder.append(province).append(" ")
        if (city!=null&&city.isNotEmpty()) builder.append(city).append(" ")
        if (district!=null&&district.isNotEmpty()) builder.append(district)
        return builder.toString()
    }

    fun getSexString(sex:Int) = when(sex){
        0 ->MyApplication.resource().getString(R.string.sex_0)
        1->MyApplication.resource().getString(R.string.sex_1)
        else->MyApplication.resource().getString(R.string.sex_2)
    }

    fun initUser() {
        val user = UserHelper.getUser()
        name.value = user?.uName
        realName.value = user?.uRealName
        header.value = user?.uHeadPortrait
        province.value = user?.uProvince
        city.value = user?.uCity
        district.value = user?.uDistrict
        sex.value = user?.uSex
        birthday.value = user?.uBirthday
        header.value = user?.uHeadPortrait
        signature.value = user?.uSignature

        uid.value = user?.uId
        qq.value = user?.uQq
        weChat.value = user?.uWeChat
        phone.value = user?.uTele
    }

    fun getSignatureString(signature:String?):String{
        if (signature == null)
            return ""
        if (signature.length>10){
            return signature.substring(0,8)+"…"
        }else
            return signature
    }

    fun uploadHeader(){
        if (!UserHelper.isLogin())
            return
        val file = File(path.value!!)
        val uploadBody = UploadRequestBody.getRequestBody(file,"header"){

        }
        val body = MultipartBody.Builder()
            .addFormDataPart("uId",UserHelper.getUser()?.uId?:"")
            .addFormDataPart("time",UserHelper.time.toString())
            .addFormDataPart("enCode", UserHelper.enCode)
            .addFormDataPart("headPhoto",file.name,uploadBody)
            .build()
        launch ({
            val bean = remoteModel.uploadHeader(body)
            when(bean.code){
                200 ->{
                    if (bean.data.isNotEmpty()){
                        header.value = bean.data
                        UserHelper.saveHeader(bean.data,bean.time,bean.enCode)
                    }
                }
                401 ->{
                    toast.value = "上传失败,UID不存在"
                }
                402 ->{
                    toast.value = "上传失败,校对失败"
                }
                403 ->{
                    toast.value = "服务器异常"
                }
                404 ->{
                    toast.value = "上传失败,图片过大"
                }
            }
        },{
            toast.value = "更新失败："+it.message
        })
    }

    fun saveUser(){
        val user = User.UserJson()
        user.uProvince = province.value?:""
        user.uCity = city.value?:""
        user.uDistrict = district.value?:""
        user.uSex = sex.value?:0
        user.uBirthday = birthday.value?:""
        user.uName = name.value?:""
        user.uRealName = realName.value?:""
        user.uSignature = signature.value?:""
        val jsonString = Gson().toJson(user)
        val body = MultipartBody.Builder()
            .addFormDataPart("uId",UserHelper.getUser()?.uId?:"")
            .addFormDataPart("time",UserHelper.time.toString())
            .addFormDataPart("enCode", UserHelper.enCode)
            .addFormDataPart("userJson",jsonString)
            .build()
        launch({
            val userBean = remoteModel.upInformation(body)
            when(userBean.code){
                200 ->{
                    if (userBean.data != null){
                        UserHelper.login(userBean)
                    }
                }
            }
        },{
            if (it is HttpException || it is UnknownHostException)
                toast.value = "未保存，无网络"
            toast.value = "异常："+it.message
        })
    }


    private var isAdaviceing = false
    //用户反馈
    fun onSendAdvice(){
        if (isAdaviceing){
            return
        }
        isAdaviceing = true
        if (fdContent.value == null||fdContent.value!!.isEmpty()){
            toast.value = "请写下您的反馈内容"
            isAdaviceing = false
            return
        }
        if(fdPhoneNumber.value == null||fdPhoneNumber.value!!.isNotEmpty()){
            if (!NormalUtil.isMobile(fdPhoneNumber.value)){
                toast.value = "请填写正确的电话号码"
                isAdaviceing = false
                return
            }
        }

        val feedBean = FeedBack()
        launch ({
            if (fdUri.value != null){
                val tempPath = PictureHelper.instance.obtainPathFromUri(fdUri.value!!)
                val comPath = PictureHelper.instance.compressImage(
                    MyApplication.app,tempPath)
                fdPath.value = comPath
            }
            feedBean.fContent = fdContent.value
            feedBean.fTele = fdPhoneNumber.value
            val bodyBuilder = MultipartBody.Builder()
            if (fdPath.value != null && fdPath.value != ""){
                val file = File(fdPath.value!!)
                val uploadBody = UploadRequestBody.getRequestBody(file,"image")
                bodyBuilder.addFormDataPart("file",file.name,uploadBody)
            }
            bodyBuilder.addFormDataPart("feedBackJson",Gson().toJson(feedBean))
            val result = remoteModel.insertFeedBack(bodyBuilder.build())
            when(result.code){
                200 ->{
                    toast.value = "反馈成功"
                    onBack.value = true
                    fdUri.value = null
                    fdContent.value = ""
                    fdPhoneNumber.value = ""
                }
                else ->{
                    toast.value = "反馈失败，请重新尝试"
                }
            }
            isAdaviceing = false
        },{
            toast.value = it.message
            isAdaviceing = false
        })
    }

    fun clearMessage(){
        launch {
            AppDataBase.instance.getImMessageDao().clearMessages(UserHelper.getUser()?.uId?:"")
            AppDataBase.instance.getIndexDao().clearIndex(UserHelper.getUser()?.uId?:"")
        }
    }

    fun clearSysMessage(){
        launch {
            AppDataBase.instance.getSysMessageDao().clearIndex(UserHelper.getUser()?.uId?:"")
        }
    }
}