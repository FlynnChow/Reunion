package com.example.reunion.base
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

abstract class BaseViewModel:ViewModel() {

    //用于记录异常
    val error by lazy { MutableLiveData<Throwable>() }

    /**
     * 各种协程方法，出现异常直接显示
     */
    protected fun launch(block:suspend ()->Unit) = viewModelScope.launch {
        try{
            block()
        }catch (e:Exception){
            error.value = e
        }
        runBlocking {  }
    }

    protected fun launchIO(block:suspend ()->Unit) = viewModelScope.launch(Dispatchers.IO) {
        try{
            block()
        }catch (e:Exception){
            error.value = e
        }
    }

    protected fun launchDefault(block:suspend ()->Unit) = viewModelScope.launch(Dispatchers.Default) {
        try{
            block()
        }catch (e:Exception){
            error.value = e
        }
    }

    protected fun launchUI(block:suspend ()->Unit) = viewModelScope.launch(Dispatchers.Main) {
        try{
            block()
        }catch (e:Exception){
            error.value = e
        }
    }

    /**
     * 各种协程方法，需要对异常处理
     */

    protected fun launch(block:suspend ()->Unit,throws:suspend (T:Throwable)->Unit) = viewModelScope.launch {
        try{
            block()
        }catch (e:Exception){
            throws.invoke(e)
        }
    }

    protected fun launchIO(block:suspend ()->Unit,throws:suspend (T:Throwable)->Unit) = viewModelScope.launch(Dispatchers.IO) {
        try{
            block()
        }catch (e:Exception){
            throws.invoke(e)
        }
    }

    protected fun launchDefault(block:suspend ()->Unit,throws:suspend (T:Throwable)->Unit) = viewModelScope.launch(Dispatchers.Default) {
        try{
            block()
        }catch (e:Exception){
            throws.invoke(e)
        }
    }

    protected fun launchUI(block:suspend ()->Unit,throws:suspend (T:Throwable)->Unit) = viewModelScope.launch(Dispatchers.Main) {
        try{
            block()
        }catch (e:Exception){
            throws.invoke(e)
        }
    }
}