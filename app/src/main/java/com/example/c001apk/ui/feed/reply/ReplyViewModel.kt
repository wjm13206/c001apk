package com.example.c001apk.ui.feed.reply

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.logic.repository.NetworkRepo
import com.example.c001apk.util.Event
import com.example.c001apk.util.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val networkRepo: NetworkRepo
) : ViewModel() {

    var type: String? = null
    var rid: String? = null

    val toastText = MutableLiveData<Event<String>>()
    val closePage = MutableLiveData<Event<Boolean>>()
    val showCaptcha = MutableLiveData<Event<Bitmap?>>()

    val replyData = HashMap<String, String>()
    val createFeedData = HashMap<String, String?>()
    var requestValidateData = HashMap<String, String?>()

    fun onPostReply() {
        viewModelScope.launch {
            val response = networkRepo.postReply(replyData, rid ?: "", type ?: "reply")
            withContext(Dispatchers.IO) {
                response.onSuccess {
                    val body = it.body()
                    if (body != null) {
                        if (body.status == 1) {
                            closePage.postValue(Event(true))
                        } else if (body.status == -1) {
                            if (body.message == "err_request_captcha") {
                                getValidateCaptcha()
                            } else {
                                toastText.postValue(Event(body.message ?: "回复失败"))
                            }
                        } else {
                            toastText.postValue(Event(body.message ?: "回复失败"))
                        }
                    }
                }.onFailure {
                    toastText.postValue(Event(it.message ?: "网络错误"))
                }
            }
        }
    }

    fun onPostCreateFeed() {
        viewModelScope.launch {
            val response = networkRepo.postCreateFeed(createFeedData)
            withContext(Dispatchers.IO) {
                response.onSuccess {
                    val body = it.body()
                    if (body != null) {
                        if (body.status == 1) {
                            closePage.postValue(Event(true))
                        } else if (body.status == -1) {
                            if (body.message == "err_request_captcha") {
                                getValidateCaptcha()
                            } else {
                                toastText.postValue(Event(body.message ?: "发布失败"))
                            }
                        } else {
                            toastText.postValue(Event(body.message ?: "发布失败"))
                        }
                    }
                }.onFailure {
                    toastText.postValue(Event(it.message ?: "网络错误"))
                }
            }
        }
    }

    private fun getValidateCaptcha() {
        viewModelScope.launch {
            val response = networkRepo.getValidateCaptcha("/v6/account/getValidateCaptcha")
            withContext(Dispatchers.IO) {
                response.onSuccess {
                    val body = it.body()
                    if (body != null) {
                        showCaptcha.postValue(Event(PrefManager.getCaptchaBitmap(body.byteStream())))
                    }
                }
            }
        }
    }

    fun onPostRequestValidate() {
        viewModelScope.launch {
            val response = networkRepo.postRequestValidate(requestValidateData)
            withContext(Dispatchers.IO) {
                response.onSuccess {
                    val body = it.body()
                    if (body != null) {
                        if (body.status == 1) {
                            if (type == "createFeed")
                                onPostCreateFeed()
                            else
                                onPostReply()
                        } else {
                            toastText.postValue(Event(body.message ?: "验证失败"))
                        }
                    }
                }.onFailure {
                    toastText.postValue(Event(it.message ?: "网络错误"))
                }
            }
        }
    }

}